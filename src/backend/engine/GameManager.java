package backend.engine;

import backend.board.Board;
import backend.board.BoardSetup;
import backend.board.Tile;
import backend.move.Move;
import backend.move.MoveHistory;
import backend.move.MoveManager;
import backend.move.MoveRecord;
import backend.move.MoveResult;
import backend.pieces.Piece;
import backend.pieces.PieceColor;
import backend.state.GameState;
import backend.state.MatchState;
import backend.rules.DrawDetector;
import backend.rules.MoveValidator;
import backend.state.TurnState;

public class GameManager {
    private final Board board;
    private final GameState gameState;
    private final TurnManager turnManager;
    private final MoveManager moveManager;
    private final ChessEngine chessEngine;

    /*
     * Không giữ MoveValidator riêng — dùng chessEngine.getMoveValidator()
     * để đảm bảo chỉ có đúng một instance được tạo từ cùng Board + MoveManager.
     */

    public GameManager() {
        this.board       = new Board();
        this.gameState   = new GameState();
        this.turnManager = new TurnManager(gameState);
        this.moveManager = new MoveManager(board);
        this.chessEngine = new ChessEngine(board, gameState, moveManager);
        initializeMatch();
    }

    /*
     * =========================
     * Match Initialization
     * =========================
     */

    private void initializeMatch() {
        BoardSetup.setup(board);
        gameState.setMatchState(MatchState.PLAYING);
        chessEngine.getDrawDetector().clear();
    }

    /*
     * =========================
     * Move Handling
     * =========================
     */

    public MoveResult processMove(Move move) {
        if (move == null) {
            return MoveResult.INVALID;
        }

        if (gameState.isGameOver()) {
            return MoveResult.INVALID;
        }

        Tile fromTile = board.getTile(move.getFrom());

        if (fromTile == null || !fromTile.isOccupied()) {
            return MoveResult.INVALID;
        }

        Piece movingPiece = fromTile.getPiece();

        /*
         * Kiểm tra turn
         */
        if (!turnManager.isCorrectTurn(movingPiece.getColor())) {
            return MoveResult.INVALID;
        }

        /*
         * Validate + Execute nguyên tử — tránh gọi executeMove 2 lần.
         *
         * Trước đây: isMoveLegal() simulate (execute→undo) rồi executeMove() thật = 2 lần.
         * Giờ: isMoveLegal() chỉ validate trên copy (không động board thật),
         * sau đó executeMove() thật chạy đúng 1 lần duy nhất.
         */
        if (!chessEngine.getMoveValidator().isMoveLegal(move)) {
            return MoveResult.INVALID;
        }

        MoveResult result = moveManager.executeMove(move);

        if (result != MoveResult.SUCCESS) {
            return result;
        }

        /*
         * Switch turn
         */
        turnManager.switchTurn();

        /*
         * Analyze board sau khi đổi turn:
         * detect check / checkmate / stalemate cho bên vừa nhận lượt.
         */
        PieceColor nextColor = (gameState.getCurrentTurn() == TurnState.WHITE_TURN)
                ? PieceColor.WHITE
                : PieceColor.BLACK;
        chessEngine.updateGameState(nextColor);

        /*
         * Ghi nhận vị trí hiện tại cho threefold repetition.
         * Phải gọi SAU switchTurn để nextColor phản ánh đúng bên sắp đi.
         */
        chessEngine.getDrawDetector().recordPosition(nextColor);

        /*
         * Trả về MoveResult phản ánh đúng trạng thái board sau nước đi.
         * Thứ tự ưu tiên: CHECKMATE > STALEMATE > DRAW > CHECK > SUCCESS.
         */
        if (gameState.isCheckmate()) { return MoveResult.CHECKMATE; }
        if (gameState.isStalemate()) { return MoveResult.STALEMATE; }

        DrawDetector draw = chessEngine.getDrawDetector();
        if (draw.isFiftyMoveRule() || draw.isThreefoldRepetition()) {
            gameState.endGame(null);
            return MoveResult.DRAW;
        }

        if (gameState.isCheck())     { return MoveResult.CHECK;     }

        return MoveResult.SUCCESS;
    }

