package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import java.util.Random;

public class CustomImageTest {

    private final Scene _scene = new Scene("Geometric Sunset Scene");
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    @Test
    public void generateCustomImage() {
        Random rand = new Random();

        // --- 1. Background & Global Lighting ---
        _scene.setBackground(new Color(135, 206, 235)); // Soft Blue Sky
        _scene.setAmbientLight(new AmbientLight(new Color(255, 230, 200), 0.1));

        // --- 2. Materials (Advanced Phong) ---
        Material waterMat = new Material().setKd(0.1).setKs(0.9).setShininess(1000).setKR(0.3);
        Material goldMat = new Material().setKd(0.3).setKs(1.0).setShininess(500).setKR(0.2);
        Material sailMat = new Material().setKd(0.5).setKs(0.5).setShininess(100);
        Material woodMat = new Material().setKd(0.4).setKs(0.2).setShininess(20);

        // --- 3. The Sea ---
        _scene.geometries.add(
                new Plane(new Point(0, -20, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(0, 80, 120))
                        .setMaterial(waterMat)
        );

        // --- 4. The Sun (The Light Source) ---
        Point sunPos = new Point(0, 250, -1000);
        _scene.geometries.add(
                new Sphere(sunPos, 120)
                        .setEmission(new Color(255, 255, 255)) // Bright White Core
                        .setMaterial(new Material().setKT(0.8))
        );
        // Sun Light
        _scene.lights.add(new DirectionalLight(new Color(255, 200, 150), new Vector(0, -0.2, -1)));
        _scene.lights.add(new PointLight(new Color(400, 300, 200), sunPos).setKl(0.000001).setKq(0.0000001));

        // --- 5. The Golden Path (Dense & Varied) ---
        for (int z = -150; z > -900; z -= 10) {
            double width = Math.pow(Math.abs(z), 0.7) * 0.5;
            int spheresInRow = (int) (width / 5) + 1;

            for (int i = 0; i < spheresInRow; i++) {
                double x = (rand.nextDouble() - 0.5) * width * 2;
                double radius = 1 + rand.nextDouble() * 4;
                _scene.geometries.add(
                        new Sphere(new Point(x, -20 + radius, z), radius)
                                .setEmission(new Color(255, 215, 0).reduce((int)((rand.nextDouble() * 0.5 + 0.5) * 100)))
                                .setMaterial(goldMat)
                );
            }
            // Add a few point lights along the path to make the water glow
            if (z % 100 == 0) {
                _scene.lights.add(new PointLight(new Color(150, 100, 0), new Point(0, -10, z)).setKl(0.01).setKq(0.005));
            }
        }

        // --- 6. The Mosaic Sailboat ---
        double bZ = -250;
        double bY = -20;

        // --- Hull (Polygonal look) ---
        Point p1 = new Point(-40, bY + 15, bZ + 30);
        Point p2 = new Point(40, bY + 15, bZ + 30);
        Point p3 = new Point(50, bY + 25, bZ - 50);
        Point p4 = new Point(-50, bY + 25, bZ - 50);
        Point pBottom = new Point(0, bY, bZ);

        _scene.geometries.add(
                new Polygon(p1, p2, p3, p4).setEmission(new Color(40, 120, 160)).setMaterial(woodMat), // Top Deck
                new Triangle(p1, p2, pBottom).setEmission(new Color(20, 80, 110)).setMaterial(woodMat), // Rear
                new Triangle(p2, p3, pBottom).setEmission(new Color(30, 100, 130)).setMaterial(woodMat), // Right
                new Triangle(p3, p4, pBottom).setEmission(new Color(10, 60, 90)).setMaterial(woodMat), // Front
                new Triangle(p4, p1, pBottom).setEmission(new Color(30, 100, 130)).setMaterial(woodMat)  // Left
        );

        // --- Mast ---
        _scene.geometries.add(new Cylinder(2.5, new Ray(new Point(0, bY + 15, bZ - 10), new Vector(0, 1, 0)), 120)
                .setEmission(new Color(50, 30, 10)).setMaterial(woodMat));

        // --- Mosaic Sails (Multiple triangles for the geometric look) ---
        Color[] colors = { new Color(0, 100, 150), new Color(255, 100, 0), new Color(200, 150, 50), new Color(255, 255, 200) };

        // Main Sail (Right) - Built from smaller triangles
        for (int i = 0; i < 5; i++) {
            double height = 20 * i;
            _scene.geometries.add(
                    new Triangle(new Point(0, bY + 40 + height, bZ - 10), new Point(0, bY + 60 + height, bZ - 10), new Point(50 - (i*8), bY + 45 + height, bZ - 10))
                            .setEmission(colors[i % colors.length]).setMaterial(sailMat)
            );
        }

        // Small Sail (Left)
        for (int i = 0; i < 4; i++) {
            double height = 25 * i;
            _scene.geometries.add(
                    new Triangle(new Point(-2, bY + 30 + height, bZ - 10), new Point(-2, bY + 55 + height, bZ - 10), new Point(-40 + (i*5), bY + 35 + height, bZ - 20))
                            .setEmission(colors[(i+1) % colors.length]).setMaterial(sailMat)
            );
        }

        // --- 7. Additional Lighting for Shadows and Contrast ---
        // Side highlight to define the boat shape
        _scene.lights.add(new SpotLight(new Color(255, 255, 255), new Point(300, 200, 100), new Vector(-1, -0.5, -0.5))
                .setKl(0.0001).setKq(0.00001));

        // Soft blue light from the opposite side to soften shadows
        _scene.lights.add(new PointLight(new Color(50, 50, 150), new Point(-200, 100, -100))
                .setKl(0.0001).setKq(0.00001));

        // --- 8. Camera ---
        _cameraBuilder
                .setLocation(new Point(180, 100, 400)) // Higher and further for better perspective
                .setDirection(new Point(0, 20, -150), Vector.AXIS_Y)
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setResolution(1000, 1000)
                .build()
                .renderImage()
                .writeToImage("AdvancedGeometricBoat");
    }
}