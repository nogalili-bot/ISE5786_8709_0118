package geometries.impl;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

/**
 * Represents an infinite tube in 3D space.
 * Defined by an axis ray and a radius.
 */
public class Tube extends RadialGeometry {
    /** The axis ray of the tube */
    protected final Ray _axis;

    /**
     * Constructor for Tube.
     * @param radius the radius of the tube
     * @param axis   the axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this._axis = axis;
    }

    /**
     * Returns the normal to the tube.
     * Note: Current implementation returns null.
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}