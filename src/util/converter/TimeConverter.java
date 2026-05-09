package util.converter;

public class TimeConverter {

    /*
     * =========================
     * Seconds -> MM:SS
     * =========================
     */

    public static String secondsToClock(
            int totalSeconds
    ) {

        int minutes =
                totalSeconds / 60;

        int seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private TimeConverter() {

    }
}