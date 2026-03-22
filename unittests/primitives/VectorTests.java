package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link primitives.Vector}.
 * The tests verify vector operations, normalization, and zero vector prevention.
 */
class VectorTests {

    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for wrong vector length */
    private static final String ERROR_LENGTH = "ERROR: length() result is incorrect";
    /** Error message for wrong dot product result */
    private static final String ERROR_DOT_PRODUCT = "ERROR: dotProduct() result is incorrect";
    /** Error message for wrong cross product result */
    private static final String ERROR_CROSS_PRODUCT = "ERROR: crossProduct() result is incorrect";
    /** Error message for wrong vector normalization */
    private static final String ERROR_NORMALIZE = "ERROR: normalize() result is incorrect";

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Correct vector construction
        assertDoesNotThrow(() -> new Vector(1, 1, 1), "Failed to construct a valid vector");

        // =============== Boundary Values Tests ==================
        // BV01: Zero vector construction should throw exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "Constructed a zero vector");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-1, -2, -3);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular vector addition
        assertEquals(new Vector(2, 4, 6), v1.add(new Vector(1, 2, 3)), "Vector addition failed");

        // =============== Boundary Values Tests ==================
        // BV01: Addition resulting in zero vector should throw exception
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2),
                "Vector addition resulting in zero vector should throw exception");
    }

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     * Verified in Vector to ensure return type is Vector and zero-vector protection.
     */
    @Test
    void testSubtract() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 4, 6);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular vector subtraction
        assertEquals(new Vector(1, 2, 3), v2.subtract(v1), "Vector subtraction failed");

        // =============== Boundary Values Tests ==================
        // BV01: Subtraction resulting in zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1),
                "Vector subtraction resulting in zero vector should throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-2, -4, -6);
        Vector v3 = new Vector(0, 3, -2);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular dot product
        assertEquals(-28d, v1.dotProduct(v2), DELTA, ERROR_DOT_PRODUCT);

        // =============== Boundary Values Tests ==================
        // BV01: Test dot product of orthogonal vectors (should be 0)
        assertEquals(0d, v1.dotProduct(v3), DELTA, "Dot product of orthogonal vectors is not 0");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        Vector v3 = v1.crossProduct(v2);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular cross product length
        assertEquals(v1.length() * v2.length(), v3.length(), DELTA, ERROR_CROSS_PRODUCT);

        // EP02: Test cross product orthogonality to operands
        assertEquals(0d, v3.dotProduct(v1), DELTA, "Cross product is not orthogonal to first vector");
        assertEquals(0d, v3.dotProduct(v2), DELTA, "Cross product is not orthogonal to second vector");

        // =============== Boundary Values Tests ==================
        // BV01: Cross product of parallel vectors (should throw exception for zero vector)
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v1.scale(2)),
                "Cross product of parallel vectors should throw exception");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test regular vector length
        assertEquals(5d, new Vector(0, 3, 4).length(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        Vector v = new Vector(1, 2, 3);
        Vector u = v.normalize();

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that normalized vector is a unit vector
        assertEquals(1d, u.length(), DELTA, ERROR_NORMALIZE);

        // EP02: Test that normalization maintains direction
        assertThrows(IllegalArgumentException.class, () -> v.crossProduct(u),
                "Normalized vector is not parallel to the original vector");
        assertTrue(v.dotProduct(u) > 0, "Normalized vector has opposite direction");
    }
}