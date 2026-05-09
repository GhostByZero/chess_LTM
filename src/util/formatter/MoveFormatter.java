package util.formatter;

import backend.move.Move;
import backend.move.MoveType;
import backend.pieces.Piece;

import util.converter.PositionConverter;

public class MoveFormatter {

    /*
     * =========================
     * Basic Move Format
     * =========================
     */

    public static String formatMove(
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
     * Detailed Move Format
     * =========================
     */

    public static String formatDetailedMove(
            Move move
    ) {

        if (move == null) {
            return "";
        }

        Piece piece =
                move.getMovedPiece();

        String pieceName =
                piece.getClass()
                        .getSimpleName();

        String from =
                PositionConverter.toChessNotation(
                        move.getFrom()
                );

        String to =
                PositionConverter.toChessNotation(
                        move.getTo()
                );

        return pieceName
                + ": "
                + from
                + " -> "
                + to;
    }

    /*
     * =========================
     * Move Type Format
     * =========================
     */

    public static String formatMoveType(
            Move move
    ) {

        if (move == null) {
            return "";
        }

        MoveType moveType =
                move.getMoveType();

        return switch (moveType) {

            case NORMAL ->
                    "Normal Move";

            case CAPTURE ->
                    "Capture";

            case CASTLING ->
                    "Castling";

            case PROMOTION ->
                    "Promotion";

            case EN_PASSANT ->
                    "En Passant";

            default ->
                    "Unknown";
        };
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private MoveFormatter() {

    }
}