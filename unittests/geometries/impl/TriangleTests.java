package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Unit tests for class {@link geometries.impl.Triangle}.
 * The tests verify the normal calculation for a triangle.
 */
class TriangleTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect normal vector */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p1 = new Point(0, 0, 1);
        Point p2 = new Point(1, 0, 1);
        Point p3 = new Point(0, 1, 1);
        Triangle triangle = new Triangle(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Test normal at a point inside the triangle
        Point pInside = new Point(0.25, 0.25, 1);

        // Ensure method does not throw exception
        assertDoesNotThrow(() -> triangle.getNormal(pInside), "getNormal() threw unexpected exception");

        Vector result = triangle.getNormal(pInside);

        // Ensure |n| = 1
        assertEquals(1, result.length(), DELTA, "Triangle normal is not a unit vector");

        // Ensure normal is correct (orthogonal to the plane of the triangle)
        // For points on Z=1 plane, normal should be (0,0,1) or (0,0,-1)
        assertTrue(result.equals(new Vector(0, 0, 1)) || result.equals(new Vector(0, 0, -1)),
                ERROR_NORMAL);

        // Ensure normal is orthogonal to at least one edge
        Vector edge = p2.subtract(p1);
        assertEquals(0d, result.dotProduct(edge), DELTA, "Normal is not orthogonal to the triangle edge");
    }
    /**
     * Test method for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(
                new Point(1, 0, 0),
                new Point(0, 1, 0),
                new Point(0, 0, 0)
        );
        final String ERROR_TRIANGLE_INTERSECTION = "Triangle intersection algorithm is incorrect";

        // ============ Equivalence Partitions Tests ==============

        // TC01: Inside triangle (1 point)
        Ray ray1 = new Ray(new Point(0.1, 0.1, -1), new Vector(0, 0, 1));
        List<Point> result1 = triangle.findIntersections(ray1);
        assertNotNull(result1, ERROR_TRIANGLE_INTERSECTION);
        assertEquals(1, result1.size(), ERROR_TRIANGLE_INTERSECTION);
        assertEquals(new Point(0.1, 0.1, 0), result1.get(0), ERROR_TRIANGLE_INTERSECTION);

        // TC02: Outside triangle - against edge (0 points)
        Ray ray2 = new Ray(new Point(1, 1, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray2), "Ray should be outside triangle against edge");

        // TC03: Outside triangle - against vertex (0 points)
        Ray ray3 = new Ray(new Point(-1, -1, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray3), "Ray should be outside triangle against vertex");

        // =============== Boundary Values Tests ==================

        // TC11: On edge (0 points)
        Ray ray11 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray11), "Point on edge should return null");

        // TC12: On vertex (0 points)
        Ray ray12 = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray12), "Point on vertex should return null");

        // TC13: On edge's continuation (0 points)
        Ray ray13 = new Ray(new Point(2, 0, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray13), "Point on edge continuation should return null");

        // Group: Cases inherited from Plane
        // TC14: Parallel ray
        Ray ray14 = new Ray(new Point(0, 0, -1), new Vector(1, 0, 0));
        assertNull(triangle.findIntersections(ray14), "Parallel ray should return null");

        // TC15: Ray starts at the triangle's plane
        Ray ray15 = new Ray(new Point(0.1, 0.1, 0), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray15), "Ray starting at the plane should return null");
    }
}
