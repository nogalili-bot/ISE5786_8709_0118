package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
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
    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
    Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
    final String ERROR_PLANE_INTERSECTION = "Plane intersection algorithm is incorrect";

    // ============ Equivalence Partitions Tests ==============

    // TC01: Ray intersects the plane (1 point)
    Ray ray1 = new Ray(new Point(0, 0, 0), new Vector(1, 1, 1));
    List<Point> result1 = plane.findIntersections(ray1);
    assertNotNull(result1, ERROR_PLANE_INTERSECTION);
    assertEquals(1, result1.size(), ERROR_PLANE_INTERSECTION);
    assertEquals(new Point(1, 1, 1), result1.get(0), ERROR_PLANE_INTERSECTION);

    // TC02: Ray does not intersect the plane (0 points)
    Ray ray2 = new Ray(new Point(0, 0, 2), new Vector(1, 1, 1));
    assertNull(plane.findIntersections(ray2), "Ray should not intersect the plane");

    // =============== Boundary Values Tests ==================

    // **** Group: Ray is parallel to the plane
    // TC11: Ray included in the plane
    Ray ray11 = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));
    assertNull(plane.findIntersections(ray11), "Ray included in the plane should return null");

    // TC12: Ray parallel to the plane but not included
    Ray ray12 = new Ray(new Point(1, 1, 2), new Vector(1, 0, 0));
    assertNull(plane.findIntersections(ray12), "Parallel ray should return null");

    // **** Group: Ray is orthogonal to the plane
    // TC13: Ray starts before the plane (1 point)
    Ray ray13 = new Ray(new Point(1, 1, 0), new Vector(0, 0, 1));
    List<Point> result13 = plane.findIntersections(ray13);
    assertNotNull(result13, ERROR_PLANE_INTERSECTION);
    assertEquals(1, result13.size(), ERROR_PLANE_INTERSECTION);
    assertEquals(new Point(1, 1, 1), result13.get(0), ERROR_PLANE_INTERSECTION);

    // TC14: Ray starts in the plane
    Ray ray14 = new Ray(new Point(1, 1, 1), new Vector(0, 0, 1));
    assertNull(plane.findIntersections(ray14), "Ray starting in the plane should return null");

    // TC15: Ray starts after the plane
    Ray ray15 = new Ray(new Point(1, 1, 2), new Vector(0, 0, 1));
    assertNull(plane.findIntersections(ray15), "Ray starting after the plane should return null");

    // **** Group: Special cases
    // TC16: Ray begins at the plane (not orthogonal/parallel)
    Ray ray16 = new Ray(new Point(2, 2, 1), new Vector(1, 1, 1));
    assertNull(plane.findIntersections(ray16), "Ray starting at the plane");

    // TC17: Ray begins at the reference point of the plane
    Ray ray17 = new Ray(plane.getPoint(), new Vector(1, 1, 1));
    assertNull(plane.findIntersections(ray17), "Ray starting at the reference point");
    }
}
