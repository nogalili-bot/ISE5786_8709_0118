package renderer;

import org.junit.jupiter.api.Test;
import geometries.impl.Triangle;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Cylinder;
import lighting.AmbientLight;
import lighting.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Custom creative test featuring a balanced fruit studio with an opaque glass
 * of purple drink, sitting on a complete blue-checkerboard table supported by 4 legs.
 */
public class CheckerboardTest {

    private final Scene _scene = new Scene("3D Solid Blue Checkerboard Scene");

    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    @Test
    @SuppressWarnings("java:S109")
    void test3DCheckerboardWithFruitsAndTableLegs() {
        // --- Materials & Styling Setup ---
        Material whiteGlass = new Material().setKd(0.1).setKs(0.9).setShininess(150).setKT(0.6);
        Material blueMirror = new Material().setKd(0.2).setKs(0.7).setShininess(150).setKR(0.6);

        // חומרי הפירות
        Material appleMat = new Material().setKd(0.7).setKs(0.2).setShininess(30);
        Material fruitMat = new Material().setKd(0.7).setKs(0.2).setShininess(15);
        Material leafMat = new Material().setKd(0.8).setKs(0.0).setShininess(1);

        // חומרי הכוס, המשקה והקש
        Material glassMat = new Material().setKd(0.05).setKs(0.95).setShininess(250).setKT(0.85).setKR(0.25);
        Material drinkMat = new Material().setKd(0.7).setKs(0.3).setShininess(80).setKT(0.15);
        Material strawMat = new Material().setKd(0.1).setKs(0.9).setShininess(200).setKT(0.7).setKR(0.1);

        // חומר מט-מבריק קל עבור רגלי השולחן הלבנות
        Material legMat = new Material().setKd(0.6).setKs(0.2).setShininess(50);

        Color whiteColor = new Color(240, 240, 250);
        Color blueColor = new Color(30, 90, 180);
        Color floorColor = new Color(20, 22, 25);
        Color appleRed = new Color(190, 10, 10);
        Color leafGreen = new Color(20, 140, 20);
        Color darkLeafGreen = new Color(10, 75, 10);
        Color pineappleYellow = new Color(220, 170, 15);
        Color legWhite = new Color(220, 220, 225);     // לבן נקי עבור הרגליים

        Color glassColor = new Color(30, 35, 40);
        Color drinkDeepPurple = new Color(80, 0, 120);
        Color strawGlassColor = new Color(40, 45, 50);

        // 1. ENVIRONMENT: Studio Floor (Plane)
        double floorY = -60;
        _scene.geometries.add(
                new Plane(new Point(0, floorY, 0), new Vector(0, 1, 0))
                        .setEmission(floorColor)
                        .setMaterial(new Material().setKd(0.4).setKs(0.1).setShininess(10))
        );

        // 2. DYNAMICALLY BUILD AN 8x8 SOLID 3D CHECKERBOARD FROM CUBES
        int gridSize = 8;
        double squareSize = 20;
        double thickness = 8;

        double startX = -((gridSize * squareSize) / 2.0); // -80
        double startZ = -100 - ((gridSize * squareSize) / 2.0); // -180

        double bottomY = -40;
        double topY = bottomY + thickness; // -32

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x1 = startX + i * squareSize;
                double x2 = x1 + squareSize;
                double z1 = startZ + j * squareSize;
                double z2 = z1 + squareSize;

                boolean isWhite = (i + j) % 2 == 0;
                Color cubeColor = isWhite ? whiteColor : blueColor;
                Material cubeMat = isWhite ? whiteGlass : blueMirror;

                _scene.geometries.add(
                        new Triangle(new Point(x1, topY, z1), new Point(x2, topY, z1), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(cubeMat),
                        new Triangle(new Point(x2, topY, z1), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(cubeMat),
                        new Triangle(new Point(x1, bottomY, z2), new Point(x2, bottomY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(cubeMat),
                        new Triangle(new Point(x2, bottomY, z2), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(cubeMat)
                );
            }
        }

        // ==========================================
        // 2.4 TABLE LEGS ADDITION (התוספת של הרגליים)
        // ==========================================
        double legRadius = 3.5;
        double legHeight = bottomY - floorY; // יורד מגובה -40 לגובה הרצפה -60 (אורך של 20 יחידות)
        Vector upVector = new Vector(0, 1, 0);

        // חישוב קצוות השולחן עם היסט פנימה (Offset) של 6 יחידות כדי שהרגליים יישבו יפה מתחת לפינות
        double offset = 6;
        double minX = startX + offset;
        double maxX = startX + (gridSize * squareSize) - offset;
        double minZ = startZ + offset;
        double maxZ = startZ + (gridSize * squareSize) - offset;

        _scene.geometries.add(
                // רגל אחורית שמאלית
                new Cylinder(legRadius, new Ray(new Point(minX, floorY, minZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                // רגל אחורית ימנית
                new Cylinder(legRadius, new Ray(new Point(maxX, floorY, minZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                // רגל קדמית שמאלית
                new Cylinder(legRadius, new Ray(new Point(minX, floorY, maxZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                // רגל קדמית ימנית
                new Cylinder(legRadius, new Ray(new Point(maxX, floorY, maxZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat)
        );

        // 2.5 THE APPLE (Sphere + Triangle Leaf)
        double appleRadius = 15;
        Point appleCenter = new Point(-30, topY + appleRadius, -100);

        _scene.geometries.add(
                new Sphere(appleCenter, appleRadius)
                        .setEmission(appleRed)
                        .setMaterial(appleMat),

                new Triangle(
                        new Point(-30, topY + (appleRadius * 2), -100),
                        new Point(-18, topY + (appleRadius * 2) + 6, -95),
                        new Point(-32, topY + (appleRadius * 2) + 4, -105)
                ).setEmission(leafGreen).setMaterial(leafMat)
        );

        // 2.6 PINEAPPLE BUILD
        double pineX = 30;
        double pineZ = -100;
        double pineBaseY = topY;

        int verticalSegments = 8;
        int radialSegments = 10;
        double pineRadius = 16;
        double pineHeight = 36;

        for (int h = 0; h < verticalSegments; h++) {
            double y1 = pineBaseY + ((double) h / verticalSegments) * pineHeight;
            double y2 = pineBaseY + ((double) (h + 1) / verticalSegments) * pineHeight;

            double sin1 = Math.sin(((double) h / verticalSegments) * Math.PI);
            double sin2 = Math.sin(((double) (h + 1) / verticalSegments) * Math.PI);
            double r1 = pineRadius * (0.4 + 0.6 * sin1);
            double r2 = pineRadius * (0.4 + 0.6 * sin2);

            for (int r = 0; r < radialSegments; r++) {
                double angle1 = ((double) r / radialSegments) * 2 * Math.PI;
                double angle2 = ((double) (r + 1) / radialSegments) * 2 * Math.PI;

                Point p1 = new Point(pineX + r1 * Math.cos(angle1), y1, pineZ + r1 * Math.sin(angle1));
                Point p2 = new Point(pineX + r1 * Math.cos(angle2), y1, pineZ + r1 * Math.sin(angle2));
                Point p3 = new Point(pineX + r2 * Math.cos(angle1), y2, pineZ + r2 * Math.sin(angle1));
                Point p4 = new Point(pineX + r2 * Math.cos(angle2), y2, pineZ + r2 * Math.sin(angle2));

                _scene.geometries.add(
                        new Triangle(p1, p2, p3).setEmission(pineappleYellow).setMaterial(fruitMat),
                        new Triangle(p2, p4, p3).setEmission(pineappleYellow).setMaterial(fruitMat)
                );
            }
        }

        // כתר עלי האננס
        double crownY = pineBaseY + pineHeight;
        for (int r = 0; r < radialSegments; r++) {
            double angle = ((double) r / radialSegments) * 2 * Math.PI;
            double nextAngle = ((double) (r + 1) / radialSegments) * 2 * Math.PI;

            Point b1 = new Point(pineX + (pineRadius * 0.3) * Math.cos(angle), crownY, pineZ + (pineRadius * 0.3) * Math.sin(angle));
            Point b2 = new Point(pineX + (pineRadius * 0.3) * Math.cos(nextAngle), crownY, pineZ + (pineRadius * 0.3) * Math.sin(nextAngle));

            double leafOut = pineRadius * 0.8;
            double leafHeight = 20 + (r % 2 == 0 ? 6 : 0);
            Point tip = new Point(pineX + leafOut * Math.cos(angle + 0.3), crownY + leafHeight, pineZ + leafOut * Math.sin(angle + 0.3));

            _scene.geometries.add(
                    new Triangle(b1, b2, tip).setEmission(darkLeafGreen).setMaterial(leafMat)
            );
        }

        // 2.7 GLASS OF PURPLE DRINK WITH STRAW
        double glassX = 0;
        double glassZ = -55;
        double glassBaseY = topY;
        double glassRadius = 16;
        double glassHeight = 32;

        _scene.geometries.add(
                // 1. גוף כוס הזכוכית הגדולה והעבה
                new Cylinder(glassRadius, new Ray(new Point(glassX, glassBaseY, glassZ), upVector), glassHeight)
                        .setEmission(glassColor)
                        .setMaterial(glassMat),

                // 2. המשקה הסגול הכהה והאטום
                new Cylinder(glassRadius - 1.5, new Ray(new Point(glassX, glassBaseY + 0.5, glassZ), upVector), glassHeight * 0.75)
                        .setEmission(drinkDeepPurple)
                        .setMaterial(drinkMat),

                // 3. הקש הזכוכיתי
                new Cylinder(1.5, new Ray(new Point(glassX - 3, glassBaseY + 1, glassZ - 3), new Vector(0.35, 1.0, 0.15).normalize()), glassHeight + 8)
                        .setEmission(strawGlassColor)
                        .setMaterial(strawMat)
        );

        // 3. STUDIO LIGHTING SETUP
        _scene.setAmbientLight(new AmbientLight(new Color(30, 30, 35)));

        _scene.lights.add(
                new SpotLight(new Color(220, 200, 180), new Point(-90, 150, 80), new Vector(0.6, -1.1, -0.9))
                        .setKl(3E-5).setKq(1E-7)
        );

        // 4. CAMERA SETUP
        _cameraBuilder
                .setLocation(new Point(0, 100, 240))
                .setDirection(new Point(0, -36, -100), Vector.AXIS_Y)
                .setVpDistance(300)
                .setVpSize(180, 180)
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("3DCheckerboardFruitStudioFinal");
    }
}