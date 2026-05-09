package backend.rules;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.move.Move;

import backend.pieces.King;
import backend.pieces.Piece;
import backend.pieces.PieceColor;
import backend.pieces.Rook;

public class CastlingValidator {
    private final Board board;
    private final CheckDetector checkDetector;
    private final MoveValidator moveValidator;

    public CastlingValidator(Board board) {
        this.board         = board;
        this.checkDetector = new CheckDetector(board);
        this.moveValidator = new MoveValidator(board);
    }
    /*
     * =========================
     * Main Validation
     * =========================
     */

    public boolean canCastleKingSide(PieceColor color) {
        int row = color == PieceColor.WHITE ? 7 : 0;
        Tile kingTile = board.getTile(new Position(row, 4));
        Tile rookTile = board.getTile(new Position(row, 7));

        if (!isValidCastlingPieces(kingTile, rookTile, color)) { return false; }

        /*
         * Path clear: col 5, 6 phải trống.
         */
        if (board.getTile(new Position(row, 5)).isOccupied()) { return false; }
        if (board.getTile(new Position(row, 6)).isOccupied()) { return false; }

        /*
         * Vua không được đang bị chiếu,
         * không được đi qua ô bị tấn công (col 5),
         * không được đến ô bị tấn công (col 6).
         */
        if (checkDetector.isKingInCheck(color))                      { return false; }
        if (isSquareAttackedBy(color, new Position(row, 5))) { return false; }
        return !isSquareAttackedBy(color, new Position(row, 6));
    }

    public boolean canCastleQueenSide(PieceColor color) {
        int row = color == PieceColor.WHITE ? 7 : 0;
        Tile kingTile = board.getTile(new Position(row, 4));
        Tile rookTile = board.getTile(new Position(row, 0));

        if (!isValidCastlingPieces(kingTile, rookTile, color)) { return false; }

        /*
         * Path clear: col 1, 2, 3 phải trống.
         */
        if (board.getTile(new Position(row, 1)).isOccupied()) { return false; }
        if (board.getTile(new Position(row, 2)).isOccupied()) { return false; }
        if (board.getTile(new Position(row, 3)).isOccupied()) { return false; }

        /*
         * Vua không được đang bị chiếu,
         * không được đi qua ô bị tấn công (col 3),
         * không được đến ô bị tấn công (col 2).
         * (col 1 là ô Rook đến — vua không đi qua, không cần kiểm tra)
         */
        if (checkDetector.isKingInCheck(color))                      { return false; }
        if (isSquareAttackedBy(color, new Position(row, 3))) { return false; }
        return !isSquareAttackedBy(color, new Position(row, 2));
    }

    /*
     * =========================
     * Helpers
     * =========================
     */

    private boolean isValidCastlingPieces(Tile kingTile, Tile rookTile, PieceColor color) {
        if (kingTile == null || rookTile == null) {
            return false;
        }

        if (!kingTile.isOccupied() || !rookTile.isOccupied()) {
            return false;
        }

        Piece king = kingTile.getPiece();
        Piece rook = rookTile.getPiece();

        if (!(king instanceof King) || !(rook instanceof Rook)) { return false; }
        if (king.getColor() != color || rook.getColor() != color) { return false; }

        return !king.hasMoved() && !rook.hasMoved();
    }

    /**
     * Kiểm tra ô {@code target} có đang bị bất kỳ quân đối phương nào của {@code color} tấn công không.
     * Dùng để xác minh vua không đi qua / đến ô nguy hiểm khi nhập thành.
     * <p>
     * Cơ chế: đặt tạm một Move từ mỗi quân địch đến target rồi hỏi MoveValidator.isValidMove().
     * isValidMove() kiểm tra hình học thuần túy (không simulate) nên không có circular dependency.
     */
    private boolean isSquareAttackedBy(PieceColor color, Position target) {
        PieceColor enemy = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Tile tile = board.getTileByCoordinate(row, col);
                if (tile == null || !tile.isOccupied()) { continue; }

                Piece piece = tile.getPiece();
                if (piece.getColor() != enemy) { continue; }

                Move attackMove = new Move(piece.getPosition(), target, piece, null);
                if (moveValidator.isValidMove(attackMove)) { return true; }
            }
        }
        return false;
    }
}
