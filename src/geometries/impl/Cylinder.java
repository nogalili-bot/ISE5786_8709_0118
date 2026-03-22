package geometries.impl;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;
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
    /**
     * Calculates the unit normal vector to the cylinder's surface at a specified point.
     * The method distinguishes between three parts of the cylinder:
     * 1. The bottom base (centered at the axis origin).
     * 2. The top base (centered at origin + height * direction).
     * 3. The side shell (the curved surface).
     * * @param point A point on the cylinder's surface.
     * @return A normalized Vector perpendicular to the surface at the given point.
     * @throws IllegalArgumentException if the point is not on the cylinder's surface.
     */
    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Check if the point is the origin itself to avoid a zero vector exception
        // during subtraction, and return the downward normal (opposite to axis direction).
        if (point.equals(p0)) {
            return v.scale(-1);
        }

        // Vector from the axis origin to the given point
        Vector pMinusP0 = point.subtract(p0);

        // Calculate the distance 't' from the origin to the point's projection on the axis
        double t = v.dotProduct(pMinusP0);

        // Case 1: The point is on the bottom base (t is effectively 0)
        if (isZero(t)) {
            return v.scale(-1);
        }

        // Case 2: The point is on the top base (t is effectively equal to height)
        if (isZero(t - _height)) {
            return v;
        }

        // Case 3: The point is on the side shell (0 < t < height)
        // Find the center of the circular cross-section (point 'o' on the axis)
        Point o = p0.add(v.scale(t));

        // The normal is the radial vector from the axis to the surface point
        return point.subtract(o).normalize();
    }
}