package renderer;

import primitives.*;
import static primitives.Util.*;
import java.util.MissingResourceException;
import scene.*;

/**
 * Camera class representing a viewpoint in 3D space.
 * The class manages the view plane geometry and generates rays through pixels.
 * Implements Cloneable to support the Builder pattern approach.
 */
public class Camera implements Cloneable {
    // Camera location and orientation vectors
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;

    // View Plane geometry
    private double width = 0;
    private double height = 0;
    private double distance = 0;

    // View Plane resolution (defaults to 1x1)
    private int nX = 1;
    private int nY = 1;

    // Pre-computed helper fields for performance optimization
    private Point vpc;      // View Plane Center
    private double pixelW;  // Pixel Width
    private double pixelH;  // Pixel Height

    private ImageWriter _imageWriter;
    private RayTracerBase _rayTracer;

    /**
     * Private default constructor to prevent direct instantiation.
     */
    private Camera() {}

    /**
     * Returns a new Builder instance for Camera construction.
     * @return A new Camera Builder.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    // --- Getters ---
    public Point getP0() { return p0; }
    public Vector getvTo() { return vTo; }
    public Vector getvUp() { return vUp; }
    public Vector getvRight() { return vRight; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getDistance() { return distance; }

    /**
     * Constructs a ray through a specific pixel (j, i).
     * @param j The column index (xIndex)
     * @param i The row index (yIndex)
     * @return The generated Ray
     */
    public Ray constructRay(int j, int i) {
        Point pIJ = vpc;

        // Calculate horizontal offset
        double xj = (j - (nX - 1) / 2.0) * pixelW;
        if (!isZero(xj)) {
            pIJ = pIJ.add(vRight.scale(xj));
        }

        // Calculate vertical offset
        double yi = -(i - (nY - 1) / 2.0) * pixelH;
        if (!isZero(yi)) {
            pIJ = pIJ.add(vUp.scale(yi));
        }

        return new Ray(p0, pIJ.subtract(p0));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Builder class for Camera.
     */
    public static class Builder {
        private final Camera _camera = new Camera();
        private Vector _vTo = null;
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y;

        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {
            this._vTo = to;
            this._vUp = up;
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            this._target = target;
            this._vUp = up;
            return this;
        }

        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        public Builder setImageWriter(ImageWriter imageWriter) {
            this._camera._imageWriter = imageWriter;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._camera._rayTracer = new SimpleRayTracer(scene);
            }
            return this;
        }

        /**
         * Builds the Camera instance.
         * Validation for ImageWriter/RayTracer is removed to support integration tests.
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            // REMOVED: MissingResourceException for ImageWriter and RayTracer.
            // This allows tests that only check Ray construction to pass.

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
        }

        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing camera location", "Camera", "p0");
            if (_vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "_vUp");

            if (_vTo == null && _target != null)
                _vTo = _target.subtract(_camera.p0);

            if (_vTo == null)
                throw new MissingResourceException("Missing direction", "Camera", "_vTo");

            _camera.vTo = _vTo.normalize();

            try {
                _camera.vRight = _camera.vTo.crossProduct(_vUp).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp vectors are parallel");
            }

            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        private void checkViewPlane() {
            if (isZero(_camera.width) || _camera.width < 0)
                throw new IllegalArgumentException("Width must be positive");
            if (isZero(_camera.height) || _camera.height < 0)
                throw new IllegalArgumentException("Height must be positive");
            if (isZero(_camera.distance) || _camera.distance < 0)
                throw new IllegalArgumentException("Distance must be positive");

            _camera.vpc = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelW = _camera.width / _camera.nX;
            _camera.pixelH = _camera.height / _camera.nY;
        }
    }

    /**
     * Renders the image. Checks for resources here instead of in build().
     */
    public Camera renderImage() {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");
        if (_rayTracer == null)
            throw new MissingResourceException("Missing RayTracer", "Camera", "rayTracer");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    private void castRay(int j, int i) {
        Ray ray = constructRay(j, i);
        Color pixelColor = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(j, i, pixelColor);
    }

    public Camera printGrid(int interval, Color color) {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    public void writeToImage(String fileName) {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");
        _imageWriter.writeToImage(fileName);
    }
}