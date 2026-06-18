package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import static geometries.api.Intersectable.Intersection;

/**
 * Represents a flat plane in 3D space.
 * The plane is defined by a point on the plane and a normal vector.
 */
public class Plane extends Geometry {
    private final Point _point;
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points on its surface.
     * @param p1 First point
     * @param p2 Second point
     * @param p3 Third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        this._point = p1;
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        this._normal = v1.crossProduct(v2).normalize();
        this.boundingBox = null;
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * @param point  A point on the plane
     * @param normal The normal vector to the plane
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /** @return The point on the plane */
    public Point getPoint() {
        return _point;
    }

    /** @return The normalized normal vector */
    public Vector getNormal() {
        return _normal;
    }

    /**
     * Helper method to find intersections of a ray with the plane.
     * @param ray The ray to intersect with the plane
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // Calculate the denominator: n * v
        double nv = n.dotProduct(v);

        // If the ray is parallel to the plane, no intersections
        if (isZero(nv)) return null;

        Vector q0MinusP0;
        try {
            q0MinusP0 = _point.subtract(p0);
        } catch (IllegalArgumentException e) {
            // Ray origin is exactly on the plane's reference point
            return null;
        }

        double nQ0MinusP0 = n.dotProduct(q0MinusP0);
        double t = alignZero(nQ0MinusP0 / nv);

        // Return intersection only if t > 0
        return t <= 0 ? null : List.of(new Intersection(this, ray.getPoint(t)));
    }
}