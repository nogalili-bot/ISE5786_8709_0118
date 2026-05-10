package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import static geometries.api.Intersectable.Intersection;

/**
 * Represents a triangle in 3D space.
 * Inherits from Polygon and is defined by three points.
 */
public class Triangle extends Polygon {

    /**
     * Constructor for Triangle.
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    /**
     * Helper method to find intersections of a ray with the triangle.
     * Uses the Möller–Trumbore intersection algorithm.
     * @param ray The ray to intersect with the triangle
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        Vector edge1 = p2.subtract(p1);
        Vector edge2 = p3.subtract(p1);

        Vector pvec;
        try {
            pvec = v.crossProduct(edge2);
        } catch (IllegalArgumentException e) {
            // Ray is parallel to the edge
            return null;
        }

        double det = edge1.dotProduct(pvec);
        // If determinant is near zero, ray lies in plane of triangle or is parallel
        if (isZero(det)) return null;

        double invDet = 1.0 / det;
        Vector tvec = p0.subtract(p1);

        double u = alignZero(tvec.dotProduct(pvec) * invDet);
        if (u <= 0 || u >= 1) return null;

        Vector qvec;
        try {
            qvec = tvec.crossProduct(edge1);
        } catch (IllegalArgumentException e) {
            return null;
        }

        double vParam = alignZero(v.dotProduct(qvec) * invDet);
        if (vParam <= 0 || u + vParam >= 1) return null;

        double t = alignZero(edge2.dotProduct(qvec) * invDet);

        // Return the intersection only if it's in the positive direction of the ray
        if (t > 0) {
            return List.of(new Intersection(this, ray.getPoint(t)));
        }

        return null;
    }
}