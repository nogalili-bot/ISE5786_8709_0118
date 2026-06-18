package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.BoundingBox;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static geometries.api.Intersectable.Intersection;

/**
 * Composite class to manage a collection of intersectable geometries.
 */
public class Geometries extends Intersectable {
    /** List of geometries in the collection */
    private List<Intersectable> _geometries = new ArrayList<>();

    public Geometries() {
    }

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds a variable number of geometries and updates/refreshes the overall bounding box.
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
        refreshBoundingBox();
    }

    /**
     * Recomputes the combined bounding box encapsulation of all sub-geometries.
     */
    public void refreshBoundingBox() {
        if (_geometries.isEmpty()) {
            this.boundingBox = null;
            return;
        }

        BoundingBox tempBox = null;
        Vector vX = new Vector(1, 0, 0);
        Vector vY = new Vector(0, 1, 0);
        Vector vZ = new Vector(0, 0, 1);

        for (Intersectable geo : _geometries) {
            BoundingBox box = geo.getBoundingBox();
            if (box != null) {
                if (tempBox == null) {
                    tempBox = box;
                } else {
                    // Calculate minimum and maximum by comparing projections
                    double minX = Math.min(tempBox.min.subtract(Point.ZERO).dotProduct(vX), box.min.subtract(Point.ZERO).dotProduct(vX));
                    double minY = Math.min(tempBox.min.subtract(Point.ZERO).dotProduct(vY), box.min.subtract(Point.ZERO).dotProduct(vY));
                    double minZ = Math.min(tempBox.min.subtract(Point.ZERO).dotProduct(vZ), box.min.subtract(Point.ZERO).dotProduct(vZ));

                    double maxX = Math.max(tempBox.max.subtract(Point.ZERO).dotProduct(vX), box.max.subtract(Point.ZERO).dotProduct(vX));
                    double maxY = Math.max(tempBox.max.subtract(Point.ZERO).dotProduct(vY), box.max.subtract(Point.ZERO).dotProduct(vY));
                    double maxZ = Math.max(tempBox.max.subtract(Point.ZERO).dotProduct(vZ), box.max.subtract(Point.ZERO).dotProduct(vZ));

                    tempBox = new BoundingBox(
                            new Point(minX, minY, minZ),
                            new Point(maxX, maxY, maxZ)
                    );
                }
            }
        }

        // Add padding (value p)
        if (tempBox != null) {
            double p = 9.5;
            this.boundingBox = new BoundingBox(
                    new Point(tempBox.min.subtract(Point.ZERO).dotProduct(vX) - p,
                            tempBox.min.subtract(Point.ZERO).dotProduct(vY) - p,
                            tempBox.min.subtract(Point.ZERO).dotProduct(vZ) - p),
                    new Point(tempBox.max.subtract(Point.ZERO).dotProduct(vX) + p,
                            tempBox.max.subtract(Point.ZERO).dotProduct(vY) + p,
                            tempBox.max.subtract(Point.ZERO).dotProduct(vZ) + p)
            );
        } else {
            this.boundingBox = null;
        }
    }

    /**
     * Automatically builds a recursive BVH binary-tree hierarchy out of the flat list.
     */
    public void buildBVH() {
        if (_geometries == null || _geometries.size() <= 4) {
            return; // Stop splitting if geometry list is small enough
        }

        if (this.boundingBox == null) {
            refreshBoundingBox();
            if (this.boundingBox == null) return;
        }

        // 1. Separate geometries: those that have a bounding box and those that do not
        List<Intersectable> withBox = new ArrayList<>();
        List<Intersectable> withoutBox = new ArrayList<>();

        for (Intersectable geo : _geometries) {
            if (geo.getBoundingBox() != null) {
                withBox.add(geo);
            } else {
                withoutBox.add(geo);
            }
        }

        // If there are not enough geometries with a box to build a hierarchy, no need to continue splitting
        if (withBox.size() <= 4) {
            return;
        }

        // 2. Define unit vectors for the three main axes
        Vector axisX = new Vector(1, 0, 0);
        Vector axisY = new Vector(0, 1, 0);
        Vector axisZ = new Vector(0, 0, 1);

        // 3. Convert the bounding box min and max points to vectors from the origin
        Vector vMax = this.boundingBox.max.subtract(Point.ZERO);
        Vector vMin = this.boundingBox.min.subtract(Point.ZERO);

        // 4. Extract box edge lengths on each axis using dotProduct
        double extX = vMax.dotProduct(axisX) - vMin.dotProduct(axisX);
        double extY = vMax.dotProduct(axisY) - vMin.dotProduct(axisY);
        double extZ = vMax.dotProduct(axisZ) - vMin.dotProduct(axisZ);

        int axis = 0; // 0 = X, 1 = Y, 2 = Z
        if (extY > extX && extY > extZ) axis = 1;
        if (extZ > extX && extZ > extY) axis = 2;

        final int sortAxis = axis;

        // 5. Sorting will only be performed on the list of geometries that have a bounding box
        withBox.sort((g1, g2) -> {
            BoundingBox b1 = g1.getBoundingBox();
            BoundingBox b2 = g2.getBoundingBox();

            Vector vCenter1 = b1.getCenter().subtract(Point.ZERO);
            Vector vCenter2 = b2.getCenter().subtract(Point.ZERO);

            double c1, c2;
            if (sortAxis == 1) {
                c1 = vCenter1.dotProduct(axisY);
                c2 = vCenter2.dotProduct(axisY);
            } else if (sortAxis == 2) {
                c1 = vCenter1.dotProduct(axisZ);
                c2 = vCenter2.dotProduct(axisZ);
            } else {
                c1 = vCenter1.dotProduct(axisX);
                c2 = vCenter2.dotProduct(axisX);
            }
            return Double.compare(c1, c2);
        });

        // 6. Split the list of bounded geometries into two groups
        int mid = withBox.size() / 2;
        Geometries leftGroup = new Geometries();
        Geometries rightGroup = new Geometries();

        for (int i = 0; i < mid; i++) {
            leftGroup._geometries.add(withBox.get(i));
        }
        for (int i = mid; i < withBox.size(); i++) {
            rightGroup._geometries.add(withBox.get(i));
        }

        leftGroup.refreshBoundingBox();
        rightGroup.refreshBoundingBox();

        // Recursive call
        leftGroup.buildBVH();
        rightGroup.buildBVH();

        // 7. Reconstruction: geometries without a box (like cylinders) remain at this level, others become a tree
        this._geometries = new ArrayList<>(withoutBox);
        this._geometries.add(leftGroup);
        this._geometries.add(rightGroup);
        System.out.println("BVH Build: withBox size: " + withBox.size() + ", withoutBox size: " + withoutBox.size());
        refreshBoundingBox();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // BVH pruning mechanism: avoid unnecessary calculations if the ray is outside the bounding box
        if (isBVHEnabled && this.boundingBox != null) {
            // If the ray does not intersect the bounding box, verify that the origin is not inside
            if (!this.boundingBox.isIntersected(ray)) {
                // Safety check: ensure we don't block rays that start inside the bounding box
                if (!isOriginInside(ray, this.boundingBox)) {
                    return null; // Ray is outside and cannot hit any contained geometries
                }
            }
        }

        List<Intersection> result = null;
        // Iterate through all geometries and accumulate intersections
        for (Intersectable item : _geometries) {
            var itemIntersections = item.calcIntersections(ray);
            if (itemIntersections != null) {
                if (result == null) result = new ArrayList<>();
                result.addAll(itemIntersections);
            }
        }
        return result;
    }

    // Helper: Checks if the ray's starting point is inside the bounding box
    private boolean isOriginInside(Ray ray, BoundingBox box) {
        Point origin = ray.origin();
        double eps = 1e-4; // Safety margin

        // Convert the point to a vector relative to the origin (to allow dotProduct)
        Vector p = origin.subtract(Point.ZERO);

        // Define axis vectors (if no global variables are available for this)
        Vector vX = new Vector(1, 0, 0);
        Vector vY = new Vector(0, 1, 0);
        Vector vZ = new Vector(0, 0, 1);

        // Extract values via projection (Dot Product)
        double px = p.dotProduct(vX);
        double py = p.dotProduct(vY);
        double pz = p.dotProduct(vZ);

        double minX = box.min.subtract(Point.ZERO).dotProduct(vX);
        double maxX = box.max.subtract(Point.ZERO).dotProduct(vX);
        double minY = box.min.subtract(Point.ZERO).dotProduct(vY);
        double maxY = box.max.subtract(Point.ZERO).dotProduct(vY);
        double minZ = box.min.subtract(Point.ZERO).dotProduct(vZ);
        double maxZ = box.max.subtract(Point.ZERO).dotProduct(vZ);

        return (px >= minX - eps && px <= maxX + eps &&
                py >= minY - eps && py <= maxY + eps &&
                pz >= minZ - eps && pz <= maxZ + eps);
    }
}