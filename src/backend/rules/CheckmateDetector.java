package backend.rules;

import backend.board.Board;
import backend.move.Move;
import backend.move.MoveGenerator;
import backend.move.MoveManager;
import backend.pieces.PieceColor;

import java.util.List;

public class CheckmateDetector {
    private final Board board;
    private final CheckDetector checkDetector;
    private final MoveGenerator moveGenerator;

    public CheckmateDetector(Board board, MoveManager moveManager) {
        this.board         = board;
        this.checkDetector = new CheckDetector(board);
        this.moveGenerator = new MoveGenerator(board, moveManager);
    }

    /** Backward-compatible constructor (không lọc tự chiếu). */
    public CheckmateDetector(Board board) {
        this.board         = board;
        this.checkDetector = new CheckDetector(board);
        this.moveGenerator = new MoveGenerator(board);
    }

    /*
     * =========================
     * Checkmate Detection
     * =========================
     */

    public boolean isCheckmate(PieceColor color) {
        if (!checkDetector.isKingInCheck(color)) { return false; }

        List<Move> legalMoves = moveGenerator.generateAllMoves(color);
        return legalMoves.isEmpty();
    }
}
