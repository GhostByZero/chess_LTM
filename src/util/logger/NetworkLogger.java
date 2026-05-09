package util.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NetworkLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    /*
     * =========================
     * Connection Log
     * =========================
     */

    public static void connection(
            String message
    ) {

        print(
                "CONNECTION",
                message
        );
    }

    /*
     * =========================
     * Packet Log
     * =========================
     */

    public static void packet(
            String message
    ) {

        print(
                "PACKET",
                message
        );
    }

    /*
     * =========================
     * Disconnect Log
     * =========================
     */

    public static void disconnect(
            String message
    ) {

        print(
                "DISCONNECT",
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
            String type,
            String message
    ) {

        String timestamp =
                LocalDateTime.now()
                        .format(FORMATTER);

        System.out.println(
                "[" + timestamp + "] "
                        + "[NETWORK-" + type + "] "
                        + message
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private NetworkLogger() {

    }
}