package geometries.impl;

import primitives.Ray;

/**
 * Represents a finite cylinder in 3D space.
 * Inherits from Tube and adds a height.
 */
public class Cylinder extends Tube {
    private final double _height;

    /**
     * Constructor for Cylinder.
     * @param radius the radius of the cylinder
     * @param axis   the axis ray
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this._height = height;
    }
}