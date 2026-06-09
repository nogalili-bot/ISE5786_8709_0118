package primitives;

/**
 * Value object representing a 2D offset (usually normalized between -0.5 and 0.5)
 * Used as a general building block for super-sampling patterns.
 */
public record Offset2D(double x, double y) {}