    /*
     * =========================
     * Surrender
     * =========================
     */

    public void surrender(PieceColor loser) {
        gameState.surrender(loser);
    }

    /*
     * =========================
     * Match Pause / Resume
     * =========================
     */

    public void startMatch()  { gameState.setMatchState(MatchState.PLAYING);  }
    public void endMatch()    { gameState.setMatchState(MatchState.ENDED);    }
    public void pauseMatch()  { gameState.setMatchState(MatchState.PAUSED);   }

    /**
     * Resume sau khi nguoi choi bam Resume tu PauseMenu (local pause).
     *
     * [FIX #4] Tach khoi resumeMatch() (von danh cho network reconnect).
     * resumeMatch() block vao areBothPlayersConnected() -- dung cho mang
     * vi can dam bao ca hai client da reconnect truoc khi tiep tuc.
     * resumeFromPause() set PLAYING truc tiep, khong kiem tra connectivity,
     * vi local pause khong lien quan den trang thai ket noi mang.
     */
    public void resumeFromPause() {
        if (gameState.getMatchState() == MatchState.PAUSED) {
            gameState.setMatchState(MatchState.PLAYING);
        }
    }

    /**
     * Resume sau khi doi thu reconnect mang.
     * Giu nguyen kiem tra areBothPlayersConnected() de dam bao
     * ca hai player da ket noi lai truoc khi tiep tuc van.
     */
    public void resumeMatch() { gameState.resumeMatch(); }

    /*
     * =========================
     * Disconnect Handling
     * =========================
     */

    public void handleDisconnect() { gameState.pauseMatchDueToDisconnect();                  }
    public boolean isGameRunning() { return gameState.getMatchState() == MatchState.PLAYING; }
    public boolean isGamePaused()  { return gameState.getMatchState() == MatchState.PAUSED;  }

    /*
     * =========================
     * Getters
     * =========================
     */

    public Board getBoard()                { return board;                       }
    public GameState getGameState()        { return gameState;                   }
    public TurnManager getTurnManager()    { return turnManager;                 }
    public MoveManager getMoveManager()    { return moveManager;                 }
    public ChessEngine getChessEngine()    { return chessEngine;                 }
    public MoveHistory getMoveHistory()    { return moveManager.getHistory();    }
    public MoveRecord getLastMove()        { return moveManager.getLastMove();   }

    /**
     * Convenience getter — delegate về ChessEngine để không expose 2 instance.
     * Tất cả code gọi getMoveValidator() qua đây đều nhận cùng một object.
     */
    public MoveValidator getMoveValidator() {
        return chessEngine.getMoveValidator();
    }

    /*
     * =========================
     * Undo
     * =========================
     */

    public boolean hasMoveHistory() {
        return moveManager.getHistory() != null && !moveManager.getHistory().isEmpty();
    }

    // Hàm thực hiện Undo (Bạn giữ nguyên logic xử lý lượt đi và checkmate như bạn đã viết)
    public MoveRecord undoLastMove() {
        if (!hasMoveHistory()) {
            return null;
        }

        MoveRecord record = moveManager.undoMove();
        if (record == null) return null;

        turnManager.switchTurn();

        if (gameState.isGameOver()) {
            gameState.setGameOver(false);
            gameState.setWinner(null);
            gameState.setMatchState(MatchState.PLAYING);
        }

        chessEngine.getDrawDetector().removeLastPosition(
                (gameState.getCurrentTurn() == TurnState.WHITE_TURN)
                        ? PieceColor.WHITE : PieceColor.BLACK
        );

        PieceColor restoredColor = (gameState.getCurrentTurn() == TurnState.WHITE_TURN)
                ? PieceColor.WHITE
                : PieceColor.BLACK;
        chessEngine.updateGameState(restoredColor);

        return record;
    }

}
