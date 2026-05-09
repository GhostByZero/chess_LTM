package util.validator;

public class UsernameValidator {

    private static final int MIN_LENGTH = 3;

    private static final int MAX_LENGTH = 16;

    /*
     * =========================
     * Username Validation
     * =========================
     */

    public static boolean isValidUsername(
            String username
    ) {

        if (username == null) {
            return false;
        }

        username =
                username.trim();

        if (username.length() < MIN_LENGTH
                || username.length() > MAX_LENGTH) {

            return false;
        }

        return username.matches(
                "^[a-zA-Z0-9_]+$"
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private UsernameValidator() {

    }
}