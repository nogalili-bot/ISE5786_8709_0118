package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract class for all geometric shapes that have a radius.
 * Inherits from Geometry.
 */
public abstract class RadialGeometry extends Geometry {
    /** The radius of the shape */
    protected final double _radius;
    /** The squared radius for performance optimization */
    protected final double _radiusSquared;

    /**
     * Constructor to initialize the radius and its square.
     * @param radius the radius of the shape
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }
}