package primitives;

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
    public Point origin() {
        return _origin;
    }

    /** @return the normalized direction vector */
    public Vector direction() {
        return _direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ray)) return false;
        Ray ray = (Ray) o;
        return _origin.equals(ray._origin) && _direction.equals(ray._direction);
    }

    @Override
    public String toString() {
        return "Ray: origin=" + _origin + ", direction=" + _direction;
    }

    /**
     * Calculates a point on the ray's line at a specific distance from the origin.
     * The method uses the formula: P = P0 + t * v
     * This implementation safely handles cases where t is zero or near zero using exception handling.
     * @param t a scalar value representing the distance from the origin.
     * Can be positive, negative, or zero.
     * @return the calculated {@link Point} on the line.
     */
    public Point getPoint(double t) {
        try {
            // The safe way: attempt to scale the direction and add to origin.
            // If t is 0 (or creates a zero vector), scale() throws IllegalArgumentException.
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException ignore) {
            // If an exception occurs, it means the result of scaling was a zero vector,
            // so the point is simply the origin of the ray.
            return _origin;
        }
    }

    /**
     * Finds the closest point to the ray's origin from a list of points.
     * @param points List of points to check
     * @return The closest point, or null if the list is null
     */
    public Point findClosestPoint(java.util.List<Point> points) {
        if (points == null) // Check only for null as per instructions
            return null;

        Point closestPoint = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY; // Use POSITIVE_INFINITY as requested

        for (Point p : points) {
            // Using squared distance for efficiency instead of distance
            double distanceSquared = _origin.distanceSquared(p);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestPoint = p;
            }
        }
        return closestPoint;
    }
}

