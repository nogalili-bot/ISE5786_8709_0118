package geometries.impl;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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
    @Override
    public Vector getNormal(Point p) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Check if the point is on the bottom base (t=0)
        double t;
        try {
            t = alignZero(v.dotProduct(p.subtract(p0)));
        } catch (IllegalArgumentException e) {
            // Point is exactly the origin (t=0), it's on the center of the bottom base
            return v.scale(-1);
        }

        // Check if the point is on the top base (t=height)
        if (isZero(t - _height)) return v;

        // Otherwise, it's on the side surface - Use REFACTORING
        Point o = _axis.getPoint(t);
        return p.subtract(o).normalize();
    }
}