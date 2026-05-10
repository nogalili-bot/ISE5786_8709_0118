package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Ambient Light class for environmental lighting.
 */
public class AmbientLight {

    /** The intensity of the ambient light */
    private final Color _intensity;

    /** Static constant representing no ambient light (Black) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, Double3.ZERO);

    /**
     * Constructor to initialize the ambient light intensity (Ia * Ka).
     * @param iA The intensity color (Ia)
     * @param kA The attenuation coefficient (Ka)
     */
    public AmbientLight(Color iA, Double3 kA) {
        this._intensity = iA.scale(kA);
    }

    /**
     * Helper constructor for a single double coefficient.
     * @param iA The intensity color (Ia)
     * @param kA The attenuation coefficient as a double
     */
    public AmbientLight(Color iA, double kA) {
        this._intensity = iA.scale(kA);
    }

    /**
     * Basic constructor for direct intensity setting.
     * @param intensity The final intensity color
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