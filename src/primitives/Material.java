package primitives;

/**
 * Material class represents the physical properties of a geometry's surface.
 * It defines how light reflects off the surface using the Phong reflection model.
 */
public class Material {
    /**
     * Refraction/Transparency coefficient (0 = opaque, 1 = fully transparent)
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection coefficient (0 = matte, 1 = perfect mirror)
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Setter for transparency coefficient (Double3)
     * @param kT transparency coefficient
     * @return this material instance
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Setter for transparency coefficient (double)
     * @param kT transparency coefficient
     * @return this material instance
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Setter for reflection coefficient (Double3)
     * @param kR reflection coefficient
     * @return this material instance
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Setter for reflection coefficient (double)
     * @param kR reflection coefficient
     * @return this material instance
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }
    /**
     * Diffuse reflection coefficient
     */
    public Double3 kD = Double3.ZERO;
    /**
     * Specular reflection coefficient
     */
    public Double3 kS = Double3.ZERO;
    public Double3 kA = Double3.ONE;
    /**
     * Prominence of the specular highlight
     */
    public int nShininess = 0;

    /**
     * PDS class representing the material properties of a geometry.
     */
    public Material() {
    }

    /**
     * Setter for kA with Double3 parameter.
     *
     * @param kA coefficient for ambient light
     * @return this for chaining
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for kA with double parameter.
     *
     * @param kA coefficient for ambient light
     * @return this for chaining
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Setter for Diffuse coefficient using Double3.
     *
     * @param kD The diffuse coefficient
     * @return This material instance (Builder pattern)
     */
    public Material setKd(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Setter for Diffuse coefficient using a single double.
     *
     * @param kD The diffuse coefficient for all channels
     * @return This material instance (Builder pattern)
     */
    public Material setKd(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Setter for Specular coefficient using Double3.
     *
     * @param kS The specular coefficient
     * @return This material instance (Builder pattern)
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Setter for Specular coefficient using a single double.
     *
     * @param kS The specular coefficient for all channels
     * @return This material instance (Builder pattern)
     */
    public Material setKs(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Setter for Shininess.
     *
     * @param nShininess The shininess factor
     * @return This material instance (Builder pattern)
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}
