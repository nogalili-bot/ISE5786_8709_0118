package primitives;

import java.util.List;
import static geometries.api.Intersectable.Intersection;

/**
 * Represents a ray (half-line) in 3D space.
 * The ray has an origin point and a normalized direction vector.
 */
public final class Ray {
    private final Point _origin;
    private final Vector _direction;

    /**
     * Constructor for Ray.
     * The direction vector is normalized automatically.
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /** @return the origin point */
    public Point origin() { return _origin; }

    /** @return the normalized direction vector */
    public Vector direction() { return _direction; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ray ray)) return false;
        return _origin.equals(ray._origin) && _direction.equals(ray._direction);
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }

    /**
     * Calculates a point on the ray's line at a specific distance from the origin.
     * @param t scalar value representing the distance
     * @return the calculated Point
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException ignore) {
            return _origin;
        }
    }

    /**
     * Finds the closest Intersection to the ray's origin from a list of intersections.
     * @param intersections List of intersections to check
     * @return The closest Intersection, or null if the list is null or empty
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty())
            return null;

        Intersection closestIntersection = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distanceSquared = _origin.distanceSquared(intersection.point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestIntersection = intersection;
            }
        }
        return closestIntersection;
    }

    /**
     * Finds the closest point to the ray's origin from a list of points.
     * Refactored to use findClosestIntersection for consistency.
     * @param points List of points to check
     * @return The closest point, or null if the list is null
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                .map(point -> new Intersection(null, point))
                .toList()
        ).point;
    }
}