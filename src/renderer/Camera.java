package renderer;

import primitives.*;
import static primitives.Util.*;
import java.util.MissingResourceException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import scene.*;

public class Camera implements Cloneable {
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;

    private double width = 0;
    private double height = 0;
    private double distance = 0;

    private int nX = 1;
    private int nY = 1;

    private Point vpc;
    private double pixelW;
    private double pixelH;

    private ImageWriter _imageWriter;
    private RayTracerBase _rayTracer;

    private double apertureSize = 0;
    private double focalDistance = 100;
    private Sampler sampler = null;

    private int threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval = 0;
    private PixelManager pixelManager;

    private Camera() {}

    public static Builder getBuilder() {
        return new Builder();
    }

    public Point getP0() { return p0; }
    public Vector getvTo() { return vTo; }
    public Vector getvUp() { return vUp; }
    public Vector getvRight() { return vRight; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getDistance() { return distance; }
    public double getApertureSize() { return apertureSize; }
    public double getFocalDistance() { return focalDistance; }

    public Ray constructRay(int j, int i) {
        Point pIJ = vpc;

        double xj = (j - (nX - 1) / 2.0) * pixelW;
        if (!isZero(xj)) {
            pIJ = pIJ.add(vRight.scale(xj));
        }

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

    public static class Builder {
        private final Camera _camera = new Camera();
        private Vector _vTo = null;
        private Point _target = null;
        private Vector _vUp = Vector.AXIS_Y;
        private int rootSamples = 1;

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

        public Builder setApertureSize(double apertureSize) {
            if (apertureSize < 0)
                throw new IllegalArgumentException("Aperture size cannot be negative");
            this._camera.apertureSize = apertureSize;
            return this;
        }

        public Builder setFocalDistance(double focalDistance) {
            if (focalDistance <= 0)
                throw new IllegalArgumentException("Focal distance must be positive");
            this._camera.focalDistance = focalDistance;
            return this;
        }

        public Builder setRootSamples(int rootSamples) {
            if (rootSamples < 1)
                throw new IllegalArgumentException("Root samples must be at least 1");
            this.rootSamples = rootSamples;
            return this;
        }

        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 0 ? 1 : cores;
            } else {
                _camera.threadsCount = threads;
            }
            return this;
        }

        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            if (_camera._imageWriter == null) {
                _camera._imageWriter = new ImageWriter(_camera.nX, _camera.nY);
            }

            if (_camera.apertureSize > 0 && this.rootSamples > 1) {
                _camera.sampler = new Sampler(this.rootSamples);
            }

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

    public Camera renderImage() {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");
        if (_rayTracer == null)
            throw new MissingResourceException("Missing RayTracer", "Camera", "rayTracer");

        pixelManager = new PixelManager(nY, nX, printInterval);

        return switch (threadsCount) {
            case 0  -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    // התיקון הקריטי: סטרים שטוח יחיד ללא קינון שמנצל נכון את הליבות
    private Camera renderImageStream() {
        int totalPixels = nX * nY;
        IntStream.range(0, totalPixels).parallel().forEach(index -> {
            int i = index / nX;
            int j = index % nX;
            castRay(j, i);
        });
        return this;
    }

    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        int currentThreadsCount = threadsCount;

        while (currentThreadsCount-- > 0) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(pixel.col(), pixel.row());
                }
            }));
        }

        for (var thread : threads) thread.start();

        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}

        return this;
    }

    private void castRay(int j, int i) {
        if (sampler == null || isZero(apertureSize)) {
            Ray ray = constructRay(j, i);
            Color pixelColor = _rayTracer.traceRay(ray);
            _imageWriter.writePixel(j, i, pixelColor);
            if (pixelManager != null) {
                pixelManager.pixelDone();
            }
            return;
        }

        Ray primaryRay = constructRay(j, i);
        Point pFocal = primaryRay.getPoint(focalDistance);

        List<Offset2D> offsets = sampler.getOffsets();
        int totalRays = offsets != null ? offsets.size() : 0;

        if (totalRays < 1) {
            Color pixelColor = _rayTracer.traceRay(primaryRay);
            _imageWriter.writePixel(j, i, pixelColor);
            if (pixelManager != null) {
                pixelManager.pixelDone();
            }
            return;
        }

        Color bkgColor = Color.BLACK;

        for (Offset2D offset : offsets) {
            Point pSample = p0;

            if (!isZero(offset.x())) {
                pSample = pSample.add(vRight.scale(offset.x() * apertureSize));
            }
            if (!isZero(offset.y())) {
                pSample = pSample.add(vUp.scale(offset.y() * apertureSize));
            }

            Vector beamDirection = pFocal.subtract(pSample).normalize();
            Ray secondaryRay = new Ray(pSample, beamDirection);

            bkgColor = bkgColor.add(_rayTracer.traceRay(secondaryRay));
        }

        _imageWriter.writePixel(j, i, bkgColor.reduce(totalRays));

        if (pixelManager != null) {
            pixelManager.pixelDone();
        }
    }

    public void writeToImage(String fileName) {
        if (_imageWriter == null)
            throw new MissingResourceException("Missing ImageWriter", "Camera", "imageWriter");
        _imageWriter.writeToImage(fileName);
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
}