package primitives;

/**
 * Represents a point in 3D space.
 * The class uses Double3 to store coordinates.
 */
public class Point {
    /**
     * Static constant for the origin point (0,0,0)
     */
    public static final Point ZERO = new Point(0, 0, 0);
    /**
     * Coordinates of the point
     */
    protected final Double3 _xyz;

    /**
     * Constructor using three double values.
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructor using a Double3 object.
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /**
     * Returns a vector from another point to this point
     */
    public Vector subtract(Point other) {
        return new Vector(this._xyz.subtract(other._xyz));
    }

    /**
     * Returns a new point after adding a vector to this point
     */
    public Point add(Vector vector) {
        return new Point(this._xyz.add(vector._xyz));
    }

    /**
     * Returns the squared distance between two points
     */
    public double distanceSquared(Point other) {
        double x1 = this._xyz._d1();
        double y1 = this._xyz._d2();
        double z1 = this._xyz._d3();

        double x2 = other._xyz._d1();
        double y2 = other._xyz._d2();
        double z2 = other._xyz._d3();

        return (x1 - x2) * (x1 - x2) +
                (y1 - y2) * (y1 - y2) +
                (z1 - z2) * (z1 - z2);
    }

    /**
     * Returns the distance between two points using distanceSquared
     */
    public double distance(Point other) {
        return Math.sqrt(this.distanceSquared(other));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return _xyz.equals(point._xyz);
    }

    @Override
    public String toString() {
        return _xyz.toString();
    }

}