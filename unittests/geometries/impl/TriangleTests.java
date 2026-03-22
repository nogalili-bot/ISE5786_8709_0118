package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

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
}
