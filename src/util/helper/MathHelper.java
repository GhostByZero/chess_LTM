package util.helper;

public class MathHelper {

    /*
     * =========================
     * Absolute Value
     * =========================
     */

    public static int abs(
            int value
    ) {

        return Math.abs(value);
    }

    /*
     * =========================
     * Clamp
     * =========================
     */

    public static int clamp(
            int value,
            int min,
            int max
    ) {

        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private MathHelper() {

    }
}