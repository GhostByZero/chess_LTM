package ui.window;

import backend.controller.GameController;
import backend.engine.GameManager;
import backend.pieces.PieceColor;
import backend.state.GameState;
import backend.state.TurnState;

import ui.input.InputHandler;
import ui.menu.PauseMenu;
import ui.panel.ChatPanel;
import ui.panel.MoveHistoryPanel;
import ui.panel.PlayerInfoPanel;
import ui.panel.StatusPanel;
import ui.panel.TimerPanel;

import util.formatter.MoveFormatter;
import util.formatter.TimeFormatter;
import util.timer.GameTimer;

import javax.swing.*;

/**
 * Chịu trách nhiệm gắn tất cả ActionListener cho GameWindow.
 *
 * GameWindow chỉ chứa UI (View). Controller này điều phối:
 *   - PauseMenu   : Resume / Settings / Surrender / Exit Match
 *   - ChatPanel   : gửi tin nhắn local (network sẽ bổ sung sau)
 *   - Keyboard    : ESC để toggle pause menu
 *   - InputHandler: nhận callback onMoveExecuted sau mỗi nước đi
 *   - GameTimer   : đếm ngược cho từng người chơi, swap sau mỗi lượt
 */
public class GameWindowController {

    private final GameWindow gameWindow;
    private final GameController gameController;
    private final GameManager gameManager;

    /*
     * =========================
     * Constructor
     * =========================
     */

    public GameWindowController(GameWindow gameWindow) {
        this.gameWindow     = gameWindow;
        this.gameController = gameWindow.getGameController();
        this.gameManager    = gameWindow.getGameManager();

        attachPauseMenuListeners();
        attachChatListeners();
        attachKeyboardShortcuts();
        attachMoveCallback();
        initializeTimer();             // ← wire GameTimer → TimerPanel
    }

    /*
     * =========================
     * Timer initialization
     * Gắn onTick callbacks vào cả hai CountdownTimer để
     * TimerPanel tự cập nhật mỗi giây mà không cần polling.
     * =========================
     */

    private void initializeTimer() {
        GameTimer  gameTimer  = gameWindow.getGameTimer();
        TimerPanel timerPanel = gameWindow.getTimerPanel();

        gameTimer.getWhiteTimer().setOnTick(seconds ->
                timerPanel.updateWhiteTimer(TimeFormatter.formatTime(seconds))
        );

        gameTimer.getBlackTimer().setOnTick(seconds ->
                timerPanel.updateBlackTimer(TimeFormatter.formatTime(seconds))
        );

        /*
         * Khi het gio: dung timer, hien thi game over.
         * Backend chua xu ly het gio nen xu ly ngay tai day.
         */
        gameTimer.getWhiteTimer().setOnTimeout(() -> onTimeout(PieceColor.WHITE));
        gameTimer.getBlackTimer().setOnTimeout(() -> onTimeout(PieceColor.BLACK));

        /*
         * White di truoc theo luat co vua standard.
         */
        gameTimer.startWhiteTimer();
    }

    private void onTimeout(PieceColor timedOutPlayer) {
        gameWindow.getGameTimer().stopAll();
        String loser  = timedOutPlayer.name();
        String winner = (timedOutPlayer == PieceColor.WHITE) ? "BLACK" : "WHITE";
        String result = loser + " ran out of time. " + winner + " wins!";
        gameWindow.getStatusPanel().setStatus(result);
        gameWindow.showGameOverDialog(result);
    }

    /*
     * =========================
     * Move callback
     * Gắn Runnable vào InputHandler để sau mỗi nước đi
     * thành công UI được cập nhật tự động.
     * =========================
     */

    private void attachMoveCallback() {
        InputHandler inputHandler = gameWindow.getInputHandler();
        inputHandler.setOnMoveExecuted(this::onMoveExecuted);
    }

    /*
     * =========================
     * onMoveExecuted
     * Cập nhật toàn bộ UI sau mỗi nước đi thành công:
     *   - Turn label
     *   - Status bar (lượt / chiếu / chiếu bí / hòa)
     *   - Move history
     *   - Timer swap
     *   - Game over dialog nếu ván kết thúc
     * =========================
     */

