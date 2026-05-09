package util.helper;

import backend.board.Position;

public class BoardHelper {

    /*
     * =========================
     * Board Boundary
     * =========================
     */

    public static boolean isInsideBoard(
            int row,
            int col
    ) {

        return row >= 0
                && row < 8
                && col >= 0
                && col < 8;
    }

    /*
     * =========================
     * Position Boundary
     * =========================
     */

    public static boolean isInsideBoard(
            Position position
    ) {

        if (position == null) {
            return false;
        }

        return isInsideBoard(
                position.getRow(),
                position.getCol()
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private BoardHelper() {

    }
}