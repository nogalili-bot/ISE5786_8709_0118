package renderer;

import java.util.ArrayList;
import java.util.List;
import primitives.Offset2D;

public class Sampler {
    private final int rootSamples; // למשל 9 עבור רשת של 9x9
    private final List<Offset2D> offsets = new ArrayList<>();

    public Sampler(int rootSamples) {
        this.rootSamples = rootSamples;
        generateGridPattern();
    }

    public List<Offset2D> getOffsets() {
        return offsets;
    }

    /**
     * Generates a basic regular grid pattern normalized between [-0.5, 0.5]
     */
    private void generateGridPattern() {
        offsets.clear();
        if (rootSamples <= 1) {
            offsets.add(new Offset2D(0, 0));
            return;
        }

        // חלוקה נכונה לפי כמות הדגימות
        double step = 1.0 / rootSamples;

        for (int i = 0; i < rootSamples; i++) {
            for (int j = 0; j < rootSamples; j++) {
                // לקיחת מרכז המשבצת בטווח [0, 1] והזזה לטווח [-0.5, 0.5]
                double x = ((i + 0.5) * step) - 0.5;
                double y = ((j + 0.5) * step) - 0.5;

                // סינון עבור אזור מטרה עגול (Aperture) - רדיוס 0.5 בריבוע הוא 0.25
                if ((x * x + y * y) <= 0.25) {
                    offsets.add(new Offset2D(x, y));
                }
            }
        }

        // הגנה: אם מסיבה כלשהי הרשת קטנה מדי ואף נקודה לא נכנסה לעיגול, נוסיף את המרכז
        if (offsets.isEmpty()) {
            offsets.add(new Offset2D(0, 0));
        }

    }
}