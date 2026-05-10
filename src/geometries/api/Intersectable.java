package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import java.util.List;
import java.util.Objects;

/**
 * Interface for all objects that can be intersected by a ray.
 */
public abstract class Intersectable {

    /**
     * PDS (Passive Data Structure) for intersection data.
     * This class is final and cannot be inherited.
     */
    public static final class Intersection {
        /** The geometry that was intersected */
        public final Geometry geometry;
        /** The point of intersection */
        public final Point point;
        /** The material of the geometry at the intersection point */
        public final Material material;

        /**
         * Constructor initializing all fields.
         * Extracts the material from the geometry or sets a default one.
         * @param geometry the geometry that was intersected
         * @param point    the point of intersection
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            // Initialization according to instructions:
            // If geometry is null (e.g. from findClosestPoint), use new Material.
            // Otherwise, get the material from the geometry.
            this.material = (geometry == null) ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Intersection that)) return false;
            // Equals remains unchanged: compares geometry by reference and point by equals
            return this.geometry == that.geometry && Objects.equals(this.point, that.point);
        }

        @Override
        public String toString() {
            return "Intersection: geometry=" + geometry + ", point=" + point;
        }
    }

    /**
     * Public method to find intersections as Intersections (NVI Pattern).
     * This method is final and calls the protected helper.
     * @param ray The ray to intersect
     * @return List of intersections, or null if none found
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Protected abstract method to be implemented by all geometries.
     * @param ray The ray to intersect
     * @return List of intersections, or null if none found
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Finds all intersection points (legacy support).
     * This method is final and uses Stream API to extract points from Intersections.
     * @param ray The ray to intersect
     * @return List of intersection points, or null if none found
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                  .map(intersection -> intersection.point)
                  .toList();
    }
}