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
}
