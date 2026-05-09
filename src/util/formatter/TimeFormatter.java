package util.formatter;

public class TimeFormatter {

    /*
     * =========================
     * Seconds -> MM:SS
     * =========================
     */

    public static String formatTime(
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
     * Milliseconds -> MM:SS
     * =========================
     */

    public static String formatMilliseconds(
            long milliseconds
    ) {

        long totalSeconds =
                milliseconds / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    /*
     * =========================
     * Seconds -> HH:MM:SS
     * =========================
     */

    public static String formatLongTime(
            int totalSeconds
    ) {

        int hours =
                totalSeconds / 3600;

        int remaining =
                totalSeconds % 3600;

        int minutes =
                remaining / 60;

        int seconds =
                remaining % 60;

        return String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private TimeFormatter() {

    }
}