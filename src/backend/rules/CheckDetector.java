package backend.rules;

import backend.board.Board;
import backend.board.Tile;
import backend.move.Move;
import backend.pieces.King;
import backend.pieces.Piece;
import backend.pieces.PieceColor;

public class CheckDetector {
    private final Board board;
    private final MoveValidator moveValidator;

    public CheckDetector(Board board) {
        this.board = board;
        this.moveValidator = new MoveValidator(board);
    }

    /*
     * =========================
     * Check Detection
     * =========================
     */

    public boolean isKingInCheck(PieceColor kingColor) {
        Tile kingTile = findKing(kingColor);

        if (kingTile == null) {
            return false;
        }

        Piece king = kingTile.getPiece();
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Tile tile = board.getTileByCoordinate(row, col);
                if (tile == null || !tile.isOccupied()) {continue;}

                Piece enemy = tile.getPiece();

                if (enemy.getColor() == kingColor) {continue;}

                Move move = new Move(enemy.getPosition(), king.getPosition(), enemy, null);

                if (moveValidator.isValidMove(move)) {return true;}
            }
        }
        return false;
    }

    /*
     * =========================
     * Find King
     * =========================
     */

    private Tile findKing(PieceColor color) {
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {

                Tile tile = board.getTileByCoordinate(row, col);

                if (tile == null || !tile.isOccupied()) {continue;}

                Piece piece = tile.getPiece();

                if (piece instanceof King && piece.getColor() == color) {return tile;}
            }
        }
        return null;
    }
}
