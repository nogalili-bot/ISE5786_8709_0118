package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a flat plane in 3D space.
 */
public class Plane extends Geometry {
    private final Point _point;
    private final Vector _normal;

    /**
     * Constructor using three points.
     * Note: Normal calculation will be implemented in the next stage.
     */
    public Plane(Point p1, Point p2, Point p3) {
        this._point = p1;
        this._normal = null;
    }

    /**
     * Constructor using a point and a normal vector.
     * The normal vector is normalized.
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }
}
