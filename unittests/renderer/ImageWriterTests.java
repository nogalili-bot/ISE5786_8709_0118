package renderer;

import org.junit.jupiter.api.Test;
import renderer.ImageWriter;
import primitives.Color;

/**
 * Unit test for ImageWriter class.
 */
public class ImageWriterTests {

    /**
     * Test method for building a basic grid image.
     */
    @Test
    void testImageWriter() {
        final int width = 800;
        final int height = 500;
        final int step = 50;

        Color backgroundColor = new Color(255, 255, 0); // Yellow
        Color gridColor = new Color(0, 0, 0);           // Black

        // תיקון 1: הבנאי שלך מקבל רק רזולוציה (בלי שם קובץ)
        ImageWriter imageWriter = new ImageWriter(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i % step == 0 || j % step == 0) {
                    imageWriter.writePixel(i, j, gridColor);
                } else {
                    imageWriter.writePixel(i, j, backgroundColor);
                }
            }
        }

        // תיקון 2: אצלך שם הקובץ ניתן במתודה writeToImage
        imageWriter.writeToImage("yellow_grid");
    }
}
