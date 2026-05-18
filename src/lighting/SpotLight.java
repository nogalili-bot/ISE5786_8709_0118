package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * SpotLight represents a directional point light source with a focused beam.
 */
public class SpotLight extends PointLight {
    /** The direction vector of the spotlight beam */
    private final Vector direction;
    /** Concentration factor for narrow beam (bonus component) */
    private int narrowBeam = 1;

    /**
     * Constructor for SpotLight.
     * @param intensity The color intensity of the light
     * @param position  The position of the light source
     * @param direction The direction of the light beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    public SpotLight setNarrowBeam(int narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Color baseIntensity = super.getIntensity(p);
        Vector l = getL(p);

        if (l == null) {
            return Color.BLACK;
        }

        double cosAlpha = direction.dotProduct(l);

        if (cosAlpha <= 0) {
            return Color.BLACK;
        }

        if (narrowBeam != 1) {
            cosAlpha = Math.pow(cosAlpha, narrowBeam);
        }

        return baseIntensity.scale(cosAlpha);
    }
}