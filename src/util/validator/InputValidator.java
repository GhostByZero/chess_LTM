package util.validator;

public class InputValidator {

    /*
     * =========================
     * Null / Empty Check
     * =========================
     */

    public static boolean isNullOrEmpty(
            String text
    ) {

        return text == null
                || text.trim().isEmpty();
    }

    /*
     * =========================
     * Max Length Check
     * =========================
     */

    public static boolean exceedsLength(
            String text,
            int maxLength
    ) {

        if (text == null) {
            return false;
        }

        return text.length() > maxLength;
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private InputValidator() {

    }
}