package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link primitives.Point}.
 * The tests verify distance calculations, vector addition, and subtraction.
 */
class PointTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect distance calculation */
    private static final String ERROR_DISTANCE = "ERROR: distance() result is incorrect";
    /** Error message for incorrect squared distance calculation */
    private static final String ERROR_DISTANCE_SQUARED = "ERROR: distanceSquared() result is incorrect";
    /** Error message for incorrect point addition */
    private static final String ERROR_ADD = "ERROR: add() result is incorrect";
    /** Error message for incorrect vector subtraction */
    private static final String ERROR_SUBTRACT = "ERROR: subtract() result is incorrect";

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 6);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular subtraction between two points
        assertEquals(new Vector(1, 2, 3), p2.subtract(p1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================
        // BV01: Test subtraction resulting in the zero vector
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
                "Subtracting a point from itself should throw an exception (Zero Vector)");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        Point p1 = new Point(1, 2, 3);
        Vector v1 = new Vector(-1, -2, -3);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular addition of a vector to a point
        assertEquals(new Point(2, 4, 6), p1.add(new Vector(1, 2, 3)), ERROR_ADD);

        // =============== Boundary Values Tests ==================
        // BV01: Test addition resulting in the origin point (ZERO)
        assertEquals(Point.ZERO, p1.add(v1), "Addition to ZERO point failed");
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 6);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular squared distance between two points
        assertEquals(14d, p1.distanceSquared(p2), DELTA, ERROR_DISTANCE_SQUARED);

        // =============== Boundary Values Tests ==================
        // BV01: Test squared distance from a point to itself (should be 0)
        assertEquals(0d, p1.distanceSquared(p1), DELTA, "Distance squared to self should be 0");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 8);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular distance between two points
        assertEquals(5d, p1.distance(p2), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================
        // BV01: Test distance from a point to itself (should be 0)
        assertEquals(0d, p1.distance(p1), DELTA, "Distance to self should be 0");
    }
}