package lighting;

import primitives.Color;

/**
 * Abstract base class for all types of light in the scene.
 */
abstract class Light {
    /** The intensity color of the light */
    protected final Color intensity;

    /**
     * Protected constructor for Light.
     * @param intensity The color intensity of the light
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Getter for the intensity color.
     * @return the intensity color
     */
    public Color getIntensity() {
        return intensity;
    }
}
