package primitives;

/**
 * Material class represents the physical properties of a geometry's surface.
 * It defines how light reflects off the surface using the Phong reflection model.
 */
public class Material {
    /** Diffuse reflection coefficient */
    public Double3 kD = Double3.ZERO;
    /** Specular reflection coefficient */
    public Double3 kS = Double3.ZERO;
    /** Prominence of the specular highlight */
    public int nShininess = 0;

    /**
     * Setter for Diffuse coefficient using Double3.
     * @param kD The diffuse coefficient
     * @return This material instance (Builder pattern)
     */
    public Material setKd(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Setter for Diffuse coefficient using a single double.
     * @param kD The diffuse coefficient for all channels
     * @return This material instance (Builder pattern)
     */
    public Material setKd(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Setter for Specular coefficient using Double3.
     * @param kS The specular coefficient
     * @return This material instance (Builder pattern)
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Setter for Specular coefficient using a single double.
     * @param kS The specular coefficient for all channels
     * @return This material instance (Builder pattern)
     */
    public Material setKs(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Setter for Shininess.
     * @param nShininess The shininess factor
     * @return This material instance (Builder pattern)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}