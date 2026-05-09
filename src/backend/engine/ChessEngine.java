package backend.engine;

import backend.board.Board;
import backend.move.Move;
import backend.move.MoveGenerator;
import backend.move.MoveManager;
import backend.pieces.PieceColor;
import backend.rules.CheckDetector;
import backend.rules.CheckmateDetector;
import backend.rules.DrawDetector;
import backend.rules.MoveValidator;
import backend.rules.StalemateDetector;
import backend.state.GameState;

public class ChessEngine {
    private final Board board;
    private final GameState gameState;
    private final MoveValidator moveValidator;
    private final CheckDetector checkDetector;
    private final CheckmateDetector checkmateDetector;
    private final StalemateDetector stalemateDetector;
    private final MoveGenerator moveGenerator;
    private final DrawDetector drawDetector;

    /**
     * @param moveManager cần thiết để MoveValidator và MoveGenerator
     *                    có thể gọi isMoveLegal() (simulate + undo).
     */
    public ChessEngine(Board board, GameState gameState, MoveManager moveManager) {
        this.board             = board;
        this.gameState         = gameState;
        this.moveValidator     = new MoveValidator(board, moveManager);
        this.checkDetector     = new CheckDetector(board);
        this.checkmateDetector = new CheckmateDetector(board, moveManager);
        this.stalemateDetector = new StalemateDetector(board, moveManager);
        this.moveGenerator     = new MoveGenerator(board, moveManager);
        this.drawDetector      = new DrawDetector(board, moveManager.getHistory());
    }

    /*
     * =========================
     * Update Game State
     * Gọi sau mỗi nước đi thành công để detect
     * check / checkmate / stalemate / draw cho bên vừa nhận lượt
     * =========================
     */

    public void updateGameState(PieceColor nextTurnColor) {
        boolean inCheck     = checkDetector.isKingInCheck(nextTurnColor);
        boolean inCheckmate = inCheck && checkmateDetector.isCheckmate(nextTurnColor);
        boolean inStalemate = !inCheck && stalemateDetector.isStalemate(nextTurnColor);

        gameState.setCheck(inCheck);
        gameState.setCheckmate(inCheckmate);
        gameState.setStalemate(inStalemate);

        if (inCheckmate) {
            PieceColor winner = (nextTurnColor == PieceColor.WHITE)
                    ? PieceColor.BLACK
                    : PieceColor.WHITE;
            gameState.endGame(winner);
        }

        if (inStalemate) {
            gameState.endGame(null);
        }
    }

    /*
     * =========================
     * Validation helper
     * =========================
     */

    public boolean validateMove(Move move) {
        return moveValidator.isMoveLegal(move);
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public Board getBoard()                 { return board;         }
    public MoveValidator getMoveValidator() { return moveValidator; }
    public CheckDetector getCheckDetector() { return checkDetector; }
    public DrawDetector getDrawDetector()   { return drawDetector;  }
    public MoveGenerator getMoveGenerator() { return moveGenerator; }
}
