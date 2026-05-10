package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for all geometric objects in the system.
 * All geometries must implement a method to find their normal vector,
 * manage their emission light, and hold surface material properties.
 */
public abstract class Geometry extends Intersectable {

    /** The emission color of the geometry */
    private Color emission = Color.BLACK;

    /** The material properties of the geometry's surface */
    private Material material = new Material();

    /**
     * Getter for the emission color.
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Setter for the emission color (Builder pattern).
     * @param emission the new emission color
     * @return the geometry object itself
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Getter for the material properties.
     * @return the material object
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Setter for the material properties (Builder pattern).
     * @param material the new material properties
     * @return the geometry object itself
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at a given point.
     * @param point The point on the geometry surface
     * @return The normal vector (perpendicular) to the surface
     */
    public abstract Vector getNormal(Point point);
}