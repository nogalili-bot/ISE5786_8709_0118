package renderer;

import geometries.api.Intersectable.Intersection;
import scene.Scene;
import primitives.*;

import java.util.List;

/**
 * Simple implementation of a ray tracer.
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
        // Find intersections between the ray and the scene geometries
        var intersections = _scene.geometries.calcIntersections(ray);

        // If no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // Find the closest intersection
        Intersection closestPoint = ray.findClosestIntersection(intersections);

        // Calculate the color at that intersection point
        return calcColor(closestPoint);
    }

    /**
     * Helper method to calculate the color at a specific intersection point.
     * Calculated as: Color = Emission + kD * AmbientLight
     * @param intersection The intersection data (point, geometry, and material)
     * @return The calculated color
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .add(intersection.geometry.getEmission());
    //            .add(_scene.ambientLight.getIntensity().scale(intersection.material.kD));
    }
}