package backend.board;

import backend.pieces.*;

/**
 * Đặt 32 quân cờ vào vị trí ban đầu trên bàn cờ.
 * <p>
 * Quy ước tọa độ:
 *   row 0 = hàng 8 (BLACK back rank)
 *   row 7 = hàng 1 (WHITE back rank)
 * <p>
 * Gọi BoardSetup.setup(board) một lần duy nhất
 * trong GameManager.initializeMatch().
 */
public class BoardSetup {

    private BoardSetup() {}

    /*
     * =========================
     * Public Entry Point
     * =========================
     */

    public static void setup(Board board) {
        if (board == null) {
            return;
        }

        board.clearBoard();

        placeBlackPieces(board);
        placeBlackPawns(board);
        placeWhitePawns(board);
        placeWhitePieces(board);
    }

    /*
     * =========================
     * Black Back Rank  (row 0)
     * =========================
     */

    private static void placeBlackPieces(Board board) {
        int row = 0;

        place(board, row, 0, new Rook  (PieceColor.BLACK, pos(row, 0)));
        place(board, row, 1, new Knight(PieceColor.BLACK, pos(row, 1)));
        place(board, row, 2, new Bishop(PieceColor.BLACK, pos(row, 2)));
        place(board, row, 3, new Queen (PieceColor.BLACK, pos(row, 3)));
        place(board, row, 4, new King  (PieceColor.BLACK, pos(row, 4)));
        place(board, row, 5, new Bishop(PieceColor.BLACK, pos(row, 5)));
        place(board, row, 6, new Knight(PieceColor.BLACK, pos(row, 6)));
        place(board, row, 7, new Rook  (PieceColor.BLACK, pos(row, 7)));
    }

    /*
     * =========================
     * Black Pawns  (row 1)
     * =========================
     */

    private static void placeBlackPawns(Board board) {
        int row = 1;

        for (int col = 0; col < Board.BOARD_SIZE; col++) {
            place(board, row, col, new Pawn(PieceColor.BLACK, pos(row, col)));
        }
    }

    /*
     * =========================
     * White Pawns  (row 6)
     * =========================
     */

    private static void placeWhitePawns(Board board) {
        int row = 6;

        for (int col = 0; col < Board.BOARD_SIZE; col++) {
            place(board, row, col, new Pawn(PieceColor.WHITE, pos(row, col)));
        }
    }

    /*
     * =========================
     * White Back Rank  (row 7)
     * =========================
     */

    private static void placeWhitePieces(Board board) {
        int row = 7;

        place(board, row, 0, new Rook  (PieceColor.WHITE, pos(row, 0)));
        place(board, row, 1, new Knight(PieceColor.WHITE, pos(row, 1)));
        place(board, row, 2, new Bishop(PieceColor.WHITE, pos(row, 2)));
        place(board, row, 3, new Queen (PieceColor.WHITE, pos(row, 3)));
        place(board, row, 4, new King  (PieceColor.WHITE, pos(row, 4)));
        place(board, row, 5, new Bishop(PieceColor.WHITE, pos(row, 5)));
        place(board, row, 6, new Knight(PieceColor.WHITE, pos(row, 6)));
        place(board, row, 7, new Rook  (PieceColor.WHITE, pos(row, 7)));
    }

    /*
     * =========================
     * Helpers
     * =========================
     */

    private static void place(Board board, int row, int col, Piece piece) {
        Tile tile = board.getTileByCoordinate(row, col);

        if (tile != null) {
            tile.setPiece(piece);
        }
    }

    private static Position pos(int row, int col) {
        return new Position(row, col);
    }
}
