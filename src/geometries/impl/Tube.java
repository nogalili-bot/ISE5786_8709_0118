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
     * Calculates the unit normal vector to the cylinder's surface at a specified point.
     * * @param point The point on the surface for which the normal is calculated.
     * @return A normalized Vector perpendicular to the cylinder's surface at the given point.
     */
    @Override
    public Vector getNormal(Point point) {
        // Find the vector from the axis origin to the given point
        Vector pMinusP0 = point.subtract(_axis.origin());

        // Calculate the projection of this vector onto the axis direction (scalar value)
        double t = _axis.direction().dotProduct(pMinusP0);

        // Start with the axis origin
        Point o = _axis.origin();

        // If the point is not directly at the origin's orthogonal plane,
        // move 'o' along the axis to find the projection of 'point' onto the axis.
        if (t != 0) {
            o = o.add(_axis.direction().scale(t));
        }
        // The normal is the vector from the projected point 'o' on the axis to the surface point
        return point.subtract(o).normalize();
    }
}