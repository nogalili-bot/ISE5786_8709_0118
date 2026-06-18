package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import primitives.BoundingBox;

import static primitives.Util.alignZero;
import static geometries.api.Intersectable.Intersection;

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
        Vector delta = new Vector(radius, radius, radius);
        this.boundingBox = new BoundingBox(
                center.add(delta.scale(-1)), // Minimum point (shifted back and left)
                center.add(delta)            // Maximum point (shifted forward and right)
        );
    }

    /**
     * Calculates the normal vector to the sphere's surface at a given point.
     * @param point The point on the sphere surface
     * @return The normalized normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    /**
     * Helper method to find intersections of a ray with the sphere.
     * @param ray The ray to intersect with the sphere
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point center = _center;
        double r = _radius;

        Vector u;
        try {
            u = center.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Case where the ray origin is at the center of the sphere
            return List.of(new Intersection(this, ray.getPoint(r)));
        }

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double rSquared = r * r;

        // If the distance from the center to the ray is greater than the radius, there are no intersections
        if (alignZero(dSquared - rSquared) >= 0) return null;

        double th = alignZero(Math.sqrt(rSquared - dSquared));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Return only points that are in the positive direction of the ray (t > 0)
        if (t1 > 0 && t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2)));
        if (t1 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)));
        if (t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t2)));

        return null;
    }
}