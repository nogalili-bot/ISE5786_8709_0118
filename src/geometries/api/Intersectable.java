package geometries.api;

import primitives.*;
import java.util.List;

public abstract class Intersectable {
    public abstract List<Point> findIntersections(Ray ray);
}