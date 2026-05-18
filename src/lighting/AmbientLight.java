package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Ambient light for the scene - provides uniform illumination for all objects.
 */
public class AmbientLight extends Light {

    /** Constant for no ambient light (Black) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor to initialize ambient light with intensity color.
     * @param intensity the color of the light
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }

    /**
     * Constructor for ambient light with attenuation factor.
     * Calculated as: Intensity * ka
     * @param Ia base intensity color
     * @param ka attenuation factor
     */
    public AmbientLight(Color Ia, Double3 ka) {
        super(Ia.scale(ka));
    }

    /**
     * Constructor for ambient light with attenuation factor (scalar).
     * @param Ia base intensity color
     * @param ka attenuation factor (double)
     */
    public AmbientLight(Color Ia, double ka) {
        super(Ia.scale(ka));
    }
}