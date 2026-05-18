package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface representing external light sources.
 */
public interface LightSource {
    /**
     * Get the intensity of the light at a specific point on a geometry.
     * @param p The point on the geometry
     * @return The color intensity at that point
     */
    public Color getIntensity(Point p);

    /**
     * Get the direction vector from the light source to the point.
     * @param p The point on the geometry
     * @return The normalized direction vector
     */
    public Vector getL(Point p);

    /**
     * Get the distance from the light source to the point.
     * @param point The point on the geometry
     * @return The distance to the point
     */
    public double getDistance(Point point);
}