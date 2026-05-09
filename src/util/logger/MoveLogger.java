package util.logger;

import backend.move.Move;

import util.formatter.MoveFormatter;

public class MoveLogger {

    /*
     * =========================
     * Log Move
     * =========================
     */

    public static void logMove(
            Move move
    ) {

        if (move == null) {
            return;
        }

        String formattedMove =
                MoveFormatter.formatDetailedMove(
                        move
                );

        System.out.println(
                "[MOVE] "
                        + formattedMove
        );
    }

    /*
     * =========================
     * Invalid Move
     * =========================
     */

    public static void invalidMove(
            Move move
    ) {

        if (move == null) {
            return;
        }

        String formattedMove =
                MoveFormatter.formatMove(
                        move
                );

        System.out.println(
                "[INVALID MOVE] "
                        + formattedMove
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private MoveLogger() {

    }
}