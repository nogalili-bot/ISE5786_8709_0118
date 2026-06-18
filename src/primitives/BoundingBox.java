package primitives;

/**
 * Axis-Aligned Bounding Box (AABB) for BVH acceleration.
 */
public class BoundingBox {
    public final Point min;
    public final Point max;

    // Unit vectors to extract components without direct access to _xyz
    private static final Vector V_X = new Vector(1, 0, 0);
    private static final Vector V_Y = new Vector(0, 1, 0);
    private static final Vector V_Z = new Vector(0, 0, 1);

    public BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new BoundingBox that contains both this box and another box.
     */
    public BoundingBox union(BoundingBox other) {
        if (other == null) return this;

        // Definition of unit vectors
        Vector vx = new Vector(1, 0, 0);
        Vector vy = new Vector(0, 1, 0);
        Vector vz = new Vector(0, 1, 0);

        // Extract values using safe dotProduct
        double minX = Math.min(this.min.subtract(Point.ZERO).dotProduct(vx), other.min.subtract(Point.ZERO).dotProduct(vx));
        double minY = Math.min(this.min.subtract(Point.ZERO).dotProduct(vy), other.min.subtract(Point.ZERO).dotProduct(vy));
        double minZ = Math.min(this.min.subtract(Point.ZERO).dotProduct(vz), other.min.subtract(Point.ZERO).dotProduct(vz));

        double maxX = Math.max(this.max.subtract(Point.ZERO).dotProduct(vx), other.max.subtract(Point.ZERO).dotProduct(vx));
        double maxY = Math.max(this.max.subtract(Point.ZERO).dotProduct(vy), other.max.subtract(Point.ZERO).dotProduct(vy));
        double maxZ = Math.max(this.max.subtract(Point.ZERO).dotProduct(vz), other.max.subtract(Point.ZERO).dotProduct(vz));

        // Add a small epsilon to each side to prevent blocking rays passing exactly on the box boundary
        double eps = 1e-7;

        return new BoundingBox(
                new Point(minX - eps, minY - eps, minZ - eps),
                new Point(maxX + eps, maxY + eps, maxZ + eps)
        );
    }

    public Point getCenter() {
        return new Point(
                (min.subtract(Point.ZERO).dotProduct(V_X) + max.subtract(Point.ZERO).dotProduct(V_X)) / 2.0,
                (min.subtract(Point.ZERO).dotProduct(V_Y) + max.subtract(Point.ZERO).dotProduct(V_Y)) / 2.0,
                (min.subtract(Point.ZERO).dotProduct(V_Z) + max.subtract(Point.ZERO).dotProduct(V_Z)) / 2.0
        );
    }

    /**
     * Ray-AABB intersection test using the Slab Method.
     */
    public boolean isIntersected(Ray ray) {
        // 1. Add padding to prevent precision issues
        double eps = 1e-4; // Small margin to prevent the box from being too "tight"

        double xMin = min.subtract(Point.ZERO).dotProduct(V_X) - eps;
        double xMax = max.subtract(Point.ZERO).dotProduct(V_X) + eps;
        double yMin = min.subtract(Point.ZERO).dotProduct(V_Y) - eps;
        double yMax = max.subtract(Point.ZERO).dotProduct(V_Y) + eps;
        double zMin = min.subtract(Point.ZERO).dotProduct(V_Z) - eps;
        double zMax = max.subtract(Point.ZERO).dotProduct(V_Z) + eps;

        Point origin = ray.origin();
        Vector dir = ray.direction();

        double ox = origin.subtract(Point.ZERO).dotProduct(V_X);
        double oy = origin.subtract(Point.ZERO).dotProduct(V_Y);
        double oz = origin.subtract(Point.ZERO).dotProduct(V_Z);

        double dx = dir.dotProduct(V_X);
        double dy = dir.dotProduct(V_Y);
        double dz = dir.dotProduct(V_Z);

        // 2. Check if the camera is inside the box (including the eps)
        if (ox >= xMin && ox <= xMax && oy >= yMin && oy <= yMax && oz >= zMin && oz <= zMax) {
            return true;
        }

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        // X axis
        if (Math.abs(dx) > 1e-10) {
            double tx1 = (xMin - ox) / dx;
            double tx2 = (xMax - ox) / dx;
            tMin = Math.max(tMin, Math.min(tx1, tx2));
            tMax = Math.min(tMax, Math.max(tx1, tx2));
        } else if (ox < xMin || ox > xMax) return false;

        // Y axis
        if (Math.abs(dy) > 1e-10) {
            double ty1 = (yMin - oy) / dy;
            double ty2 = (yMax - oy) / dy;
            tMin = Math.max(tMin, Math.min(ty1, ty2));
            tMax = Math.min(tMax, Math.max(ty1, ty2));
        } else if (oy < yMin || oy > yMax) return false;

        // Z axis
        if (Math.abs(dz) > 1e-10) {
            double tz1 = (zMin - oz) / dz;
            double tz2 = (zMax - oz) / dz;
            tMin = Math.max(tMin, Math.min(tz1, tz2));
            tMax = Math.min(tMax, Math.max(tz1, tz2));
        } else if (oz < zMin || oz > zMax) return false;

        return tMax >= tMin && tMax > 0;
    }
}