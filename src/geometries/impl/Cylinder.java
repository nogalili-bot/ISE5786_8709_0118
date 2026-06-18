package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.ArrayList;
import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;
import primitives.BoundingBox;

import static geometries.api.Intersectable.Intersection;

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

        // צילינדר כללי במרחב דורש חישוב קופסה מורכב שעלול לעוות את התמונה.
        // נשאיר את הקופסה שלו כ-null; המערכת תחשב עבורו חיתוך רגיל,
        // וה-BVH עדיין יאיץ את מאות המשולשים שבסצנה בצורה פנומנלית!
        this.boundingBox = null;
    }

    /**
     * Calculates the unit normal vector to the cylinder's surface.
     * @param p The point on the surface
     * @return Normalized normal vector
     */
    @Override
    public Vector getNormal(Point p) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        double t;
        try {
            t = alignZero(v.dotProduct(p.subtract(p0)));
        } catch (IllegalArgumentException e) {
            return v.scale(-1);
        }

        if (isZero(t) || t < 0) return v.scale(-1);
        if (isZero(t - _height) || t > _height) return v;

        Point o = _axis.getPoint(t);
        return p.subtract(o).normalize();
    }

    /**
     * Helper method to find intersections of a ray with the finite cylinder.
     * @param ray The ray to intersect with the cylinder
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> result = new ArrayList<>();
        Vector va = _axis.direction();
        Point p0 = _axis.origin();
        Point topCenter = _axis.getPoint(_height);

        // 1. Check intersections with the infinite tube shell
        // Important: Use super.calcIntersectionsHelper to get Intersections
        List<Intersection> tubeIntersections = super.calcIntersectionsHelper(ray);
        if (tubeIntersections != null) {
            for (Intersection inter : tubeIntersections) {
                double t = alignZero(va.dotProduct(inter.point.subtract(p0)));
                if (t > 0 && t < _height) {
                    result.add(new Intersection(this, inter.point));
                }
            }
        }

        // 2. Check intersections with the bottom base (disk at p0)
        List<Intersection> base1Inter = findDiskIntersections(ray, p0, va);
        if (base1Inter != null) result.addAll(base1Inter);

        // 3. Check intersections with the top base (disk at topCenter)
        List<Intersection> base2Inter = findDiskIntersections(ray, topCenter, va);
        if (base2Inter != null) result.addAll(base2Inter);

        return result.isEmpty() ? null : result;
    }

    /**
     * Helper method to find intersections with a disk base.
     * @param ray    The ray to intersect
     * @param center The center of the disk
     * @param normal The normal of the disk's plane
     * @return A list with the intersection, or null
     */
    private List<Intersection> findDiskIntersections(Ray ray, Point center, Vector normal) {
        Point r0 = ray.origin();
        Vector v = ray.direction();

        double nv = normal.dotProduct(v);
        if (isZero(nv)) return null;

        double t;
        try {
            t = alignZero(normal.dotProduct(center.subtract(r0)) / nv);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (t <= 0) return null;

        Point p = ray.getPoint(t);
        try {
            if (alignZero(p.distanceSquared(center) - _radius * _radius) < 0) {
                return List.of(new Intersection(this, p));
            }
        } catch (IllegalArgumentException e) {
            return List.of(new Intersection(this, p));
        }

        return null;
    }
}