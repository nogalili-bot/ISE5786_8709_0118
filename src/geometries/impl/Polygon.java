package geometries.impl;

import primitives.*;

import static primitives.Util.isZero;

import java.util.List;

import geometries.api.Geometry;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
   /** Ordered list of polygon vertices */
   protected final List<Point> _vertices;
   /** Plane containing the polygon */
   protected final Plane       _plane;
   /** Number of vertices */
   private final int           _size;

   /**
    * Constructs a convex polygon from ordered vertices.
    * <p>
    * The vertices must:
    * </p>
    * <ul>
    * <li>Contain at least three points</li>
    * <li>Be ordered along the polygon edge path</li>
    * <li>Lie in the same plane</li>
    * <li>Form a convex polygon</li>
    * </ul>
    * @param  vertices                 polygon vertices in edge order
    * @throws IllegalArgumentException if the vertices do not form a valid convex
    *                                  polygon
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      _vertices = List.of(vertices);
      _size     = vertices.length;

      // Create the supporting plane using the first three vertices.
      // The plane stores the constant normal of the polygon.
      _plane    = new Plane(vertices[0], vertices[1], vertices[2]);
      if (_size == 3) return; // no need for more tests for a Triangle

      Vector  n        = _plane.getNormal(vertices[0]);
      // Subtracting identical vertices would create a zero vector (illegal)
      Vector  edge1    = vertices[_size - 1].subtract(vertices[_size - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[_size - 1]);

      // Cross product of consecutive edges determines orientation.
      // All edge pairs must produce the same sign relative to the normal,
      // otherwise the polygon is concave or vertices are unordered.
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < _size; ++i) {
         // Test that the point is in the same plane as calculated originally
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
         // Test the consequent edges have
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }

   @Override
   public Vector getNormal(Point point) { return _plane.getNormal(point); }

    /**
     * Finds the intersections of a ray with the polygon.
     * @param ray The ray to intersect with the polygon.
     * @return A list containing the intersection point, or null if there are no intersections.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        // Step 1: Check for intersection with the plane containing the polygon
        List<Point> intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Step 2: Verify if the intersection point is inside the polygon boundaries.
        // We use the algorithm of checking if the ray's direction is within the
        // pyramid formed by the ray's origin and the polygon's edges.

        int size = _vertices.size();

        // Vectors from the ray's origin to the polygon vertices
        Vector v1 = _vertices.get(size - 1).subtract(p0);
        Vector v2 = _vertices.get(0).subtract(p0);

        // Calculate the first normal (to the side-plane formed by the edge and the ray)
        // and its dot product with the ray's direction.
        Vector n = v1.crossProduct(v2).normalize();
        double s1 = primitives.Util.alignZero(v.dotProduct(n));

        // If the dot product is zero, the ray hits the edge or vertex boundary
        if (isZero(s1)) return null;

        // Iterate through all other edges to ensure consistent orientation (same sign)
        for (int i = 1; i < size; i++) {
            v1 = v2;
            v2 = _vertices.get(i).subtract(p0);
            n = v1.crossProduct(v2).normalize();
            double s2 = primitives.Util.alignZero(v.dotProduct(n));

            // If the sign changes or is zero, the point is outside or on the boundary
            if (isZero(s2) || (s1 > 0) != (s2 > 0)) return null;
        }

        // All cross products had the same sign, meaning the point is inside
        return intersections;
    }
}
