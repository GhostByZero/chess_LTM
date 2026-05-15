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

        // Lấy tên tiếng Việt thay vì lấy tên Class của Java
        String pieceName = getVietnamesePieceName(piece.getSymbol());

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
     * Hàm phụ trợ để chuyển đổi ký hiệu quân cờ sang tên tiếng Việt
     */
    private static String getVietnamesePieceName(String symbol) {
        if (symbol == null) return "Quân cờ";

        return switch (symbol.toUpperCase()) {
            case "P" -> "Tốt";
            case "R" -> "Xe";
            case "N" -> "Mã";
            case "B" -> "Tượng";
            case "Q" -> "Hậu";
            case "K" -> "Vua";
            default -> symbol;
        };
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
                    "Nước đi thường";

            case CAPTURE ->
                    "Bắt quân";

            case CASTLING ->
                    "Nhập thành";

            case PROMOTION ->
                    "Phong cấp";

            case EN_PASSANT ->
                    "Bắt tốt qua đường";

            default ->
                    "Không xác định";
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