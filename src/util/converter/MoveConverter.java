package util.converter;

import backend.move.Move;

public class MoveConverter {

    /*
     * =========================
     * Move -> Simple Notation
     * =========================
     */

    public static String moveToNotation(
            Move move
    ) {

        if (move == null) {
            return "";
        }

        String from =
                PositionConverter.toChessNotation(
                        move.getFrom()
                );

        String to =
                PositionConverter.toChessNotation(
                        move.getTo()
                );

        return from + " -> " + to;
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private MoveConverter() {

    }
}