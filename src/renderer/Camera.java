package renderer;

import primitives.*;
import static primitives.Util.*;
import java.util.MissingResourceException;
import scene.*;

/**
 * Camera class representing a viewpoint in 3D space.
 * The class manages the view plane geometry and generates rays through pixels.
 * Implements Cloneable to support the Builder pattern approach.
 * * @author Your Name
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

    /**
     * Private default constructor to prevent direct instantiation.
     */
    private Camera() {}

    private ImageWriter _imageWriter;
    private RayTracerBase _rayTracer;

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
     * Constructs a ray through a specific pixel (j, i) using pre-calculated helper fields.
     * * @param j The column index (xIndex)
     * @param i The row index (yIndex)
     * @return  The generated Ray from the camera origin through the pixel center
     */
    public Ray constructRay(int j, int i) {
        // 1. Start from the pre-calculated View Plane Center (vpc)
        Point pIJ = vpc;

        // 2. Calculate horizontal offset (X axis):
        // The distance from the center of the VP to the center of pixel j.
        // Formula: (j - (nX - 1) / 2.0) * pixelWidth
        double xj = (j - (nX - 1) / 2.0) * pixelW;
        if (!isZero(xj)) {
            pIJ = pIJ.add(vRight.scale(xj));
        }

        // 3. Calculate vertical offset (Y axis):
        // In CG, i increases downwards, so we multiply by -pixelHeight.
        // Formula: -(i - (nY - 1) / 2.0) * pixelHeight
        double yi = -(i - (nY - 1) / 2.0) * pixelH;
        if (!isZero(yi)) {
            pIJ = pIJ.add(vUp.scale(yi));
        }

        // 4. Create the ray starting from p0 towards the calculated pixel center point
        // Ray constructor will normalize the direction vector automatically.
        return new Ray(p0, pIJ.subtract(p0));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Builder class for Camera using the "Empty Object" approach.
     */
    public static class Builder {
        private final Camera _camera = new Camera();

        // Temporary fields for direction calculation
        private Vector _vTo = null;
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y; // Default as per instructions

        /**
         * Sets the camera's location.
         * @param location The position point of the camera.
         * @return The builder instance.
         */
        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using two vectors.
         * @param to The direction vector the camera points to.
         * @param up The general "up" direction vector.
         * @return The builder instance.
         */
        public Builder setDirection(Vector to, Vector up) {
            this._vTo = to;
            this._vUp = up;
            return this;
        }

        /**
         * Sets the camera direction towards a target point.
         * @param target The point the camera is looking at.
         * @param up     The general "up" direction vector.
         * @return The builder instance.
         */
        public Builder setDirection(Point target, Vector up) {
            this._target = target;
            this._vUp = up;
            return this;
        }

        /**
         * Sets the camera direction towards a target point with default up vector.
         * @param target The point the camera is looking at.
         * @return The builder instance.
         */
        public Builder setDirection(Point target) {
            this._target = target;
            return this;
        }

        /**
         * Sets the physical size of the view plane.
         * @param width  Width of the plane.
         * @param height Height of the plane.
         * @return The builder instance.
         */
        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        /**
         * Sets the distance between the camera and the view plane.
         * @param distance Focal distance.
         * @return The builder instance.
         */
        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         * @param nX Number of pixels on X axis.
         * @param nY Number of pixels on Y axis.
         * @return The builder instance.
         */
        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        /**
         * Validates all parameters and builds the Camera object.
         * @return A finalized Camera instance.
         * @throws IllegalArgumentException if any numeric parameter is invalid.
         * @throws MissingResourceException if required fields are null.
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._imageWriter == null)
                throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");
            if (_camera._rayTracer == null)
                throw new MissingResourceException("Missing RayTracer", "Camera", "rayTracer");

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

        // --- Helper Methods for Build Validation and Calculation ---

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
        }

        private void checkLocationAndDirection() {
            if (_camera.p0 == null)
                throw new MissingResourceException("Missing camera location", "Camera", "p0");
            if (_vUp == null)
                throw new MissingResourceException("Missing up vector", "Camera", "_vUp");
            if (_vTo == null && _target == null)
                throw new MissingResourceException("Missing direction", "Camera", "_vTo");

            // Calculate vTo if target point was provided instead of vector
            if (_vTo == null) {
                _vTo = _target.subtract(_camera.p0);
            }
            _camera.vTo = _vTo.normalize();

            // Calculate vRight (vTo x vUp)
            try {
                _camera.vRight = _camera.vTo.crossProduct(_vUp).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp vectors are parallel");
            }

            // Recalculate final vUp to ensure perfect orthogonality
            _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
        }

        private void checkViewPlane() {
            if (isZero(_camera.width) || _camera.width < 0)
                throw new IllegalArgumentException("Width must be positive");
            if (isZero(_camera.height) || _camera.height < 0)
                throw new IllegalArgumentException("Height must be positive");
            if (isZero(_camera.distance) || _camera.distance < 0)
                throw new IllegalArgumentException("Distance must be positive");

            // Pre-calculate view plane center and pixel dimensions
            _camera.vpc = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelW = _camera.width / _camera.nX;
            _camera.pixelH = _camera.height / _camera.nY;
        }
        /**
         * Rotates the camera around a given axis.
         * @param axis  The vector to rotate around (should be normalized)
         * @param angle The angle of rotation in degrees
         * @return The builder instance
         */
        public Builder rotate(Vector axis, double angle) {
            if (isZero(angle)) return this;

            double radians = Math.toRadians(angle);
            double cosA = Math.cos(radians);
            double sinA = Math.sin(radians);

            // Function to rotate a single vector using Rodrigues' formula
            // Note: We only rotate vTo and vUp. vRight will be re-calculated in build().
            if (_vTo != null) _vTo = rotateVector(_vTo, axis, cosA, sinA);
            _vUp = rotateVector(_vUp, axis, cosA, sinA);

            return this;
        }

        /**
         * Helper method to rotate a vector using Rodrigues' rotation formula.
         */
        private Vector rotateVector(Vector v, Vector k, double cosA, double sinA) {
            Vector vRot = v.scale(cosA);

            Vector cross = k.crossProduct(v);
            vRot = vRot.add(cross.scale(sinA));

            double dot = k.dotProduct(v);
            if (!isZero(dot)) {
                vRot = vRot.add(k.scale(dot * (1 - cosA)));
            }

            return vRot;
        }

        /**
         * Sets the ImageWriter for the camera.
         * @param imageWriter The image writer to use.
         * @return The builder instance.
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            this._camera._imageWriter = imageWriter;
            return this;
        }

        /**
         * Sets the RayTracer using a Scene and a RayTracerType.
         * This matches the signature required by RenderTests.
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                this._camera._rayTracer = new SimpleRayTracer(scene);
            }
            return this;
        }
    }

    /**
     * Renders the image by casting rays through all pixels.
     * @return The camera object itself
     */
    public Camera renderImage() {
        // Validation: ensure all resources are provided
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "");
        if (_rayTracer == null)
            throw new MissingResourceException("Missing RayTracer", "Camera", "");

        // Iterate over all pixels (i for rows, j for columns)
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    /**
     * Helper method to cast a ray through a pixel and color it.
     * @param j column index
     * @param i row index
     */
    private void castRay(int j, int i) {
        // Using your existing constructRay(j, i) method
        Ray ray = constructRay(j, i);
        // Trace the ray to get the color
        Color pixelColor = _rayTracer.traceRay(ray);
        // Write the pixel to the image
        _imageWriter.writePixel(j, i, pixelColor);
    }

    /**
     * Prints a grid on top of the existing image.
     * @param interval The size of each grid square
     * @param color    The color of the grid lines
     * @return The camera object itself
     */
    public Camera printGrid(int interval, Color color) {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                // If the coordinate is a multiple of interval, it's a grid line
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Finalizes the image creation by calling the image writer with a file name.
     * @param fileName the name of the output file
     */
    public void writeToImage(String fileName) {
        if (_imageWriter == null)
            throw new java.util.MissingResourceException("Missing ImageWriter", "Camera", "");

        _imageWriter.writeToImage(fileName);
    }

}