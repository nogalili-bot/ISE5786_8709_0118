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
     * Calculates the unit normal vector to the sphere's surface at a specified point.
     * For a sphere, the normal is simply the direction from the center to the point.
     * * @param point A point on the surface of the sphere.
     * @return A normalized Vector perpendicular to the sphere's surface at the given point.
     */
    @Override
    public Vector getNormal(Point point) {
        // The normal vector is (Point - Center) normalized
        return point.subtract(_center).normalize();
    }
}