    public void onMoveExecuted() {
        GameState state = gameController.getGameState();

        PieceColor currentTurn = (state.getCurrentTurn() == TurnState.WHITE_TURN)
                ? PieceColor.WHITE
                : PieceColor.BLACK;

        backend.move.MoveRecord lastMove = gameManager.getLastMove();
        if (lastMove != null) {
            MoveHistoryPanel historyPanel = gameWindow.getMoveHistoryPanel();
            String moveText = MoveFormatter.formatDetailedMove(lastMove.getMove());
            historyPanel.addMove(moveText);
        }

        PlayerInfoPanel playerInfo = gameWindow.getPlayerInfoPanel();
        playerInfo.updateTurn(currentTurn);

        StatusPanel status = gameWindow.getStatusPanel();

        if (state.isCheckmate()) {
            String winner = state.getWinner() != null
                    ? state.getWinner().name() + " wins by checkmate!"
                    : "Checkmate!";
            status.setStatus(winner);
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog(winner);
            return;
        }

        if (state.isStalemate()) {
            status.setStatus("Stalemate! Draw.");
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog("Stalemate! Draw.");
            return;
        }

        if (state.isGameOver()) {
            status.setStatus("Draw!");
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog("Draw! (50-move rule or threefold repetition)");
            return;
        }

        /*
         * Swap timer sang nguoi choi tiep theo.
         * currentTurn da la luot MOI (sau khi switchTurn() trong backend),
         * nen start timer cua nguoi do.
         */
        if (currentTurn == PieceColor.WHITE) {
            gameWindow.getGameTimer().startWhiteTimer();
        } else {
            gameWindow.getGameTimer().startBlackTimer();
        }

        if (state.isCheck()) {
            status.setStatus(currentTurn.name() + " is in Check!");
        } else {
            status.setStatus(currentTurn.name() + "'s turn");
        }
    }

    /*
     * =========================
     * PauseMenu listeners
     * =========================
     */

    private void attachPauseMenuListeners() {
        PauseMenu pauseMenu = gameWindow.getPauseMenu();

        pauseMenu.getResumeButton()  .addActionListener(e -> onResume());
        pauseMenu.getSettingsButton().addActionListener(e -> onSettingsFromPause());
        pauseMenu.getSurrenderButton().addActionListener(e -> onSurrender());
        pauseMenu.getExitButton()    .addActionListener(e -> onExitMatch());
    }

    private void onResume() {
        gameController.resumeFromPause();
        gameWindow.hidePauseMenu();

        /*
         * Resume timer cua nguoi dang den luot.
         */
        GameState state = gameController.getGameState();
        if (state.getCurrentTurn() == TurnState.WHITE_TURN) {
            gameWindow.getGameTimer().startWhiteTimer();
        } else {
            gameWindow.getGameTimer().startBlackTimer();
        }
    }

    private void onSettingsFromPause() {
        new SettingsWindow(gameWindow);
    }

    private void onSurrender() {
        boolean confirmed = gameWindow.showSurrenderDialog();

        if (confirmed) {
            PieceColor localPlayer = PieceColor.WHITE;
            gameController.handleSurrender(localPlayer);
            gameWindow.getGameTimer().stopAll();
            gameWindow.hidePauseMenu();

            String winner = (localPlayer == PieceColor.WHITE) ? "Black wins!" : "White wins!";
            gameWindow.showGameOverDialog("You surrendered. " + winner);
        }
    }

    private void onExitMatch() {
        boolean confirmed = gameWindow.showLeaveRoomDialog();

        if (confirmed) {
            gameWindow.getGameTimer().stopAll();
            gameController.endMatch();
            gameWindow.dispose();

            MainWindow mainWindow = gameWindow.getMainWindow();
            if (mainWindow != null) {
                SwingUtilities.invokeLater(() -> mainWindow.setVisible(true));
            } else {
                SwingUtilities.invokeLater(MainWindowController::new);
            }
        }
    }

    /*
     * =========================
     * Chat listeners
     * =========================
     */

    private void attachChatListeners() {
        ChatPanel chatPanel = gameWindow.getChatPanel();

        chatPanel.getSendButton()  .addActionListener(e -> sendChatMessage());
        chatPanel.getMessageField().addActionListener(e -> sendChatMessage());
    }

    private void sendChatMessage() {
        ChatPanel chatPanel = gameWindow.getChatPanel();
        String text = chatPanel.getMessageField().getText().trim();

        if (text.isEmpty()) return;

        chatPanel.appendMessage("You: " + text);
        chatPanel.getMessageField().setText("");

        /*
         * TODO (sau khi có network):
         * gameClient.sendChat(new ChatPacket(text));
         */
    }

    /*
     * =========================
     * Keyboard shortcuts
     * =========================
     */

    private void attachKeyboardShortcuts() {
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");

        gameWindow.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(esc, "togglePause");

        gameWindow.getRootPane().getActionMap().put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                togglePause();
            }
        });
    }

    private void togglePause() {
        if (gameController.isGamePaused()) {
            onResume();
        } else {
            gameController.pauseMatch();
            gameWindow.getGameTimer().stopAll();   // dung timer khi pause
            gameWindow.showPauseMenu();
        }
    }
}
