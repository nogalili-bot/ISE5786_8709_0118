package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static geometries.api.Intersectable.Intersection;

/**
 * Composite class to manage a collection of intersectable geometries.
 */
public class Geometries extends Intersectable {
    /** List of geometries in the collection */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Geometries() {
    }

    /**
     * Constructor with a variable number of geometries.
     * @param geometries variable number of Intersectable objects
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds a variable number of geometries to the collection.
     * @param geometries variable number of Intersectable objects
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
    }

    /**
     * Helper method to find all intersections in the composite collection.
     * @param ray The ray to intersect with the geometries
     * @return A list of Intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;

        for (Intersectable item : _geometries) {
            // Important: Call the public calcIntersections method of the item
            var itemIntersections = item.calcIntersections(ray);

            if (itemIntersections != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}