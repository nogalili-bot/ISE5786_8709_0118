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
import java.util.List;

public class CheckerboardTest3 {

    private final Scene _scene = new Scene("3D Matte Shortened Table - DoF Setup");

    @Test
    @SuppressWarnings("java:S109")
    void test3DCheckerboardWithDepthOfField() {
        System.out.println(">>> SUCCESS: WE ARE INSIDE THE CORRECT TEST! <<<");

        // --- Materials Setup ---
        Material tableMat = new Material().setKd(0.6).setKs(0.1).setShininess(10);
        Material appleMat = new Material().setKd(0.7).setKs(0.2).setShininess(30);
        Material fruitMat = new Material().setKd(0.7).setKs(0.2).setShininess(15);
        Material leafMat = new Material().setKd(0.8).setKs(0.0).setShininess(1);
        Material vaseMat = new Material().setKd(0.7).setKs(0.1).setShininess(20);
        Material flowerMat = new Material().setKd(0.7).setKs(0.2).setShininess(30);
        Material glassMat = new Material().setKd(0.05).setKs(0.95).setShininess(250).setKT(0.85).setKR(0.25);
        Material drinkMat = new Material().setKd(0.7).setKs(0.3).setShininess(80).setKT(0.15);
        Material strawMat = new Material().setKd(0.1).setKs(0.9).setShininess(200).setKT(0.7).setKR(0.1);
        Material legMat = new Material().setKd(0.6).setKs(0.2).setShininess(50);

        // חומר חדש ייעודי לשלושת הכדורים שאנחנו רוצים לטשטש באמצעות ה-Aperture
        Material blurryBallMat = new Material().setKd(0.6).setKs(0.1).setShininess(15).setBlurry(true);

        // הגדרות צבעים
        Color whiteColor = new Color(240, 240, 250);
        Color pinkishRedColor = new Color(50, 0, 75);
        Color floorColor = new Color(20, 22, 25);
        Color apricotOrange = new Color(250, 130, 40);
        Color leafGreen = new Color(20, 140, 20);
        Color darkLeafGreen = new Color(10, 75, 10);
        Color pineappleYellow = new Color(220, 170, 15);
        Color legWhite = new Color(220, 220, 225);
        Color vaseTurquoise = new Color(30, 180, 150);
        Color earthBrown = new Color(75, 45, 25);
        Color stemGreen = new Color(34, 139, 34);
        Color lightBlueFlower = new Color(135, 206, 250);
        Color glassColor = new Color(30, 35, 40);
        Color drinkDeepPurple = new Color(80, 0, 0);
        Color strawGlassColor = new Color(40, 45, 50);

        // 1. ENVIRONMENT: Studio Floor (Plane)
        double floorY = -140;
        _scene.geometries.add(
                new Plane(new Point(0, floorY, 0), new Vector(0, 1, 0))
                        .setEmission(floorColor)
                        .setMaterial(new Material().setKd(0.4).setKs(0.1).setShininess(10))
        );

        // 2. DYNAMICALLY BUILD AN 8x12 SHORT MATTE CHECKERBOARD TABLE WITH CLOSED EDGES
        int gridX = 8;
        int gridZ = 12;
        double squareSize = 20;
        double thickness = 8;
        double startX = -((gridX * squareSize) / 2.0);
        double startZ = -100 - ((gridZ * squareSize) / 2.0);
        double bottomY = -40;
        double topY = bottomY + thickness;

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridZ; j++) {
                double x1 = startX + i * squareSize;
                double x2 = x1 + squareSize;
                double z1 = startZ + j * squareSize;
                double z2 = z1 + squareSize;
                boolean isWhite = (i + j) % 2 == 0;
                Color cubeColor = isWhite ? whiteColor : pinkishRedColor;

                // 1. משטח עליון של השולחן
                _scene.geometries.add(
                        new Triangle(new Point(x1, topY, z1), new Point(x2, topY, z1), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat),
                        new Triangle(new Point(x2, topY, z1), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                .setEmission(cubeColor).setMaterial(tableMat)
                );

                // 2. דופן קדמית (הכי קרובה למצלמה - שורת ה-Z האחרונה)
                if (j == gridZ - 1) {
                    _scene.geometries.add(
                            new Triangle(new Point(x1, bottomY, z2), new Point(x2, bottomY, z2), new Point(x1, topY, z2))
                                    .setEmission(cubeColor).setMaterial(tableMat),
                            new Triangle(new Point(x2, bottomY, z2), new Point(x2, topY, z2), new Point(x1, topY, z2))
                                    .setEmission(cubeColor).setMaterial(tableMat)
                    );
                }

                // 3. דופן ימנית (הצד שביקשת לסגור - טור ה-X האחרון)
                if (i == gridX - 1) {
                    _scene.geometries.add(
                            new Triangle(new Point(x2, bottomY, z1), new Point(x2, bottomY, z2), new Point(x2, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat),
                            new Triangle(new Point(x2, bottomY, z2), new Point(x2, topY, z2), new Point(x2, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat)
                    );
                }

                // 4. דופן שמאלית (טור ה-X הראשון)
                if (i == 0) {
                    _scene.geometries.add(
                            new Triangle(new Point(x1, bottomY, z1), new Point(x1, bottomY, z2), new Point(x1, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat),
                            new Triangle(new Point(x1, bottomY, z2), new Point(x1, topY, z2), new Point(x1, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat)
                    );
                }

                // 5. דופן אחורית (הרחוקה ביותר - שורת ה-Z הראשונה)
                if (j == 0) {
                    _scene.geometries.add(
                            new Triangle(new Point(x1, bottomY, z1), new Point(x2, bottomY, z1), new Point(x1, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat),
                            new Triangle(new Point(x2, bottomY, z1), new Point(x2, topY, z1), new Point(x1, topY, z1))
                                    .setEmission(cubeColor).setMaterial(tableMat)
                    );
                }
            }
        }

        // 2.4 TABLE LEGS
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

        // 2.5 THE APRICOT (נשאר חד)
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

        // 2.5.1 BROWN BALL - מטושטש
        Color ballBrownColor = new Color(110, 65, 35);
        double brownBallRadius = 18;
        Point brownBallCenter = new Point(-10, topY + 0, -400);
        _scene.geometries.add(
                new Sphere(brownBallCenter, brownBallRadius)
                        .setEmission(ballBrownColor)
                        .setMaterial(blurryBallMat)
        );

        // 2.5.2 BLUE BALL - מטושטש
        Color ballBlueColor = new Color(65, 105, 225);
        double blueBallRadius = 30;
        Point blueBallCenter = new Point(-150, topY + 40, -400);
        _scene.geometries.add(
                new Sphere(blueBallCenter, blueBallRadius)
                        .setEmission(ballBlueColor)
                        .setMaterial(blurryBallMat)
        );

        // 2.5.3 STONE BALL - מטושטש
        Color ballStoneColor = new Color(225, 215, 195);
        double stoneBallRadius = 50;
        Point stoneBallCenter = new Point(-270, topY - 10, -400);
        _scene.geometries.add(
                new Sphere(stoneBallCenter, stoneBallRadius)
                        .setEmission(ballStoneColor)
                        .setMaterial(blurryBallMat)
        );

        // 2.5.4 PINK BALL - מטושטש
        Color ballPinkColor = new Color(255, 182, 193);
        double pinkBallRadius = 45;
        Point pinkBallCenter = new Point(-95, topY + 20, -400);
        _scene.geometries.add(
                new Sphere(pinkBallCenter, pinkBallRadius)
                        .setEmission(ballPinkColor)
                        .setMaterial(blurryBallMat)
        );

        // 2.6 PINEAPPLE BUILD (נשאר חד)
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

            _scene.geometries.add(new Triangle(b1, b2, tip).setEmission(darkLeafGreen).setMaterial(leafMat));
        }

        // 2.7 GLASS (נשאר חד)
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
                        .setMaterial(drinkMat)
        );

        // 2.8 TURQUOISE POT (נשאר חד)
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
                        .setMaterial(vaseMat),
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

        // 3. STUDIO LIGHTING SETUP
        _scene.setAmbientLight(new AmbientLight(new Color(0, 0, 0)));
        _scene.lights.add(
                new SpotLight(new Color(220, 200, 180), new Point(-90, 150, 80), new Vector(0.6, -1.1, -0.9))
                        .setKl(3E-5).setKq(1E-7)
        );

        // 4. CAMERA SETUP WITH MULTI-THREADING & PROGRESS PRINT
        Camera.getBuilder()
                .setRayTracer(_scene, RayTracerType.SIMPLE)
                .setLocation(new Point(210.0, 210.0, 280.0))
                .setDirection(new Point(0, -36, -100), Vector.AXIS_Y)
                .setVpDistance(300)
                .setVpSize(180, 180)
                .setResolution(1000, 1000)
                .setApertureSize(15.0)
                .setFocalDistance(285)
                .setRootSamples(9)
                .setMultithreading(-2)
                .setDebugPrint(1.0)
                .build()
                .renderImage()
                .writeToImage("3DCheckerboard_With_Selective_DoF");
    }
}