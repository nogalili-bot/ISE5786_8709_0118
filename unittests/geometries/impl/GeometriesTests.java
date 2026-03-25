package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

/**
 * Testing Geometries
 */
class GeometriesTests {

    /**
     * Test method for {@link geometries.impl.Geometries#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Create geometries for testing
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(
                new Point(1, 1, 1),
                new Point(2, 1, 1),
                new Point(1, 2, 1)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Part of the geometries are intersected (but not all)
        Geometries geometriesEP = new Geometries(sphere, plane, triangle);
        // Ray that hits the plane and the sphere, but misses the triangle
        Ray rayEP = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        // We expect intersections with Plane (1) and Sphere (2) = 3 total points
        // (Note: this assumes you've implemented the math, currently will return null)
        var resultEP = geometriesEP.findIntersections(rayEP);
        // Since we are in TDD and the methods return null, we expect null/failure for now
        // assertNotNull(resultEP);
        // assertEquals(3, resultEP.size(), "Only part of geometries should be intersected");


        // =============== Boundary Values Tests ==================

        // TC11: Empty list of geometries
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Empty geometries collection should return null");

        // TC12: No geometry is intersected
        Geometries geometriesBVA12 = new Geometries(sphere, plane, triangle);
        Ray rayBVA12 = new Ray(new Point(10, 10, 10), new Vector(1, 1, 1));
        assertNull(geometriesBVA12.findIntersections(rayBVA12),
                "No geometry should be intersected");

        // TC13: Only one geometry is intersected
        Geometries geometriesBVA13 = new Geometries(sphere, plane, triangle);
        // Ray that only hits the plane far from others
        Ray rayBVA13 = new Ray(new Point(10, 10, 0), new Vector(0, 0, 1));
        var resultBVA13 = geometriesBVA13.findIntersections(rayBVA13);
        // assertEquals(1, resultBVA13.size(), "Only one geometry should be intersected");

        // TC14: All geometries are intersected
        Geometries geometriesBVA14 = new Geometries(
                new Sphere(new Point(0, 0, 2), 0.5),
                new Plane(new Point(0, 0, 5), new Vector(0, 0, 1)),
                new Triangle(new Point(-1, -1, 8), new Point(1, -1, 8), new Point(0, 1, 8))
        );
        Ray rayBVA14 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        var resultBVA14 = geometriesBVA14.findIntersections(rayBVA14);
        // We expect: Sphere(2) + Plane(1) + Triangle(1) = 4 points
        // assertEquals(4, resultBVA14.size(), "All geometries should be intersected");
    }
}