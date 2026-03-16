package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 * Inherits radius from RadialGeometry.
 */
public class Sphere extends RadialGeometry {
    private final Point _center;

    /**
     * Constructor for Sphere.
     * @param center the center point
     * @param radius the radius value
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this._center = center;
    }

    /**
     * Returns the normal to the sphere.
     * Note: Current implementation returns null.
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}