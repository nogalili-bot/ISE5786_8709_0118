package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Util;
import java.util.List;

import static geometries.api.Intersectable.Intersection;

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
        this.boundingBox = null;
    }

    /**
     * Calculates the unit normal vector to the tube's surface at a specified point.
     * @param p The point on the surface
     * @return Normalized normal vector
     */
    @Override
    public Vector getNormal(Point p) {
        // t = v * (P - P0)
        double t = _axis.direction().dotProduct(p.subtract(_axis.origin()));

        // If t is zero, the normal is simply p - p0
        if (Util.isZero(t)) return p.subtract(_axis.origin()).normalize();

        // The normal is the vector from the point on the axis (o) to p
        Point o = _axis.getPoint(t);
        return p.subtract(o).normalize();
    }

    /**
     * Helper method to find intersections of a ray with the tube.
     * @param ray The ray to intersect with the tube
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Vector v = ray.direction();
        Point p0 = ray.origin();
        Vector va = _axis.direction();
        Point pa = _axis.origin();

        // a = (v - (v,va)va)^2
        double vva = v.dotProduct(va);
        Vector vMinusVVaVa = v;
        if (!Util.isZero(vva)) {
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
            if (!Util.isZero(dpva)) {
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
        double discriminant = Util.alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = Util.alignZero((-b + sqrtDisc) / (2 * a));
        double t2 = Util.alignZero((-b - sqrtDisc) / (2 * a));

        if (t1 > 0 && t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)),
                    new Intersection(this, ray.getPoint(t2)));
        if (t1 > 0)
            return List.of(new Intersection(this, ray.getPoint(t1)));
        if (t2 > 0)
            return List.of(new Intersection(this, ray.getPoint(t2)));

        return null;
    }
}