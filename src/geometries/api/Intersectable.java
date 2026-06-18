package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.BoundingBox;
import java.util.List;
import java.util.Objects;

/**
 * Interface for all objects that can be intersected by a ray.
 */
public abstract class Intersectable {

    /** Global flag to enable or disable BVH optimization */
    public static boolean isBVHEnabled = false;

    /** Bounding Box for this intersectable object */
    protected BoundingBox boundingBox;

    /** Getter for the bounding box */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public static final class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = (geometry == null) ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Intersection that)) return false;
            return this.geometry == that.geometry && Objects.equals(this.point, that.point);
        }

        @Override
        public String toString() {
            return "Intersection: geometry=" + geometry + ", point=" + point;
        }
    }

    /**
     * Public method to find intersections as Intersections (NVI Pattern).
     * Refactored to clip calculation early if BVH box is missed.
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        // BVH Optimization check
        if (isBVHEnabled && boundingBox != null && !boundingBox.isIntersected(ray)) {
            return null; // Early cutoff: Ray missed the bounding box
        }
        return calcIntersectionsHelper(ray);
    }

    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                  .map(intersection -> intersection.point)
                  .toList();
    }
}