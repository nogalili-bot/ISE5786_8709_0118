package primitives;

/**
 * Represents a vector in 3D space.
 * Inherits from Point and must not be the zero vector.
 */
public class Vector extends Point {

    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize vector with three double values.
     * Throws exception if values are all zero.
     */
    public Vector(double x, double y, double z) {
        this(new Double3(x, y, z));
    }

    /**
     * Constructor to initialize vector with a Double3 object.
     * Throws exception if the values represent the zero point.
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (this._xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /** Returns a new vector that is the sum of this and another vector */
    public Vector add(Vector other) {
        return new Vector(this._xyz.add(other._xyz));
    }

    /** Returns a new vector scaled by a factor */
    public Vector scale(double scalar) {
        return new Vector(this._xyz.scale(scalar));
    }

    /** Calculates the dot product of two vectors */
    public double dotProduct(Vector other) {
        return this._xyz._d1() * other._xyz._d1() +
                this._xyz._d2() * other._xyz._d2() +
                this._xyz._d3() * other._xyz._d3();
    }

    /** Calculates the cross product of two vectors */
    public Vector crossProduct(Vector other) {
        double x = this._xyz._d2() * other._xyz._d3() - this._xyz._d3() * other._xyz._d2();
        double y = this._xyz._d3() * other._xyz._d1() - this._xyz._d1() * other._xyz._d3();
        double z = this._xyz._d1() * other._xyz._d2() - this._xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    /** Returns the squared length of the vector */
    public double lengthSquared() {
        return distanceSquared(Point.ZERO);
    }

    /** Returns the length of the vector */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /** Returns a new normalized vector in the same direction */
    public Vector normalize() {
        double len = length();
        return new Vector(this._xyz.scale(1 / len));
    }

    @Override
    public String toString() {
        return "Vector " + _xyz;
    }
}