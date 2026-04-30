package renderer;

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
        var intersections = _scene.geometries.findIntersections(ray);

        // If no intersections, return the background color
        if (intersections == null) {
            return _scene.background;
        }

        // Find the closest intersection point
        Point closestPoint = ray.findClosestPoint(intersections);

        // Calculate the color at that point
        return calcColor(closestPoint);
    }

    /**
     * Helper method to calculate the color at a specific point.
     * For now, it only considers ambient light.
     * @param intersection The point of intersection
     * @return The calculated color
     */
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }
}