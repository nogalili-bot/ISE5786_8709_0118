package lighting;

import primitives.Color;

/**
 * Ambient Light class for environmental lighting.
 * This class is immutable.
 */
public class AmbientLight {

    /**
     * The intensity of the ambient light
     */
    private final Color _intensity;

    /**
     * Static constant representing no ambient light (Black).
     * Initialized with an object that has Black color.
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor to initialize the ambient light intensity.
     * @param intensity The color of the light
     */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Getter for the light intensity.
     * @return the intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}