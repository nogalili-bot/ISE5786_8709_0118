package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link geometries.impl.Plane}.
 * The tests verify plane construction and normal calculation.
 */
class PlaneTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect normal vector */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";
    /** Error message for wrong exception thrown */
    private static final String ERROR_EXCEPTION = "ERROR: wrong exception thrown";

    /**
     * Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Correct plane construction with 3 non-collinear points
        assertDoesNotThrow(() -> new Plane(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0)),
                "Failed to construct a valid plane");

        // =============== Boundary Values Tests ==================

        // BV01: Two points are co-located (p1 and p2)
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(1, 1, 1), new Point(1, 1, 1), new Point(0, 1, 0)),
                "Constructed a plane with two identical points");

        // BV02: Three points are on the same line (collinear)
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                "Constructed a plane with collinear points");
    }

    /**
     * Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Vector)}.
     */
    @Test
    void testConstructorWithNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that the normal is normalized in the constructor
        Plane plane = new Plane(new Point(1, 2, 3), new Vector(2, 0, 0));
        assertEquals(1d, plane.getNormal(new Point(1, 2, 3)).length(), DELTA,
                "Plane normal should be normalized");
    }

    /**
     * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Point p1 = new Point(1, 0, 1);
        Point p2 = new Point(0, 1, 1);
        Plane plane = new Plane(p0, p1, p2);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Get normal at a point in the plane (not the reference point)
        Vector normal = plane.getNormal(new Point(0.5, 0.5, 1));
        assertEquals(1d, normal.length(), DELTA, "Normal length should be 1");
        // Check direction (Z axis normal for plane XY at z=1)
        assertTrue(normal.equals(new Vector(0, 0, 1)) || normal.equals(new Vector(0, 0, -1)),
                ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // BV01: Get normal at the reference point of the plane
        assertDoesNotThrow(() -> plane.getNormal(p0), "getNormal at reference point failed");
    }
}
