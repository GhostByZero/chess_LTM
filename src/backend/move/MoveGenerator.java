package backend.move;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.pieces.*;
import backend.rules.MoveValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Sinh danh sách nước đi hợp lệ (legal moves) cho một quân hoặc toàn bộ một bên.
 * <p>
 * Sử dụng {@link MoveValidator#isMoveLegal(Move)} thay vì isValidMove()
 * để đảm bảo các nước đi tự chiếu bị loại bỏ hoàn toàn.
 * Đây là điều kiện cần thiết để CheckmateDetector và StalemateDetector
 * hoạt động đúng.
 */
public class MoveGenerator {

    private final Board board;
    private final MoveValidator moveValidator;

    /**
     * Constructor đầy đủ — dùng trong GameManager và các detector.
     * MoveManager cần thiết để isMoveLegal() có thể simulate nước đi.
     */
    public MoveGenerator(Board board, MoveManager moveManager) {
        this.board         = board;
        this.moveValidator = new MoveValidator(board, moveManager);
    }

    /**
     * Constructor tối giản (backward-compatible) — isMoveLegal fallback
     * về isValidMove(). Chỉ dùng trong context không cần lọc tự chiếu.
     */
    public MoveGenerator(Board board) {
        this.board         = board;
        this.moveValidator = new MoveValidator(board);
    }

    /*
     * =========================
     * Generate All Legal Moves
     * =========================
     */

    /**
     * Sinh tất cả nước đi hợp lệ (sau khi lọc tự chiếu) cho một bên.
     * Dùng bởi CheckmateDetector và StalemateDetector để kiểm tra
     * còn nước đi nào không.
     */
    public List<Move> generateAllMoves(PieceColor color) {
        List<Move> legalMoves = new ArrayList<>();

        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Tile tile = board.getTileByCoordinate(row, col);

                if (!tile.isOccupied()) { continue; }

                Piece piece = tile.getPiece();

                if (piece.getColor() != color) { continue; }

                legalMoves.addAll(generateMoves(piece));
            }
        }

        return legalMoves;
    }

    /*
     * =========================
     * Generate Moves for a Piece
     * =========================
     */

    /**
     * Sinh tất cả nước đi hợp lệ của một quân cụ thể.
     * Duyệt toàn bộ 64 ô, lọc qua {@link MoveValidator#isMoveLegal(Move)}.
     */
    public List<Move> generateMoves(Piece piece) {
        List<Move> moves = new ArrayList<>();

        if (piece == null) { return moves; }

        Position from = piece.getPosition();

        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Position target = new Position(row, col);
                Move move       = new Move(from, target, piece, MoveType.NORMAL);

                if (moveValidator.isMoveLegal(move)) {
                    Tile targetTile = board.getTile(target);
                    if (targetTile != null && targetTile.isOccupied()) {
                        move.setMoveType(MoveType.CAPTURE);
                    }
                    moves.add(move);
                }
            }
        }

        return moves;
    }

    /*
     * =========================
     * Per-piece convenience methods
     * =========================
     */

    public List<Move> generatePawnMoves  (Pawn   pawn)   { return generateMoves(pawn);   }
    public List<Move> generateRookMoves  (Rook   rook)   { return generateMoves(rook);   }
    public List<Move> generateBishopMoves(Bishop bishop) { return generateMoves(bishop); }
    public List<Move> generateKnightMoves(Knight knight) { return generateMoves(knight); }
    public List<Move> generateQueenMoves (Queen  queen)  { return generateMoves(queen);  }
    public List<Move> generateKingMoves  (King   king)   { return generateMoves(king);   }
}
