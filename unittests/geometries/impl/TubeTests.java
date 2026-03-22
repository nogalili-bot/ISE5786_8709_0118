package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link geometries.impl.Tube}.
 * The tests verify the normal calculation for an infinite tube.
 */
class TubeTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect normal vector */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        double radius = 1.0;
        Tube tube = new Tube(radius, axis);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is at some distance from the axis head (t > 0)
        Point p1 = new Point(1, 0, 2);
        Vector n1 = tube.getNormal(p1);

        assertDoesNotThrow(() -> tube.getNormal(p1), "getNormal() threw unexpected exception");
        assertEquals(new Vector(1, 0, 0), n1, ERROR_NORMAL);
        assertEquals(1d, n1.length(), DELTA, "Tube normal is not a unit vector");

        // EP02: Point is "behind" the axis head (t < 0)
        Point p2 = new Point(1, 0, 0);
        Vector n2 = tube.getNormal(p2);

        assertEquals(new Vector(1, 0, 0), n2, ERROR_NORMAL);
        assertEquals(1d, n2.length(), DELTA, "Tube normal is not a unit vector");

        // =============== Boundary Values Tests ==================

        // BV01: The projection of the point onto the axis is exactly the axis head (t = 0)
        // This is a special case where (P - P0) is orthogonal to the direction vector V.
        Point pHead = new Point(1, 0, 1);

        assertDoesNotThrow(() -> tube.getNormal(pHead),
                "getNormal() failed when point projection is at the axis head");

        Vector nHead = tube.getNormal(pHead);
        assertEquals(new Vector(1, 0, 0), nHead, "Normal at the axis head level is incorrect");
        assertEquals(1d, nHead.length(), DELTA, "Tube normal at the axis head is not a unit vector");
    }
}
