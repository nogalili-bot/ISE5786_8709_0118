package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link primitives.Ray}.
 * The tests verify construction and vector normalization.
 */
class RayTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for non-normalized vector */
    private static final String ERROR_NORMALIZED = "ERROR: Ray direction vector must be normalized";
    /** Error message for wrong origin point */
    private static final String ERROR_ORIGIN = "ERROR: Ray origin point is incorrect";

    /**
     * Test method for {@link primitives.Ray#Ray(primitives.Point, primitives.Vector)}.
     */
    @Test
    void testConstructor() {
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(2, 0, 0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that the constructor normalizes the direction vector
        Ray ray = new Ray(p, v);
        assertEquals(1d, ray.direction().length(), DELTA, ERROR_NORMALIZED);
        assertEquals(new Vector(1, 0, 0), ray.direction(), "Ray direction was not normalized correctly");

        // EP02: Test that origin point is stored correctly
        assertEquals(p, ray.origin(), ERROR_ORIGIN);
    }

    /**
     * Test method for {@link primitives.Ray#equals(Object)}.
     */
    @Test
    void testEquals() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 3);
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(1, 0, 0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test two rays with identical origin and direction
        assertEquals(new Ray(p1, v1), new Ray(p2, v2), "Two identical rays should be equal");

        // EP02: Test rays with different origin
        assertNotEquals(new Ray(p1, v1), new Ray(new Point(0, 0, 0), v1), "Rays with different origins should not be equal");

        // EP03: Test rays with different direction
        assertNotEquals(new Ray(p1, v1), new Ray(p1, new Vector(0, 1, 0)), "Rays with different directions should not be equal");
    }
}