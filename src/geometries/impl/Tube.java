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
        Vector v = ray.direction();
        Point p0 = ray.origin();
        Vector va = _axis.direction();
        Point pa = _axis.origin();

        // a = (v - (v,va)va)^2
        double vva = v.dotProduct(va);
        Vector vMinusVVaVa = v;
        if (!primitives.Util.isZero(vva)) {
            try {
                vMinusVVaVa = v.subtract(va.scale(vva));
            } catch (IllegalArgumentException e) {
                return null; // Ray is parallel to the axis
            }
        }
        double a = vMinusVVaVa.lengthSquared();

        // b = 2 * (v - (v,va)va, deltaP - (deltaP,va)va)
        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            deltaP = null; // p0 == pa
        }

        Vector deltaPMinusDeltaPVaVa = deltaP;
        if (deltaP != null) {
            double dpva = deltaP.dotProduct(va);
            if (!primitives.Util.isZero(dpva)) {
                try {
                    deltaPMinusDeltaPVaVa = deltaP.subtract(va.scale(dpva));
                } catch (IllegalArgumentException e) {
                    deltaPMinusDeltaPVaVa = null;
                }
            }
        }

        double b = 0;
        if (deltaPMinusDeltaPVaVa != null) {
            b = 2 * vMinusVVaVa.dotProduct(deltaPMinusDeltaPVaVa);
        }

        // c = (deltaP - (deltaP,va)va)^2 - r^2
        double c = (deltaPMinusDeltaPVaVa == null) ? -_radius * _radius
                : deltaPMinusDeltaPVaVa.lengthSquared() - _radius * _radius;

        // Solve At^2 + Bt + C = 0
        double discriminant = primitives.Util.alignZero(b * b - 4 * a * c);

        if (discriminant <= 0) return null; // No intersection or tangent

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = primitives.Util.alignZero((-b + sqrtDisc) / (2 * a));
        double t2 = primitives.Util.alignZero((-b - sqrtDisc) / (2 * a));

        if (t1 > 0 && t2 > 0) return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0) return List.of(ray.getPoint(t1));
        if (t2 > 0) return List.of(ray.getPoint(t2));

        return null;
    }
}