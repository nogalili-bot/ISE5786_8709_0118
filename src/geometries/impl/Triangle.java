package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import primitives.BoundingBox;

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
        // 1. Define unit vectors for the three main axes
        Vector axisX = new Vector(1, 0, 0);
        Vector axisY = new Vector(0, 1, 0);
        Vector axisZ = new Vector(0, 0, 1);

        // 2. Convert the points to vectors from the origin
        Vector v1 = p1.subtract(Point.ZERO);
        Vector v2 = p2.subtract(Point.ZERO);
        Vector v3 = p3.subtract(Point.ZERO);

        // 3. Extract X, Y, Z values using dotProduct (scalar product with unit vector returns projection on that axis)
        double x1 = v1.dotProduct(axisX), y1 = v1.dotProduct(axisY), z1 = v1.dotProduct(axisZ);
        double x2 = v2.dotProduct(axisX), y2 = v2.dotProduct(axisY), z2 = v2.dotProduct(axisZ);
        double x3 = v3.dotProduct(axisX), y3 = v3.dotProduct(axisY), z3 = v3.dotProduct(axisZ);

        // 4. Find the minimum and maximum for the box edges
        double minX = Math.min(x1, Math.min(x2, x3));
        double minY = Math.min(y1, Math.min(y2, y3));
        double minZ = Math.min(z1, Math.min(z2, z3));

        double maxX = Math.max(x1, Math.max(x2, x3));
        double maxY = Math.max(y1, Math.max(y2, y3));
        double maxZ = Math.max(z1, Math.max(z2, z3));

        // 5. Set the bounding box
        this.boundingBox = new BoundingBox(
                new Point(minX, minY, minZ),
                new Point(maxX, maxY, maxZ)
        );
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