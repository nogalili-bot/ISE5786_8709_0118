package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Directional light source represents light from infinity (e.g., Sun).
 * The direction is constant, and there is no attenuation with distance.
 */
public class DirectionalLight extends Light implements LightSource {
    /** The direction vector of the light (from the source to the scene) */
    private final Vector direction;

    /**
     * Constructor for DirectionalLight.
     * @param intensity The color intensity
     * @param direction The direction of the light rays
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }

    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
}