package renderer;

import geometries.impl.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

public class CustomImageTest0 {

    private final Scene _scene = new Scene("Geometric Sunset Scene");
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setRayTracer(_scene, RayTracerType.SIMPLE);

    @Test
    public void generateCustomImage() {
        // --- 1. צבעי שקיעה (Phong אוהב ניגודיות) ---
        _scene.setBackground(new Color(255, 200, 150)); // שמיים כתומים
        _scene.setAmbientLight(new AmbientLight(new Color(50, 30, 10), 0.15));

        // --- 2. חומרים (כאן קורה הקסם של פונג) ---
        // מים: השתקפות גבוהה (kR) וברק (Shininess)
        Material waterMat = new Material().setKd(0.2).setKs(0.8).setShininess(500).setKR(0.4);
        // זהב: ברק ספקולרי חזק מאוד (Ks)
        Material goldMat = new Material().setKd(0.4).setKs(1.0).setShininess(1000).setKR(0.2);
        // עץ/מפרש: יותר מט (Kd גבוה, Ks נמוך)
        Material boatMat = new Material().setKd(0.6).setKs(0.3).setShininess(30);

        // --- 3. הים (מישור) ---
        _scene.geometries.add(
                new Plane(new Point(0, -20, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(0, 50, 100)) // כחול עמוק
                        .setMaterial(waterMat)
        );

        // --- 4. השמש (כדור רחוק) ---
        Point sunPos = new Point(0, 100, -800);
        _scene.geometries.add(
                new Sphere(sunPos, 80)
                        .setEmission(new Color(255, 255, 200))
                        .setMaterial(new Material().setKT(0.5))
        );

        // --- 5. שביל כדורי הזהב (מסודר לכיוון השמש) ---
        // אנחנו יוצרים שביל שמתרחב ככל שהוא קרוב למצלמה
        for (int z = -100; z > -700; z -= 15) {
            double width = Math.abs(z) * 0.08; // רוחב השביל גדל עם המרחק
            for (double x = -width; x <= width; x += 15) {
                double radius = 2 + Math.random() * 3;
                _scene.geometries.add(
                        new Sphere(new Point(x + (Math.random()*5), -20 + radius, z), radius)
                                .setEmission(new Color(255, 215, 0)) // צבע זהב
                                .setMaterial(goldMat)
                );
            }
        }

        // --- 6. בניית הסירה (בנייה גיאומטרית ידנית) ---
        double boatZ = -150;
        double boatY = -20;

        // גוף הסירה (Triangle) - צד שמאל, ימין ואחור
        Point bow = new Point(0, boatY + 5, boatZ - 60);       // חרטום
        Point sternL = new Point(-20, boatY + 15, boatZ + 20); // ירכתיים שמאל
        Point sternR = new Point(20, boatY + 15, boatZ + 20);  // ירכתיים ימין
        Point keel = new Point(0, boatY, boatZ);               // תחתית

        _scene.geometries.add(
                new Triangle(bow, sternL, keel).setEmission(new Color(20, 100, 150)).setMaterial(boatMat),
                new Triangle(bow, sternR, keel).setEmission(new Color(10, 80, 130)).setMaterial(boatMat),
                new Triangle(sternL, sternR, keel).setEmission(new Color(50, 30, 10)).setMaterial(boatMat)
        );

        // תורן (Cylinder)
        _scene.geometries.add(
                new Cylinder(1.5, new Ray(new Point(0, boatY + 10, boatZ), new Vector(0, 1, 0)), 80)
                        .setEmission(new Color(60, 40, 20)).setMaterial(boatMat)
        );

        // מפרשים (משולשים צבעוניים כמו בתמונה המקורית)
        _scene.geometries.add(
                // מפרש ראשי
                new Triangle(new Point(0, boatY + 85, boatZ), new Point(0, boatY + 20, boatZ), new Point(40, boatY + 25, boatZ))
                        .setEmission(new Color(255, 100, 0)).setMaterial(boatMat),
                // מפרש קדמי
                new Triangle(new Point(-2, boatY + 75, boatZ), new Point(-2, boatY + 20, boatZ), new Point(-35, boatY + 20, boatZ - 10))
                        .setEmission(new Color(0, 150, 200)).setMaterial(boatMat)
        );

        // --- 7. תאורה (קריטי עבור Phong) ---
        // אור נקודתי מהשמש (יוצר את הנצנוץ על כדורי הזהב)
        _scene.lights.add(new PointLight(new Color(600, 500, 300), sunPos)
                .setKl(0.00001).setKq(0.000001));

        // אור ספוט שמכוון על הסירה כדי להדגיש את הצורה שלה
        _scene.lights.add(new SpotLight(new Color(200, 200, 200), new Point(100, 100, 0), new Vector(-1, -1, -1))
                .setKl(0.0001).setKq(0.00001));

        // --- 8. מצלמה ---
        _cameraBuilder
                .setLocation(new Point(120, 60, 200)) // זווית מהצד ומלמעלה
                .setDirection(new Point(0, 0, -250), Vector.AXIS_Y)
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("GeometricBoatPhong");
    }
}