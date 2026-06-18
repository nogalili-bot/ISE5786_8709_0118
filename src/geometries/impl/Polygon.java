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
 * Represents a convex polygon in 3D space.
 */
public class Polygon extends Geometry {
    protected final List<Point> _vertices;
    protected final Plane       _plane;
    private final   int         _size;

    /**
     * Constructor for Polygon.
     * Validates that the vertices form a convex polygon on a single plane.
     * @param vertices vertices of the polygon in order
     * @throws IllegalArgumentException if vertices are not valid
     */

    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");

        _vertices = List.of(vertices);
        _size     = vertices.length;

        // Generate the plane from the first three points
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (vertices.length == 3) return; // A triangle is always convex and planar

        Vector n = _plane.getNormal();

        // 1. Check that all vertices are on the same plane
        for (int i = 3; i < vertices.length; ++i)
            if (!isZero(n.dotProduct(vertices[i].subtract(vertices[0]))))
                throw new IllegalArgumentException("All vertices must be on the same plane");

        // 2. Check the order of vertices and convexity
        // We calculate the cross product of each two consecutive edges.
        // All resulting vectors must point to the same side of the plane.
        Vector edge1 = vertices[vertices.length - 1].subtract(vertices[vertices.length - 2]);
        Vector edge2 = vertices[0].subtract(vertices[vertices.length - 1]);

        // Cross product of first two edges
        Vector cp = edge1.crossProduct(edge2);

        // Check if the first cross product is in the same direction as the normal
        boolean positive = n.dotProduct(cp) > 0;

        for (int i = 1; i < vertices.length; ++i) {
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            cp = edge1.crossProduct(edge2);

            // If the direction changes relative to the normal, the order is wrong or not convex
            if (isZero(n.dotProduct(cp)) || (n.dotProduct(cp) > 0) != positive)
                throw new IllegalArgumentException("Vertices must be in correct order and form a convex polygon");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // First, check if the ray intersects the plane containing the polygon
        var planeIntersections = _plane.calcIntersections(ray);
        if (planeIntersections == null) return null;

        Point  p0 = ray.origin();
        Vector v  = ray.direction();

        // Vectors from ray origin to vertices
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);

        // Calculate the first sign
        Vector n1 = v1.crossProduct(v2).normalize();
        double s1 = alignZero(v.dotProduct(n1));
        if (isZero(s1)) return null;

        boolean positive = s1 > 0;

        // Iterate over all edges and check the sign of the dot product
        for (int i = 1; i < _size; i++) {
            v1 = v2;
            v2 = _vertices.get((i + 1) % _size).subtract(p0);
            Vector ni = v1.crossProduct(v2).normalize();
            double si = alignZero(v.dotProduct(ni));

            // If the point is outside one of the edges, it's outside the polygon
            if (isZero(si) || (si > 0) != positive) return null;
        }

        // Return the intersection point with this polygon as the associated geometry
        return List.of(new Intersection(this, planeIntersections.get(0).point));
    }
}