package renderer;

import static java.awt.Color.RED;
import org.junit.jupiter.api.Test;
import geometries.impl.*;
import lighting.*;
import primitives.*;
import scene.Scene;
import java.util.Random;

@SuppressWarnings("java:S109")
public class WhiteBlueShadowTest {

    private final Scene _scene = new Scene("Sunset Sailboat Scene");

    // FIX 1: Move Camera much further back and set a wide View Plane
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setLocation(new Point(0, 100, 1500)) // Moved back to 1500
            .setVpDistance(1000)
            .setVpSize(600, 300) // Wider view plane for a landscape feel
            .setDirection(new Point(0, 0, -1000), Vector.AXIS_Y) // Looking towards the horizon
            .setResolution(1000, 500)
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    private void renderSceneToImage(String pictName) {
        _cameraBuilder.build().renderImage().writeToImage(pictName);
    }

    @Test
    void testTrianglesSphereWhiteBlueBackground() {
        // ========== BACKGROUND (Soft Sunset) ==========
        _scene.setBackground(new Color(255, 180, 130));
        _scene.setAmbientLight(new AmbientLight(new Color(255, 150, 100), 0.1));

        // ========== THE SUN (Further away and smaller) ==========
        Point sunPos = new Point(300, 200, -1500);
        _scene.lights.add(new PointLight(new Color(600, 400, 200), sunPos)
                .setKl(0.00001).setKq(0.000001));

        _scene.geometries.add(new Sphere(sunPos, 80)
                .setEmission(new Color(255, 255, 240))
                .setMaterial(new Material().setKd(0.2).setKs(0.2).setShininess(10).setKT(0.7)));

        // ========== THE SEA (Large and Reflective) ==========
        // Increase KR for reflections if your project supports it
        Material seaMaterial = new Material().setKd(0.3).setKs(0.7).setShininess(100).setKR(0.3);
        _scene.geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setEmission(new Color(20, 80, 100))
                .setMaterial(seaMaterial));

        // ========== THE SAILBOAT (Smaller and Further Back) ==========
        buildSailboat(new Point(-250, 0, -300));

        // ========== THE GOLDEN SPHERE PATH ==========
        Material goldMat = new Material().setKd(0.5).setKs(0.8).setShininess(100);
        Color goldColor = new Color(255, 215, 0);
        Random rand = new Random(42);

        // FIX 2: Better sphere path logic
        // We create spheres along a line from the camera to the sun
        for (int z = -1400; z < 800; z += 25) {
            // This math makes the path point toward the sun at x=300
            double centerX = (z + 1400) * 0.15 - 50;

            // Width of the path (gets wider as it approaches camera)
            double spread = (800 - z) * 0.08;

            for (int i = 0; i < 4; i++) {
                double x = centerX + (rand.nextDouble() - 0.5) * spread;
                double radius = rand.nextDouble() * 5 + 2;
                // Place spheres just above the water (y=0)
                _scene.geometries.add(new Sphere(new Point(x, radius, z), radius)
                        .setEmission(goldColor)
                        .setMaterial(goldMat));
            }
        }

        renderSceneToImage("SunsetFinal");
    }

    private void buildSailboat(Point pos) {
        Material boatMat = new Material().setKd(0.5).setKs(0.5).setShininess(30);

        // Hull
        Point hL = pos.add(new Vector(-60, 0, 0));
        Point hR = pos.add(new Vector(60, 0, 0));
        Point hC = pos.add(new Vector(0, 30, 0));
        Point hFront = pos.add(new Vector(100, 40, -20));

        _scene.geometries.add(
                new Triangle(hL, hR, hC).setEmission(new Color(0, 100, 120)).setMaterial(boatMat),
                new Triangle(hR, hFront, hC).setEmission(new Color(150, 100, 40)).setMaterial(boatMat)
        );

        // Mast
        _scene.geometries.add(new Cylinder(2, new Ray(pos.add(new Vector(0,30,0)), new Vector(0,1,0)), 140)
                .setEmission(new Color(40, 20, 0)).setMaterial(boatMat));

        // Sails
        Point mastTop = pos.add(new Vector(0, 160, 0));
        Point mastMid = pos.add(new Vector(0, 40, 0));

        _scene.geometries.add(
                new Triangle(mastTop, mastMid, pos.add(new Vector(-70, 50, 0)))
                        .setEmission(new Color(200, 80, 40)).setMaterial(boatMat),
                new Triangle(mastTop, mastMid, pos.add(new Vector(50, 50, -20)))
                        .setEmission(new Color(220, 180, 60)).setMaterial(boatMat)
        );
    }
}