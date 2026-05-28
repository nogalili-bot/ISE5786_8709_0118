package renderer;

import geometries.api.Intersectable.Intersection;
import scene.Scene;
import primitives.*;
import lighting.LightSource;

/**
 * Simple implementation of a ray tracer that computes color using the Phong reflection model,
 * supporting shadows, transparency, and reflection recursions.
 */
class SimpleRayTracer extends RayTracerBase {

    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructor for SimpleRayTracer.
     * @param scene The scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);

        if (intersections == null) {
            return _scene.background;
        }

        Intersection closestPoint = ray.findClosestIntersection(intersections);
        return calcColor(closestPoint, ray);
    }

    /**
     * Internal entry point for calculating color with initial level and attenuation factor.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return calcColor(intersection, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(_scene.ambientLight.getIntensity());
    }

    /**
     * Recursive method to calculate the color at a specific intersection point.
     * Color = Emission + Local Effects + Global Effects (Reflection + Refraction)
     */
    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = intersection.geometry.getEmission()
                .add(calcLocalEffects(intersection, ray, k));

        return level == 1 ? color : color.add(calcGlobalEffects(intersection, ray, level, k));
    }

    /**
     * Calculates the global effects (Reflection and Refraction) recursively.
     */
    private Color calcGlobalEffects(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = Color.BLACK;
        Material material = intersection.geometry.getMaterial();
        Vector n = intersection.geometry.getNormal(intersection.point);

        // Calculate Reflection
        Double3 kkr = k.product(material.kR);
        if (kkr.isGreaterThan(MIN_CALC_COLOR_K)) { // מעודכן!
            Ray reflectedRay = constructReflectedRay(intersection.point, ray.direction(), n);
            Intersection closestIntersection = findCloseIntersection(reflectedRay);
            if (closestIntersection != null) {
                color = color.add(calcColor(closestIntersection, reflectedRay, level - 1, kkr).scale(material.kR));
            } else {
                color = color.add(_scene.background.scale(material.kR));
            }
        }

        // Calculate Refraction/Transparency
        Double3 kkt = k.product(material.kT);
        if (kkt.isGreaterThan(MIN_CALC_COLOR_K)) { // מעודכן!
            Ray refractedRay = constructRefractedRay(intersection.point, ray.direction(), n);
            Intersection closestIntersection = findCloseIntersection(refractedRay);
            if (closestIntersection != null) {
                color = color.add(calcColor(closestIntersection, refractedRay, level - 1, kkt).scale(material.kT));
            } else {
                color = color.add(_scene.background.scale(material.kT));
            }
        }

        return color;
    }

    /**
     * Calculates the local effects (Diffuse and Specular reflection) of all light sources,
     * taking transparency shadows into account.
     */
    private Color calcLocalEffects(Intersection intersection, Ray ray, Double3 k) {
        Vector v = ray.direction();
        Vector n = intersection.geometry.getNormal(intersection.point);
        double nv = n.dotProduct(v);

        if (Util.isZero(nv)) {
            return Color.BLACK;
        }

        Material material = intersection.geometry.getMaterial();
        Color color = Color.BLACK;

        for (LightSource lightSource : _scene.lights) {
            Vector l = lightSource.getL(intersection.point);
            double nl = n.dotProduct(l);

            if (nl * nv > 0) { // Light and camera are on the same side
                Double3 ktr = transparency(intersection, lightSource, l, n);
                Double3 kKtr = ktr.product(k);

                if (kKtr.isGreaterThan(MIN_CALC_COLOR_K)) { // מעודכן!
                    Color iL = lightSource.getIntensity(intersection.point).scale(ktr);
                    color = color.add(
                            calcDiffuse(material.kD, nl, iL),
                            calcSpecular(material.kS, l, n, nl, v, material.nShininess, iL)
                    );
                }
            }
        }
        return color;
    }

    /**
     * Calculates the transparency factor (shadow attenuation) from a light source to a point.
     * Supports partial shadows from transparent objects.
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource, Vector l, Vector n) {
        Vector lightDirection = l.scale(-1); // from point to light source
        Ray shadowRay = new Ray(intersection.point, lightDirection, n);

        var intersections = _scene.geometries.calcIntersections(shadowRay);
        if (intersections == null) {
            return Double3.ONE; // Completely unshaded
        }

        double distance = lightSource.getDistance(intersection.point);
        Double3 ktr = Double3.ONE;

        for (Intersection geoTest : intersections) {
            if (geoTest.point.distance(intersection.point) < distance) {
                ktr = ktr.product(geoTest.geometry.getMaterial().kT);

                // If it becomes completely opaque or too dark to pass light
                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO; // Completely shaded
                }
            }
        }
        return ktr;
    }

    /**
     * Constructs a reflected ray on a surface according to specular reflection rules.
     */
    private Ray constructReflectedRay(Point point, Vector v, Vector n) {
        // r = v - 2 * (v * n) * n
        double vn = v.dotProduct(n);
        if (Util.isZero(vn)) {
            return new Ray(point, v, n);
        }
        Vector r = v.subtract(n.scale(2 * vn));
        return new Ray(point, r, n);
    }

    /**
     * Constructs a refracted ray (assuming flat index of refraction, direction stays the same).
     */
    private Ray constructRefractedRay(Point point, Vector v, Vector n) {
        return new Ray(point, v, n);
    }

    /**
     * Helper to find the closest intersection for a secondary ray.
     */
    private Intersection findCloseIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        return ray.findClosestIntersection(intersections);
    }

    private Color calcDiffuse(Double3 kd, double nl, Color iL) {
        double factor = Math.abs(nl);
        return iL.scale(kd.scale(factor));
    }

    private Color calcSpecular(Double3 ks, Vector l, Vector n, double nl, Vector v, int nShininess, Color iL) {
        Vector r = l.subtract(n.scale(2 * nl)).normalize();
        double minusVr = v.scale(-1).dotProduct(r);
        if (minusVr <= 0) {
            return Color.BLACK;
        }
        double factor = Math.pow(minusVr, nShininess);
        return iL.scale(ks.scale(factor));
    }
}