package renderer;

import scene.Scene;
import primitives.Color;
import primitives.Ray;

/**
 * Abstract base class for ray tracing engines.
 */
abstract class RayTracerBase {
    /** The scene to be rendered */
    protected Scene _scene;

    /**
     * Constructor for RayTracerBase.
     * @param scene The scene to trace rays in
     */
    public RayTracerBase(Scene scene) {
        this._scene = scene;
    }

    /**
     * Traces a ray and calculates the color at the intersection point.
     * @param ray The ray to trace
     * @return The color observed by the ray
     */
    public abstract Color traceRay(Ray ray);
}