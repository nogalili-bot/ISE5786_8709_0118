package geometries.impl;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

import java.util.List;

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
    public Vector getNormal(Point p) {
        // Determination of t: t = v * (P - P0)
        double t = _axis.direction().dotProduct(p.subtract(_axis.origin()));

        // REFACTORING: Instead of manual calculation: _axisRay.origin().add(_axisRay.direction().scale(t))
        // We use the safe getPoint method:
        Point o = _axis.getPoint(t);

        // The normal is the vector from o to p
        return p.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}