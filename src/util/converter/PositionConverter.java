package util.converter;

import backend.board.Position;

public class PositionConverter {

    /*
     * =========================
     * Position -> Chess Notation
     * =========================
     */

    public static String toChessNotation(
            Position position
    ) {

        if (position == null) {
            return "";
        }

        char file =
                (char) ('a' + position.getCol());

        int rank =
                8 - position.getRow();

        return file + String.valueOf(rank);
    }

    /*
     * =========================
     * Chess Notation -> Position
     * =========================
     */

    public static Position fromChessNotation(
            String notation
    ) {

        if (notation == null || notation.length() != 2) {
            return null;
        }

        char file =
                notation.charAt(0);

        char rank =
                notation.charAt(1);

        int col =
                file - 'a';

        int row =
                8 - Character.getNumericValue(rank);

        return new Position(
                row,
                col
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private PositionConverter() {

    }
}