package backend.rules;

import backend.board.Position;
import backend.pieces.*;

public class PromotionHandler {
    /*
     * =========================
     * Promotion Check
     * =========================
     */
    public boolean canPromote(Pawn pawn) {
        if (pawn == null) { return false; }

        Position position = pawn.getPosition();
        if (pawn.getColor() == PieceColor.WHITE) { return position.getRow() == 0; }

        return position.getRow() == 7;
    }
    /*
     * =========================
     * Promotion
     * =========================
     */
    /**
     * Phong cấp tốt thành quân mới.
     * <p>
     * Nếu {@code promoteTo} là null hoặc không hợp lệ, mặc định thành Queen —
     * tránh NPE khi UI chưa gửi lựa chọn (ví dụ lúc test backend độc lập).
     * <p>
     * Giá trị hợp lệ (case-insensitive): "QUEEN", "ROOK", "BISHOP", "KNIGHT".
     */
    public Piece promote(Pawn pawn, String promoteTo) {
        if (!canPromote(pawn)) {
            return pawn;
        }

        Position   position = pawn.getPosition();
        PieceColor color    = pawn.getColor();

        if (promoteTo == null) {
            return new Queen(color, position);
        }

        return switch (promoteTo.toUpperCase()) {
            case "ROOK"   -> new Rook(color,   position);
            case "BISHOP" -> new Bishop(color, position);
            case "KNIGHT" -> new Knight(color, position);
            default       -> new Queen(color,  position);
        };
    }

}
