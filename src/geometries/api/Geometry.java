package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Interface for all geometric objects in the system.
 * All geometries must implement a method to find their normal vector.
 */
public abstract class Geometry {

    /**
     * Calculates the normal vector to the geometry at a given point.
     * @param point The point on the geometry surface
     * @return The normal vector (perpendicular) to the surface
     */
    public abstract Vector getNormal(Point point);
}