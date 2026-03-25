package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link geometries.impl.Sphere}.
 * The tests verify the normal calculation for the sphere.
 */
class SphereTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect normal vector */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point center = new Point(0, 0, 0);
        double radius = 1.0;
        Sphere sphere = new Sphere(center, radius);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test normal at a point on the sphere surface
        // Using point (1, 0, 0) for a sphere centered at (0, 0, 0) with radius 1
        Point p = new Point(1, 0, 0);
        Vector n = sphere.getNormal(p);

        // Ensure method does not throw exception
        assertDoesNotThrow(() -> sphere.getNormal(p), "getNormal() threw unexpected exception");

        // Ensure normal is correct (should be vectored (1, 0, 0))
        assertEquals(new Vector(1, 0, 0), n, ERROR_NORMAL);

        // Ensure |n| = 1
        assertEquals(1, n.length(), DELTA, "Sphere normal is not a unit vector");

        // EP02: Test normal at another point on the surface
        // Using a Pythagorean triple (3,4,5) scaled to radius 1: (0.6, 0.8, 0)
        Point p2 = new Point(0.6, 0.8, 0);
        Vector n2 = sphere.getNormal(p2);
        assertEquals(new Vector(0.6, 0.8, 0), n2, ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);
        final String ERROR_SPHERE_INTERSECTION = "Sphere intersection algorithm is incorrect";

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        Ray ray1 = new Ray(new Point(-1, 0, 0), new Vector(1, 1, 0));
        assertNull(sphere.findIntersections(ray1), "Ray's line is outside the sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        List<Point> result2 = sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(3, 1, 0)));
        assertNotNull(result2, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result2.size(), ERROR_SPHERE_INTERSECTION);
        if (result2.get(0).distance(new Point(-1, 0, 0)) > result2.get(1).distance(new Point(-1, 0, 0)))
            result2 = List.of(result2.get(1), result2.get(0));
        assertEquals(List.of(p1, p2), result2, ERROR_SPHERE_INTERSECTION);

        // TC03: Ray starts inside the sphere (1 point)
        Ray ray3 = new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0));
        List<Point> result3 = sphere.findIntersections(ray3);
        assertNotNull(result3, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result3.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(new Point(2, 0, 0), result3.get(0), ERROR_SPHERE_INTERSECTION);

        // TC04: Ray starts after the sphere (0 points)
        Ray ray4 = new Ray(new Point(3, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(ray4), "Ray starts after the sphere");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 points)
        Ray ray11 = new Ray(new Point(1, -1, 0), new Vector(0, 1, 0));
        List<Point> result11 = sphere.findIntersections(ray11);
        assertNotNull(result11, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result11.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(new Point(1, 1, 0), result11.get(0), ERROR_SPHERE_INTERSECTION);

        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, -1, 0))), ERROR_SPHERE_INTERSECTION);

        // **** Group 2: Ray's line goes through the center
        // TC13: Ray starts before the sphere (2 points)
        Ray ray13 = new Ray(new Point(1, -2, 0), new Vector(0, 1, 0));
        List<Point> result13 = sphere.findIntersections(ray13);
        assertNotNull(result13, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result13.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(List.of(new Point(1, -1, 0), new Point(1, 1, 0)), result13, ERROR_SPHERE_INTERSECTION);

        // TC14: Ray starts at sphere and goes inside (1 points)
        Ray ray14 = new Ray(new Point(1, -1, 0), new Vector(0, 1, 0));
        assertEquals(1, sphere.findIntersections(ray14).size(), ERROR_SPHERE_INTERSECTION);

        // TC15: Ray starts inside (not center) (1 points)
        Ray ray15 = new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0));
        assertEquals(1, sphere.findIntersections(ray15).size(), ERROR_SPHERE_INTERSECTION);

        // TC16: Ray starts at the center (1 points)
        Ray ray16 = new Ray(new Point(1, 0, 0), new Vector(0, 1, 0));
        assertEquals(1, sphere.findIntersections(ray16).size(), ERROR_SPHERE_INTERSECTION);

        // TC17: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))), ERROR_SPHERE_INTERSECTION);

        // TC18: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))), ERROR_SPHERE_INTERSECTION);

        // **** Group 3: Ray's line is tangent to the sphere
        // TC19: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))), ERROR_SPHERE_INTERSECTION);
        // TC20: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))), ERROR_SPHERE_INTERSECTION);
        // TC21: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))), ERROR_SPHERE_INTERSECTION);

        // **** Group 4: Special cases
        // TC22: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(1, 0, 0))), ERROR_SPHERE_INTERSECTION);
    }
}
