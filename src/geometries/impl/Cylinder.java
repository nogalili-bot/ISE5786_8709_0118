package geometries.impl;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;
import java.util.List;

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
    /**
     * Calculates the unit normal vector to the cylinder's surface at a specified point.
     * The method handles the shell (side) and the two flat bases.
     * * @param p The point on the surface for which the normal is calculated.
     * @return A normalized Vector perpendicular to the cylinder's surface at the given point.
     */
    @Override
    public Vector getNormal(Point p) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        // Calculate the projection of the point onto the axis: t = v * (p - p0)
        double t;
        try {
            t = alignZero(v.dotProduct(p.subtract(p0)));
        } catch (IllegalArgumentException e) {
            // Point is exactly the center of the bottom base (t=0)
            return v.scale(-1);
        }

        // Check if the point is on the bottom base (t=0)
        // This includes the boundary/edge points due to priority check
        if (isZero(t)) {
            return v.scale(-1);
        }

        // Check if the point is on the top base (t=height)
        if (isZero(t - _height)) {
            return v;
        }

        // Otherwise, the point is on the shell surface.
        // The normal is the vector from the point's projection on the axis to the point.
        Point o = _axis.getPoint(t);
        return p.subtract(o).normalize();
    }

    /**
     * Finds the intersections of a ray with the finite cylinder.
     * Checks intersections with the shell (within height bounds) and the two bases.
     * * @param ray The ray to intersect with the cylinder.
     * @return A list of intersection points, or null if none are found.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = new java.util.ArrayList<>();
        Vector va = _axis.direction();
        Point p0 = _axis.origin();
        Point topCenter = _axis.getPoint(_height);

        // 1. Check intersections with the infinite tube (shell/envelope)
        List<Point> tubeIntersections = super.findIntersections(ray);
        if (tubeIntersections != null) {
            for (Point p : tubeIntersections) {
                // Only keep intersections that are within the finite height
                double t = alignZero(va.dotProduct(p.subtract(p0)));
                if (t > 0 && t < _height) {
                    result.add(p);
                }
            }
        }

        // 2. Check intersections with the bottom base (disk at p0)
        List<Point> base1Inter = findDiskIntersections(ray, p0, va);
        if (base1Inter != null) result.addAll(base1Inter);

        // 3. Check intersections with the top base (disk at topCenter)
        List<Point> base2Inter = findDiskIntersections(ray, topCenter, va);
        if (base2Inter != null) result.addAll(base2Inter);

        return result.isEmpty() ? null : result;
    }

    /**
     * Helper method to find intersections with a disk base without creating Plane objects.
     * * @param ray    The ray to intersect.
     * @param center The center of the disk.
     * @param normal The normal of the disk's plane.
     * @return A list with the intersection point if it exists and is within the radius.
     */
    private List<Point> findDiskIntersections(Ray ray, Point center, Vector normal) {
        Point r0 = ray.origin();
        Vector v = ray.direction();

        double nv = normal.dotProduct(v);
        if (isZero(nv)) return null; // Ray is parallel to the disk plane

        double t;
        try {
            t = alignZero(normal.dotProduct(center.subtract(r0)) / nv);
        } catch (IllegalArgumentException e) {
            return null; // Ray origin is on the disk plane
        }

        if (t <= 0) return null; // Intersection is behind the ray origin

        Point p = ray.getPoint(t);
        // Check if the intersection point is within the disk's radius
        try {
            if (alignZero(p.distanceSquared(center) - _radius * _radius) < 0) {
                return List.of(p);
            }
        } catch (IllegalArgumentException e) {
            return List.of(p); // Point is exactly the center of the disk
        }

        return null;
    }
}