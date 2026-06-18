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
 * Production Code: Matte table shortened by 2 rows (gridZ = 12).
 * Clean version: No trees, no pyramids, just the main table and objects.
 */
public class CheckerboardTest {

    private final Scene _scene = new Scene("3D Matte Shortened Table - Clean Setup");

    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    @Test
    @SuppressWarnings("java:S109")
    void test3DCheckerboardWithFruitsAndTableLegs() {
        // --- Materials Setup (Matte - No reflections to prevent shadows from clipping) ---
        Material tableMat = new Material().setKd(0.6).setKs(0.1).setShininess(10);

        // Material of the elements on the table
        Material appleMat = new Material().setKd(0.7).setKs(0.2).setShininess(30);
        Material fruitMat = new Material().setKd(0.7).setKs(0.2).setShininess(15);
        Material leafMat = new Material().setKd(0.8).setKs(0.0).setShininess(1);
        Material vaseMat = new Material().setKd(0.7).setKs(0.1).setShininess(20);
        Material flowerMat = new Material().setKd(0.7).setKs(0.2).setShininess(30);

        // Materials for the glass, drink, and straw
        Material glassMat = new Material().setKd(0.05).setKs(0.95).setShininess(250).setKT(0.85).setKR(0.25);
        Material drinkMat = new Material().setKd(0.7).setKs(0.3).setShininess(80).setKT(0.15);
        Material strawMat = new Material().setKd(0.1).setKs(0.9).setShininess(200).setKT(0.7).setKR(0.1);

        // White material for the table legs
        Material legMat = new Material().setKd(0.6).setKs(0.2).setShininess(50);

        // Color definitions
        Color whiteColor = new Color(240, 240, 250);
        Color pinkishRedColor = new Color(240, 100, 120);
        Color floorColor = new Color(20, 22, 25);
        Color apricotOrange = new Color(250, 130, 40);
        Color leafGreen = new Color(20, 140, 20);
        Color darkLeafGreen = new Color(10, 75, 10);
        Color pineappleYellow = new Color(220, 170, 15);
        Color legWhite = new Color(220, 220, 225);

        // Vase and earth colors
        Color vaseTurquoise = new Color(30, 180, 150);
        Color earthBrown = new Color(75, 45, 25);
        Color stemGreen = new Color(34, 139, 34);
        Color lightBlueFlower = new Color(135, 206, 250);

        Color glassColor = new Color(30, 35, 40);
        Color drinkDeepPurple = new Color(80, 0, 120);
        Color strawGlassColor = new Color(40, 45, 50);

        // 1. ENVIRONMENT: Studio Floor (Plane)
        double floorY = -140;
        _scene.geometries.add(
                new Plane(new Point(0, floorY, 0), new Vector(0, 1, 0))
                        .setEmission(floorColor)
                        .setMaterial(new Material().setKd(0.4).setKs(0.1).setShininess(10))
        );

        // 2. DYNAMICALLY BUILD AN 8x12 SHORT MATTE CHECKERBOARD TABLE
        int gridX = 8;
        int gridZ = 12;
        double squareSize = 20;
        double thickness = 8;

        double startX = -((gridX * squareSize) / 2.0);
        double startZ = -100 - ((gridZ * squareSize) / 2.0);

        double bottomY = -40;
        double topY = bottomY + thickness; // -32

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridZ; j++) {
                double x1 = startX + i * squareSize;
                double x2 = x1 + squareSize;
                double z1 = startZ + j * squareSize;
                double z2 = z1 + squareSize;

                boolean isWhite = (i + j) % 2 == 0;
                Color cubeColor = isWhite ? whiteColor : pinkishRedColor;

                _scene.geometries.add(
                        new Triangle(new Point(x1, topY, z1), new Point(x2, topY, z1), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat),
                        new Triangle(new Point(x2, topY, z1), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat),
                        new Triangle(new Point(x1, bottomY, z2), new Point(x2, bottomY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat),
                        new Triangle(new Point(x2, bottomY, z2), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat)
                );
            }
        }

        // ==========================================
        // 2.4 TABLE LEGS
        // ==========================================
        double legRadius = 3.5;
        double legHeight = bottomY - floorY;
        Vector upVector = new Vector(0, 1, 0);

        double offset = 6;
        double minX = startX + offset;
        double maxX = startX + (gridX * squareSize) - offset;
        double minZ = startZ + offset;
        double maxZ = startZ + (gridZ * squareSize) - offset;

