package geometries.impl;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;
import java.util.List;

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

    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }
    /**
     * Finds the intersections of a ray with the triangle using the Möller–Trumbore algorithm.
     * This implementation avoids pre-calculating the plane and solves the intersection
     * directly using barycentric coordinates.
     * * @param ray The ray to intersect with the triangle.
     * @return A list containing the intersection point, or null if there are no intersections.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        Vector edge1 = p2.subtract(p1);
        Vector edge2 = p3.subtract(p1);

        // PROTECTION: Check if v is parallel to edge2 before crossProduct
        Vector pvec;
        try {
            pvec = v.crossProduct(edge2);
        } catch (IllegalArgumentException e) {
            // If crossProduct fails, the ray is parallel to the edge
            return null;
        }

        double det = edge1.dotProduct(pvec);

        // If determinant is near zero, ray lies in plane of triangle or is parallel
        if (isZero(det)) return null;

        double invDet = 1.0 / det;
        Vector tvec = p0.subtract(p1);

        double u = alignZero(tvec.dotProduct(pvec) * invDet);
        if (u <= 0 || u >= 1) return null;

        // PROTECTION: Check if tvec is parallel to edge1 before crossProduct
        Vector qvec;
        try {
            qvec = tvec.crossProduct(edge1);
        } catch (IllegalArgumentException e) {
            // If crossProduct fails, tvec and edge1 are collinear
            return null;
        }

        double vParam = alignZero(v.dotProduct(qvec) * invDet);
        if (vParam <= 0 || u + vParam >= 1) return null;

        double t = alignZero(edge2.dotProduct(qvec) * invDet);

        if (t > 0) {
            return List.of(ray.getPoint(t));
        }

        return null;
    }
}