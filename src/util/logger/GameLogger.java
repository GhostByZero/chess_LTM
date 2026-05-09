package util.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    /*
     * =========================
     * Info Log
     * =========================
     */

    public static void info(
            String message
    ) {

        print(
                "INFO",
                message
        );
    }

    /*
     * =========================
     * Warning Log
     * =========================
     */

    public static void warning(
            String message
    ) {

        print(
                "WARNING",
                message
        );
    }

    /*
     * =========================
     * Error Log
     * =========================
     */

    public static void error(
            String message
    ) {

        print(
                "ERROR",
                message
        );
    }

    /*
     * =========================
     * Internal Print
     * =========================
     */

    private static void print(
            String level,
            String message
    ) {

        String timestamp =
                LocalDateTime.now()
                        .format(FORMATTER);

        System.out.println(
                "[" + timestamp + "] "
                        + "[" + level + "] "
                        + message
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private GameLogger() {

    }
}