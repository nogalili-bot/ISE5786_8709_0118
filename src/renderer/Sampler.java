package renderer;

import java.util.ArrayList;
import java.util.List;
import primitives.Offset2D;

public class Sampler {
    private final int rootSamples; // e.g., 9 for a 9x9 grid
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

        // Correct division according to the number of samples
        double step = 1.0 / rootSamples;

        for (int i = 0; i < rootSamples; i++) {
            for (int j = 0; j < rootSamples; j++) {
                // Taking the center of the cell in range [0, 1] and shifting to [-0.5, 0.5]
                double x = ((i + 0.5) * step) - 0.5;
                double y = ((j + 0.5) * step) - 0.5;

                // Filtering for a circular target area (Aperture) - radius 0.5 squared is 0.25
                if ((x * x + y * y) <= 0.25) {
                    offsets.add(new Offset2D(x, y));
                }
            }
        }

        // Safeguard: if for some reason the grid is too small and no point entered the circle, add the center
        if (offsets.isEmpty()) {
            offsets.add(new Offset2D(0, 0));
        }

    }
}