        _scene.geometries.add(
                new Cylinder(legRadius, new Ray(new Point(minX, floorY, minZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                new Cylinder(legRadius, new Ray(new Point(maxX, floorY, minZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                new Cylinder(legRadius, new Ray(new Point(minX, floorY, maxZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat),
                new Cylinder(legRadius, new Ray(new Point(maxX, floorY, maxZ), upVector), legHeight)
                        .setEmission(legWhite).setMaterial(legMat)
        );

        // 2.5 THE APRICOT
        double appleRadius = 15;
        Point appleCenter = new Point(-30, topY + appleRadius, -100);

        _scene.geometries.add(
                new Sphere(appleCenter, appleRadius)
                        .setEmission(apricotOrange)
                        .setMaterial(appleMat),

                new Triangle(
                        new Point(-30, topY + (appleRadius * 2), -100),
                        new Point(-18, topY + (appleRadius * 2) + 6, -95),
                        new Point(-32, topY + (appleRadius * 2) + 4, -105)
                ).setEmission(leafGreen).setMaterial(leafMat)
        );

        // ==========================================
        // 2.5.1 BROWN BALL IN THE TOP-LEFT CORNER
        // ==========================================
        Material brownBallMat = new Material().setKd(0.6).setKs(0.1).setShininess(15);
        Color ballBrownColor = new Color(110, 65, 35); // Chocolate brown

        double brownBallRadius = 12;
        Point brownBallCenter = new Point(-60, topY + 80, -140);

        _scene.geometries.add(
                new Sphere(brownBallCenter, brownBallRadius)
                        .setEmission(ballBrownColor)
                        .setMaterial(brownBallMat)
        );

        // ==========================================
        // 2.5.2 BLUE BALL IN THE TOP-LEFT CORNER
        // ==========================================
        Material blueBallMat = new Material().setKd(0.6).setKs(0.1).setShininess(15);
        Color ballBlueColor = new Color(65, 105, 225); // Royal blue

        double blueBallRadius = 20;
        Point blueBallCenter = new Point(-90, topY + 50, -140);

        _scene.geometries.add(
                new Sphere(blueBallCenter, blueBallRadius)
                        .setEmission(ballBlueColor)
                        .setMaterial(blueBallMat)
        );

        // ==========================================
        // 2.5.3 STONE BALL IN THE TOP-LEFT CORNER
        // ==========================================
        Material stoneBallMat = new Material().setKd(0.6).setKs(0.1).setShininess(15);
        Color ballStoneColor = new Color(225, 215, 195);

        double stoneBallRadius = 32;
        Point stoneBallCenter = new Point(-130, topY + 30, -140);

        _scene.geometries.add(
                new Sphere(stoneBallCenter, stoneBallRadius)
                        .setEmission(ballStoneColor)
                        .setMaterial(stoneBallMat)
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

        // Pineapple crown leaves
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
                new Cylinder(glassRadius, new Ray(new Point(glassX, glassBaseY, glassZ), upVector), glassHeight)
                        .setEmission(glassColor)
                        .setMaterial(glassMat),

                new Cylinder(glassRadius - 1.5, new Ray(new Point(glassX, glassBaseY + 0.5, glassZ), upVector), glassHeight * 0.75)
                        .setEmission(drinkDeepPurple)
                        .setMaterial(drinkMat),

                new Cylinder(1.5, new Ray(new Point(glassX - 3, glassBaseY + 1, glassZ - 3), new Vector(0.35, 1.0, 0.15).normalize()), glassHeight + 8)
                        .setEmission(strawGlassColor)
                        .setMaterial(strawMat)
        );

        // ======================================================================
        // 2.8 TURQUOISE POT WITH EARTH & 10 LIGHT-BLUE FLOWERS
        // ======================================================================
        double vaseX = 0;
        double vaseZ = -160;
        double vaseBaseY = topY;
        double vaseOuterRadius = 18;
        double vaseHeight = 38;
        double wallThickness = 1.2;
        double vaseInnerRadius = vaseOuterRadius - wallThickness;

        _scene.geometries.add(
                new Cylinder(vaseOuterRadius, new Ray(new Point(vaseX, vaseBaseY, vaseZ), upVector), vaseHeight)
                        .setEmission(vaseTurquoise)
                        .setMaterial(vaseMat)
        );

        // Earth plug at the vase opening
        _scene.geometries.add(
                new Cylinder(vaseInnerRadius, new Ray(new Point(vaseX, vaseBaseY + vaseHeight - 1.5, vaseZ), upVector), 1.5)
                        .setEmission(earthBrown)
                        .setMaterial(new Material().setKd(0.5).setKs(0.0).setShininess(2))
        );

        Point stemsStartPoint = new Point(vaseX, vaseBaseY + vaseHeight - 0.5, vaseZ);
        double stemRadius = 0.7;
        double flowerRadius = 3.8;

        for (int i = 0; i < 10; i++) {
            double angle = i * 2.39996;
            double spread = 0.08 + (i * 0.025);

            double dirX = Math.cos(angle) * spread;
            double dirZ = Math.sin(angle) * spread;
            double dirY = 1.0 - (spread * 0.15);

            Vector dir = new Vector(dirX, dirY, dirZ).normalize();
            double len = 25.0 + (i * 4.3) % 25.0;

            _scene.geometries.add(
                    new Cylinder(stemRadius, new Ray(stemsStartPoint, dir), len)
                            .setEmission(stemGreen)
                            .setMaterial(leafMat)
            );

            Point flowerCenter = stemsStartPoint.add(dir.scale(len));
            _scene.geometries.add(
                    new Sphere(flowerCenter, flowerRadius)
                            .setEmission(lightBlueFlower)
                            .setMaterial(flowerMat)
            );
        }

        // 2.9 CHAOTIC ARTISTIC BACKGROUND
        java.util.Random rnd = new java.util.Random(12345);

        // 3. STUDIO LIGHTING SETUP
        _scene.setAmbientLight(new AmbientLight(new Color(0, 0, 0)));

        _scene.lights.add(
                new SpotLight(new Color(220, 200, 180), new Point(-90, 150, 80), new Vector(0.6, -1.1, -0.9))
                        .setKl(3E-5).setKq(1E-7)
        );

        // 4. CAMERA SETUP
        _cameraBuilder
                .setLocation(new Point(143.43, 176.32, 188.19))
                .setDirection(new Point(0, -36, -100), Vector.AXIS_Y)
                .setVpDistance(300)
                .setVpSize(180, 180)
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("3DCheckerboardFruitStudioRotated");
    }
}