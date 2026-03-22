package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

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
}
