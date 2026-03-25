package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.List;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;

/**
 * Represents a flat plane in 3D space.
 * The plane is defined by a point on the plane and a normal vector.
 */
public class Plane extends Geometry {
    /** A point on the plane used as a reference */
    private final Point _point;
    /** The normal vector perpendicular to the plane */
    private final Vector _normal;

    /**
     * Constructor to initialize a plane from three points on its surface.
     * The points must not be co-linear or co-located to define a unique plane.
     * * @param p1 First point
     * @param p2 Second point
     * @param p3 Third point
     * @throws IllegalArgumentException if points are co-linear or co-located
     */
    public Plane(Point p1, Point p2, Point p3) {
        this._point = p1;

        // Create two vectors from the three points
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        // The normal is the cross product of the two vectors.
        // If the points are co-linear, crossProduct will throw IllegalArgumentException (Zero Vector).
        this._normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructor to initialize a plane from a point and a normal vector.
     * The normal vector is automatically normalized.
     * * @param point  A point on the plane
     * @param normal The normal vector to the plane
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    /**
     * Returns the normal to the plane.
     * Since it is a flat plane, the normal is constant for all points.
     * * @param point A point on the plane (unused in this implementation)
     * @return The normalized normal vector to the plane
     */
    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /**
     * Getter for the reference point of the plane.
     * * @return The point on the plane
     */
    public Point getPoint() {
        return _point;
    }

    /**
     * Getter for the normal vector of the plane.
     * * @return The normalized normal vector
     */
    public Vector getNormal() {
        return _normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // 1. Calculate the denominator: n * v
        double nv = n.dotProduct(v);

        // If the ray is parallel to the plane (n * v == 0), no intersections.
        if (isZero(nv)) {
            return null;
        }

        // 2. Calculate the numerator: n * (Q0 - P0)
        Vector q0MinusP0;
        try {
            q0MinusP0 = _point.subtract(p0);
        } catch (IllegalArgumentException e) {
            // P0 is exactly on the plane's reference point Q0
            return null;
        }

        double nQ0MinusP0 = n.dotProduct(q0MinusP0);

        // 3. Calculate t - This is where 't' is defined!
        double t = alignZero(nQ0MinusP0 / nv);

        // 4. Return intersection only if t > 0
        // REFACTORING: Now 't' is recognized and passed to getPoint()
        return t <= 0 ? null : List.of(ray.getPoint(t));
    }
}