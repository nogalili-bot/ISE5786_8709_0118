package geometries.impl;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.List;

import static primitives.Util.alignZero;

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

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point center = _center;
        double r = _radius;

        // Vector from ray origin to sphere center
        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // If p0 is the center, the intersection is at distance r
            return List.of(ray.getPoint(r));
        }

        // Projection of u onto the ray direction
        double tm = alignZero(v.dotProduct(u));

        // Distance from center to the ray (squared)
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double rSquared = r * r;

        // If d > r, the ray misses the sphere
        if (alignZero(dSquared - rSquared) >= 0) {
            return null;
        }

        // Half of the chord length (th)
        double th = alignZero(Math.sqrt(rSquared - dSquared));

        // Calculate the two distances to intersection points
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // REFACTORING: Use getPoint(t) to safely calculate intersection points.
        // We only return points where t > 0 (points in the ray's direction).
        if (t1 > 0 && t2 > 0) {
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }
        if (t1 > 0) {
            return List.of(ray.getPoint(t1));
        }
        if (t2 > 0) {
            return List.of(ray.getPoint(t2));
        }
        return null;
    }
}