package renderer;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import geometries.api.*;
import geometries.impl.*;


/**
 * Integration tests for Camera rays generation and Geometries intersections.
 */
public class CameraIntersectionIntegrationTests {

    /** Camera resolution for integration tests */
    private static final int NX = 3;
    private static final int NY = 3;

    /**
     * Helper method to count total intersections through all pixels and assert the result.
     * @param camera        The camera to test
     * @param body          The geometry (sphere, plane, triangle)
     * @param expectedCount The expected number of intersection points
     * @param testName      The name of the test for error messages
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expectedCount, String testName) {
        int actualCount = 0;

        // Act: Iterate over all pixels in the 3x3 grid
        for (int i = 0; i < NY; ++i) {
            for (int j = 0; j < NX; ++j) {
                // Generate ray through the pixel
                Ray ray = camera.constructRay(j, i);

                // Calculate intersections between the ray and the body
                List<Point> intersections = body.findIntersections(ray);

                // Accumulate the count of intersection points
                if (intersections != null) {
                    actualCount += intersections.size();
                }
            }
        }

        // Assert: Compare actual total intersections to the expected count
        assertEquals(expectedCount, actualCount, testName + " failed: incorrect total intersections");
    }

    /**
     * Test integration of Camera rays with Sphere intersections.
     */
    @Test
    public void testCameraRaySphereIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setVpSize(3, 3)
                .setVpDistance(1)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setResolution(NX, NY);

        // TC01: Sphere r=1 (2 points) - Only the center pixel intersects the sphere twice
        Camera cam1 = builder.setLocation(new Point(0, 0, 0)).build();
        assertIntersectionsCount(cam1, new Sphere(new Point(0, 0, -3), 1), 2, "Sphere TC01");

        // TC02: Sphere r=2.5 (18 points) - All 9 pixels intersect twice
        Camera cam2 = builder.setLocation(new Point(0, 0, 0.5)).build();
        assertIntersectionsCount(cam2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "Sphere TC02");

        // TC03: Sphere r=2 (10 points) - Center pixel and adjacent pixels hit different parts
        assertIntersectionsCount(cam2, new Sphere(new Point(0, 0, -2), 2), 10, "Sphere TC03");

        // TC04: Sphere r=4 (9 points) - Camera is inside the sphere, each ray hits once
        assertIntersectionsCount(cam2, new Sphere(new Point(0, 0, -1), 4), 9, "Sphere TC04");

        // TC05: Sphere r=0.5 (0 points) - Sphere is behind the camera
        assertIntersectionsCount(cam1, new Sphere(new Point(0, 0, 1), 0.5), 0, "Sphere TC05");
    }

    /**
     * Test integration of Camera rays with Plane intersections.
     */
    @Test
    public void testCameraRayPlaneIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setVpDistance(1)
                .setResolution(NX, NY);

        // TC01: Plane parallel to View Plane (9 points)
        assertIntersectionsCount(builder.build(), new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "Plane TC01");

        // TC02: Plane slanted (9 points)
        assertIntersectionsCount(builder.build(), new Plane(new Point(0, 0, -2), new Vector(0, -0.5, 1)), 9, "Plane TC02");

        // TC03: Plane slanted strongly (6 points) - Upper row rays don't hit
        assertIntersectionsCount(builder.build(), new Plane(new Point(0, 0, -5), new Vector(0, -1, 1)), 6, "Plane TC03");
    }

    /**
     * Test integration of Camera rays with Triangle intersections.
     */
    @Test
    public void testCameraRayTriangleIntegration() {
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setVpDistance(1)
                .setResolution(NX, NY);

        // TC01: Small triangle (1 point) - Only center pixel hits
        assertIntersectionsCount(builder.build(), new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "Triangle TC01");

        // TC02: Tall triangle (2 points) - Center and top-middle hit
        assertIntersectionsCount(builder.build(), new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "Triangle TC02");
    }
}