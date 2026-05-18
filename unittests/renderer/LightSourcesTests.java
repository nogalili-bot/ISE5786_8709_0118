package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.*;
import primitives.*;

/**
 * Unit tests for Light sources (DirectionalLight, PointLight, SpotLight).
 * Testing getL() and getIntensity() methods directly.
 */
class LightSourcesTests {

    /**
     * Test method for {@link lighting.DirectionalLight}.
     */
    @Test
    void testDirectionalLight() {
        Vector direction = new Vector(1, 1, -1);
        Color intensity = new Color(200, 100, 50);
        DirectionalLight light = new DirectionalLight(intensity, direction);
        Point p = new Point(1, 2, 3);

        // ================= Paragraph: getL =================
        // TC01: Test that getL returns the constant normalized direction vector
        Vector l = light.getL(p);
        assertNotNull(l, "getL() should not return null for DirectionalLight");
        assertEquals(direction.normalize(), l, "getL() direction is incorrect");
        // Check that the returned vector is normalized (length is 1)
        assertEquals(1.0, l.length(), 0.00001, "getL() vector must be normalized");

        // ================= Paragraph: getIntensity =================
        // TC02: Test that intensity remains constant regardless of the point position
        assertEquals(intensity, light.getIntensity(p), "getIntensity() should be constant for DirectionalLight");
    }

    /**
     * Test method for {@link lighting.PointLight}.
     */
    @Test
    void testPointLight() {
        Point position = new Point(0, 0, 0);
        Color intensity = new Color(100, 100, 100);

        // kC = 1, kL = 0.1, kQ = 0.02
        PointLight light = new PointLight(intensity, position).setKc(1).setKl(0.1).setKq(0.02);

        // Point at distance 5 from the light source: (5, 0, 0)
        Point p = new Point(5, 0, 0);

        // ================= Paragraph: getL =================
        // TC01: Test getL vector direction and normalization from source to point
        Vector l = light.getL(p);
        assertNotNull(l, "getL() should not return null");
        assertEquals(new Vector(1, 0, 0), l, "getL() direction is incorrect");
        assertEquals(1.0, l.length(), 0.00001, "getL() vector must be normalized");

        // TC02: Boundary values - point is exactly at the light source position
        assertNull(light.getL(position), "getL() should return null when point is at the light source position");

        // ================= Paragraph: getIntensity =================
        // TC03: Test attenuation calculation: d = 5
        // factor = 1 + 0.1 * 5 + 0.02 * 25 = 1 + 0.5 + 0.5 = 2.0
        // expected intensity = intensity / 2.0
        Color expectedColor = intensity.scale(0.5);
        assertEquals(expectedColor, light.getIntensity(p), "getIntensity() with attenuation is incorrect");
    }

    /**
     * Test method for {@link lighting.SpotLight}.
     */
    @Test
    void testSpotLight() {
        Point position = new Point(0, 0, 0);
        Vector direction = new Vector(0, 0, -1); // Facing down Z axis
        Color intensity = new Color(100, 100, 100);

        // Regular SpotLight: kC = 1, kL = 0, kQ = 0
        SpotLight spot = new SpotLight(intensity, position, direction).setKc(1).setKl(0).setKq(0);

        // ================= Paragraph: getIntensity =================
        // TC01: Point perfectly aligned with the spot direction at distance 2: (0, 0, -2)
        // cos(Alpha) = 1, distance attenuation factor = 1
        Point pAligned = new Point(0, 0, -2);
        assertEquals(intensity, spot.getIntensity(pAligned), "Intensity should be full when aligned");

        // TC02: Point at an angle where cos(Alpha) is positive but less than 1
        // Point at (2, 0, -2) -> vector l is (2, 0, -2).normalize() = (1/sqrt(2), 0, -1/sqrt(2))
        // dotProduct(direction, l) = cos(Alpha) = 1/sqrt(2) approx 0.7071
        Point pAngle = new Point(2, 0, -2);
        Color expectedColor = intensity.scale(1.0 / Math.sqrt(2));
        assertEquals(expectedColor.getColor(), spot.getIntensity(pAngle).getColor(),
                "Intensity should be attenuated by the angle cosine");

        // TC03: Point behind the spotlight (cos(Alpha) <= 0)
        Point pBehind = new Point(0, 0, 2);
        assertEquals(Color.BLACK, spot.getIntensity(pBehind), "Intensity should be black behind the spotlight");

        // ================= Paragraph: Narrow Beam Bonus =================
        // TC04: Test SpotLight with narrow beam (concentration exponent)
        SpotLight narrowSpot = new SpotLight(intensity, position, direction)
                .setKc(1).setKl(0).setKq(0).setNarrowBeam(10);

        // For pAngle, cos(Alpha) = 1/sqrt(2). With narrowBeam=10, factor = (1/sqrt(2))^10 = 1/32 = 0.03125
        Color expectedNarrowColor = intensity.scale(1.0 / 32.0);
        assertEquals(expectedNarrowColor.getColor(), narrowSpot.getIntensity(pAngle).getColor(),
                "Narrow beam intensity calculation is incorrect");
    }
}