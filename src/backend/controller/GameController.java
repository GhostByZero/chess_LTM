package backend.controller;

import backend.engine.GameManager;
import backend.move.Move;
import backend.move.MoveResult;
import backend.state.GameState;
import backend.pieces.PieceColor;

public class GameController {
    private final GameManager gameManager;

    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /*
     * =========================
     * Handle Move
     * =========================
     */

    public MoveResult handleMove(Move move) {
        if (move == null) {
            return MoveResult.INVALID;
        }
        return gameManager.processMove(move);
    }

    //undo
    // ĐOẠN MỚI NÊN DÙNG
    public boolean undoLastMove() {
        // Gọi hàm kiểm tra đã thêm ở GameManager
        if (gameManager.hasMoveHistory()) {
            // Thực hiện hoàn tác và trả về true nếu thành công
            return gameManager.undoLastMove() != null;
        }
        return false;
    }

    /*
     * =========================
     * Surrender
     * =========================
     */

    public void handleSurrender(PieceColor player) {
        if (player == null) {
            return;
        }
        gameManager.surrender(player);
    }

    /*
     * =========================
     * Pause Game
     * =========================
     */

    public void pauseMatch() {
        gameManager.pauseMatch();
    }

    /*
     * =========================
     * Resume Game
     * =========================
     */

    /**
     * [FIX #4] Resume tu PauseMenu (local pause) -- khong block vao connectivity.
     * Goi thay cho resumeMatch() khi nguoi choi bam Resume tu PauseMenu.
     */
    public void resumeFromPause() {
        gameManager.resumeFromPause();
    }

    /**
     * Resume sau network reconnect -- giu kiem tra areBothPlayersConnected().
     */
    public void resumeMatch() {
        gameManager.resumeMatch();
    }

    /*
     * =========================
     * Disconnect
     * =========================
     */

    public void handleDisconnect() {
        gameManager.handleDisconnect();
    }

    /*
     * =========================
     * Reconnect
     * =========================
     */

    public void handleReconnect() {
        gameManager.resumeMatch();
    }

    /*
     * =========================
     * Start Match
     * =========================
     */

    public void startMatch() {
        gameManager.startMatch();
    }

    /*
     * =========================
     * End Match
     * =========================
     */

    public void endMatch() {
        gameManager.endMatch();
    }

    /*
     * =========================
     * Game State
     * =========================
     */

    public GameState getGameState() {
        return gameManager.getGameState();
    }

    /*
     * =========================
     * Check Running State
     * =========================
     */

    public boolean isGameRunning() {
        return gameManager.isGameRunning();
    }

    /*
     * =========================
     * Check Paused State
     * =========================
     */

    public boolean isGamePaused() {
        return gameManager.isGamePaused();
    }

    /*
     * =========================
     * Access GameManager
     * =========================
     */

    public GameManager getGameManager() {
        return gameManager;
    }
}
