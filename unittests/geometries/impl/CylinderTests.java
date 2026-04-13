package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for class {@link geometries.impl.Cylinder}.
 * The tests verify the normal calculation for the cylinder's shell and bases.
 */
class CylinderTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for incorrect normal vector */
    private static final String ERROR_NORMAL = "ERROR: getNormal() result is incorrect";

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        double radius = 1.0;
        double height = 2.0;
        Cylinder cylinder = new Cylinder(radius, axis, height);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on the shell (side) of the cylinder
        Point pShell = new Point(1, 0, 1);
        Vector nShell = cylinder.getNormal(pShell);
        assertEquals(new Vector(1, 0, 0), nShell, ERROR_NORMAL);
        assertEquals(1d, nShell.length(), DELTA, "Normal should be a unit vector");

        // EP02: Point on the first base (at the origin side, t=0)
        Point pBase1 = new Point(0.5, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(pBase1), ERROR_NORMAL);

        // EP03: Point on the second base (at the height side, t=height)
        Point pBase2 = new Point(0.5, 0, 2);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(pBase2), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Point at the center of the first base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)),
                "Normal at the center of the first base is incorrect");

        // BV02: Point at the center of the second base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)),
                "Normal at the center of the second base is incorrect");

        // BV03: Point on the edge (connection between shell and first base)
        // According to the requirement, we assume the point is on the surface.
        // On the edge, it can be considered part of the base.
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)),
                "Normal on the edge of the first base is incorrect");

        // BV04: Point on the edge (connection between shell and second base)
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)),
                "Normal on the edge of the second base is incorrect");
    }
    
}
