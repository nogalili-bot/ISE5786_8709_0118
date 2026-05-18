package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * PointLight represents an omni-directional point light source (like a light bulb).
 * The light propagates from a specific position in space and attenuates with distance.
 */
public class PointLight extends Light implements LightSource {
    /** The position of the light source in space */
    protected final Point position;

    /** Constant attenuation factor */
    private double kC = 1.0;
    /** Linear attenuation factor */
    private double kL = 0.0;
    /** Quadratic attenuation factor */
    private double kQ = 0.0;

    /**
     * Constructor for PointLight.
     * @param intensity The color intensity of the light
     * @param position  The position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double d = position.distance(p);
        //  kC + kL * d + kQ * d^2
        double factor = kC + kL * d + kQ * d * d;

        return intensity.scale(1.0 / factor);
    }

    @Override
    public Vector getL(Point p) {
        if (p.equals(position)) {
            return null;
        }
        return p.subtract(position).normalize();
    }

    @Override
    public double getDistance(Point point) {
        return position.distance(point);
    }
}