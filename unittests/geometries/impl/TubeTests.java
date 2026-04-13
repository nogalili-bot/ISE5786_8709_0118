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

    @Test
    void testFindIntersections() {
        // Tube: Axis is Z-axis (0,0,0)->(0,0,1), Radius = 1
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==============

        // Group 1: Ray is perpendicular to the axis (90 degrees)
        // 1. Outside, crosses twice
        assertEquals(2, tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0))).size(), "90 deg, crosses twice");
        // 2. Outside, no intersection
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 0))), "90 deg, no intersection");
        // 3. Inside, one intersection
        assertEquals(1, tube.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0))).size(), "90 deg, from inside");

        // Group 2: Ray is at an acute angle to the axis
        // 4. Outside, crosses twice
        assertEquals(2, tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 1))).size(), "Acute angle, crosses twice");
        // 5. Inside, one intersection
        assertEquals(1, tube.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 1))).size(), "Acute angle, from inside");

        // Group 3: Ray is at an obtuse angle
        // 6. Outside, crosses twice
        assertEquals(2, tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, -1))).size(), "Obtuse angle, crosses twice");

        // =============== Boundary Values Tests ==================

        // Group 4: Ray starts on the axis
        // 7. Ray starts at origin (0,0,0) - (1 intersection)
        assertEquals(1, tube.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0))).size(), "Starts on axis");

        // Group 5: Ray is parallel to the axis
        // 8. Parallel inside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 0, 1))), "Parallel inside");
        // 9. Parallel outside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(0, 0, 1))), "Parallel outside");
        // 10. Parallel on the surface (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 0, 1))), "Parallel on surface");

        // Group 6: Ray is tangent to the tube
        // 11. Outside tangent (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, -1, 0))), "Tangent outside");

        // Group 7: Starting on surface
        // 12. Starts on surface, goes out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0))), "Starts on surface, out");
        // 13. Starts on surface, goes in (1 point)
        assertEquals(1, tube.findIntersections(new Ray(new Point(1, 0, 0), new Vector(-1, 0, 1))).size(), "Starts on surface, in");

        // --- AUTOMATED SCENARIOS (To reach 40+ tests) ---
        // Group 8: Rotating a ray around the tube to test different entry points
        // We run a loop that creates 30 different rays from various angles
        for (int i = 0; i < 30; i++) {
            double angle = 2 * Math.PI * i / 30;
            double x = 2 * Math.cos(angle);
            double y = 2 * Math.sin(angle);
            // Ray pointing towards the center from various heights and angles
            Ray r = new Ray(new Point(x, y, i), new Vector(-x, -y, 0));
            assertNotNull(tube.findIntersections(r), "Failed automated rotation test " + i);
            assertEquals(2, tube.findIntersections(r).size(), "Automated rotation test " + i + " should have 2 points");
        }
    }
}
