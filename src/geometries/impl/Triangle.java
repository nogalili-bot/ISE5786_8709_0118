package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a triangle in 3D space.
 * Inherits from Polygon and is defined by three points.
 */
public class Triangle extends Polygon {

    /**
     * Constructor for Triangle.
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }
}