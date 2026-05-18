package renderer;

import geometries.api.Intersectable.Intersection;
import scene.Scene;
import primitives.*;
import lighting.LightSource;

/**
 * Simple implementation of a ray tracer that computes color using the Phong reflection model.
 */
class SimpleRayTracer extends RayTracerBase {

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
     * Helper method to calculate the color at a specific intersection point.
     * Color = Emission + Ambient + Local Effects (Diffuse + Specular)
     * @param intersection The intersection data
     * @param ray          The ray that caused the intersection
     * @return The calculated color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return _scene.ambientLight.getIntensity()
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection, ray));
    }

    /**
     * Calculates the local effects (Diffuse and Specular reflection) of all light sources.
     * @param intersection The intersection data
     * @param ray          The ray from the camera
     * @return The combined color components of local effects
     */
    private Color calcLocalEffects(Intersection intersection, Ray ray) {
        Vector v = ray.direction();
        Vector n = intersection.geometry.getNormal(intersection.point);
        double nv = n.dotProduct(v);

        if (Util.isZero(nv)) {
            return Color.BLACK;
        }

        Material material = intersection.geometry.getMaterial();
        Color color = Color.BLACK;

        // Loop through all light sources in the scene
        for (LightSource lightSource : _scene.lights) {
            Vector l = lightSource.getL(intersection.point);
            double nl = n.dotProduct(l);

            // Check that the light and the camera are on the same side of the surface
            if (nl * nv > 0) {
                Color iL = lightSource.getIntensity(intersection.point);
                color = color.add(
                        calcDiffuse(material.kD, nl, iL),
                        calcSpecular(material.kS, l, n, nl, v, material.nShininess, iL)
                );
            }
        }
        return color;
    }

    /**
     * Calculates the Diffuse reflection component.
     * @param kd  Diffuse coefficient
     * @param nl  Dot product of normal and light direction
     * @param iL  Light intensity at the point
     * @return The diffuse color component
     */
    private Color calcDiffuse(Double3 kd, double nl, Color iL) {
        double factor = Math.abs(nl);
        return iL.scale(kd.scale(factor));
    }

    /**
     * Calculates the Specular reflection component.
     * @param ks         Specular coefficient
     * @param l          Light direction vector
     * @param n          Normal vector
     * @param nl         Dot product of normal and light direction
     * @param v          View direction vector
     * @param nShininess Shininess factor
     * @param iL         Light intensity at the point
     * @return The specular color component
     */
    private Color calcSpecular(Double3 ks, Vector l, Vector n, double nl, Vector v, int nShininess, Color iL) {
        // r = l - 2 * (l * n) * n
        Vector r = l.subtract(n.scale(2 * nl)).normalize();

        // Target: max(0, -v * r)^nShininess
        double minusVr = v.scale(-1).dotProduct(r);

        if (minusVr <= 0) {
            return Color.BLACK;
        }

        double factor = Math.pow(minusVr, nShininess);
        return iL.scale(ks.scale(factor));
    }
}