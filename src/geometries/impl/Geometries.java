package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite class to manage a collection of intersectable geometries.
 */
public class Geometries extends Intersectable {
    /** List of geometries in the collection */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Geometries() {}

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

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable item : _geometries) {
            var itemIntersections = item.findIntersections(ray);

            if (itemIntersections != null) {
                // Initialize the list only when the first intersection is found
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}