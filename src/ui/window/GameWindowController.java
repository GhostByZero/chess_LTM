package ui.window;

import backend.board.Position;
import backend.controller.GameController;
import backend.engine.GameManager;
import backend.engine.ChessBot; // Đã thêm Import Bot
import backend.move.Move;
import backend.move.MoveType;
import backend.pieces.PieceColor;
import backend.state.GameState;
import backend.state.TurnState;

import network.client.GameClient;
import network.client.GameClientCallback;

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

public class GameWindowController {

    private final GameWindow     gameWindow;
    private final GameController gameController;
    private final GameManager    gameManager;

    // null nếu offline
    private final GameClient gameClient;
    private final String     myColor;

    // Biến chứa Bot
    private ChessBot bot;

    /*
     * =========================
     * Constructor — Offline
     * =========================
     */
    public GameWindowController(GameWindow gameWindow) {
        this.gameWindow     = gameWindow;
        this.gameController = gameWindow.getGameController();
        this.gameManager    = gameWindow.getGameManager();
        this.gameClient     = null;
        this.myColor        = "WHITE";

        attachAll();
    }

    /*
     * =========================
     * Constructor — Multiplayer
     * =========================
     */
    public GameWindowController(GameWindow gameWindow,
                                GameClient client,
                                String myColor,
                                String opponentName) {
        this.gameWindow     = gameWindow;
        this.gameController = gameWindow.getGameController();
        this.gameManager    = gameWindow.getGameManager();
        this.gameClient     = client;
        this.myColor        = myColor;

        // Gắn callback network
        if (client != null) {
            client.setTempCallback(new NetworkHandler());
            PieceColor colorEnum = "WHITE".equals(myColor) ? PieceColor.WHITE : PieceColor.BLACK;
            gameWindow.getInputHandler().setMyColor(colorEnum);
        }

        // Cập nhật tên người chơi lên UI
        setupPlayerInfo(myColor, opponentName);

        attachAll();
    }

    /*
     * =========================
     * Constructor — PvE (Chơi với máy)
     * =========================
     */
    public GameWindowController(GameWindow gameWindow, boolean isHardMode) {
        this.gameWindow     = gameWindow;
        this.gameController = gameWindow.getGameController();
        this.gameManager    = gameWindow.getGameManager();
        this.gameClient     = null;
        this.myColor        = "WHITE";

        // 1. Khởi tạo Bot (Cầm quân Đen)
        this.bot = new ChessBot(gameManager, gameController, PieceColor.BLACK, isHardMode);
        this.bot.setCallbacks(
                () -> gameWindow.refreshBoard(),
                this::onMoveExecuted
        );

        // 2. Khóa chuột, ngăn người chơi gian lận bấm quân của Máy
        gameWindow.getInputHandler().setMyColor(PieceColor.WHITE);

        // 3. Hiển thị tên máy lên màn hình
        String botName = isHardMode ? "Máy tính (Khó)" : "Máy tính (Dễ)";
        setupPlayerInfo("WHITE", botName);

        attachAll();
    }

    // Gắn tất cả listener — dùng chung cho cả 3 constructor
    private void attachAll() {
        attachPauseMenuListeners();
        attachControlPanelListeners();
        attachChatListeners();
        attachKeyboardShortcuts();
        attachMoveCallback();
        initializeTimer();

        // KẾT NỐI NÚT UNDO
        if (gameWindow.getUndoButton() != null) {
            gameWindow.getUndoButton().addActionListener(e -> handleUndo());
        }
    }

    /*
     * =========================
     * Setup tên người chơi
     * =========================
     */
    private void setupPlayerInfo(String myColor, String opponentName) {
        PlayerInfoPanel info = gameWindow.getPlayerInfoPanel();
        String myName = gameClient != null
                ? gameClient.getPlayerName() : "You";

        if ("WHITE".equals(myColor)) {
            info.setWhitePlayerName(myName);
            info.setBlackPlayerName(opponentName);
        } else {
            info.setBlackPlayerName(myName);
            info.setWhitePlayerName(opponentName);
        }
    }

    /*
     * =========================
     * Timer
     * =========================
     */
    private void initializeTimer() {
        GameTimer  gameTimer  = gameWindow.getGameTimer();
        TimerPanel timerPanel = gameWindow.getTimerPanel();

        gameTimer.getWhiteTimer().setOnTick(seconds ->
                timerPanel.updateWhiteTimer(TimeFormatter.formatTime(seconds)));
        gameTimer.getBlackTimer().setOnTick(seconds ->
                timerPanel.updateBlackTimer(TimeFormatter.formatTime(seconds)));

        gameTimer.getWhiteTimer().setOnTimeout(() -> onTimeout(PieceColor.WHITE));
        gameTimer.getBlackTimer().setOnTimeout(() -> onTimeout(PieceColor.BLACK));

        gameTimer.startWhiteTimer();
    }

    private void onTimeout(PieceColor timedOut) {
        gameWindow.getGameTimer().stopAll();
        String winner = (timedOut == PieceColor.WHITE) ? "BLACK" : "WHITE";
        String msg    = timedOut.name() + " hết giờ! " + winner + " thắng!";
        gameWindow.getStatusPanel().setStatus(msg);
        gameWindow.showGameOverDialog(msg);
    }

    /*
     * =========================
     * Move callback
     * =========================
     */
    private void attachMoveCallback() {
        gameWindow.getInputHandler().setOnMoveExecuted(this::onMoveExecuted);
    }

    public void onMoveExecuted() {
        GameState state = gameController.getGameState();
        PieceColor currentTurn = (state.getCurrentTurn() == TurnState.WHITE_TURN)
                ? PieceColor.WHITE : PieceColor.BLACK;

        // Cập nhật move history
        backend.move.MoveRecord lastMove = gameManager.getLastMove();
        if (lastMove != null) {
            Move move = lastMove.getMove();
            gameWindow.getBoardPanel().highlightLastMove(move.getFrom(), move.getTo());
            String moveText = MoveFormatter.formatDetailedMove(move);
            gameWindow.getMoveHistoryPanel().addMove(moveText);

            // Gửi dữ liệu lên Server nếu đang đánh Multiplayer
            if (gameClient != null && move.getMovedPiece().getColor().name().equals(myColor)) {
                gameClient.sendMove(
                        move.getFrom().getRow(), move.getFrom().getCol(),
                        move.getTo().getRow(),   move.getTo().getCol(),
                        move.getMovedPiece().getSymbol(),
                        move.getMoveType().name(),
                        move.getPromotionChoice()
                );
            }
        }

        // Cập nhật turn label
        gameWindow.getPlayerInfoPanel().updateTurn(currentTurn);
        StatusPanel status = gameWindow.getStatusPanel();

        // Kiểm tra kết thúc game
        if (state.isCheckmate()) {
            String winner = state.getWinner() != null
                    ? state.getWinner().name() + " thắng bằng chiếu bí!"
                    : "Chiếu bí!";
            status.setStatus(winner);
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog(winner);
            return;
        }

        if (state.isStalemate()) {
            status.setStatus("Stalemate! Hòa.");
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog("Stalemate! Hòa.");
            return;
        }

        if (state.isGameOver()) {
            status.setStatus("Hòa!");
            gameWindow.getGameTimer().stopAll();
            gameWindow.showGameOverDialog("Hòa! (Luật 50 nước hoặc lặp vị trí)");
            return;
        }

        // Swap timer
        if (currentTurn == PieceColor.WHITE) {
            gameWindow.getGameTimer().startWhiteTimer();
            gameWindow.getTimerPanel().setWhiteActive(true);
        } else {
            gameWindow.getGameTimer().startBlackTimer();
            gameWindow.getTimerPanel().setWhiteActive(false);
        }

        // Hiệu ứng chiếu tướng
        if (state.isCheck()) {
            status.setStatus(currentTurn.name() + " ĐANG BỊ CHIẾU!");
            shakeWindow();
            JOptionPane.showMessageDialog(gameWindow,
                    "⚠️ CẢNH BÁO: " + currentTurn.name() + " đang bị chiếu tướng!",
                    "Chiếu Tướng",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            status.setStatus("Lượt của " + currentTurn.name());
        }

        // Đánh thức Bot (nếu đang đánh với máy và đến lượt Đen)
        if (bot != null && currentTurn == PieceColor.BLACK && !state.isGameOver()) {
            status.setStatus("Máy đang suy nghĩ...");
            bot.triggerBotMove();
        } // Dấu ngoặc này của hàm if Bot
    } // Dấu ngoặc này là để ĐÓNG HÀM onMoveExecuted (bạn bị thiếu cái này lúc nãy)
    /*
     * =========================
       * Xử lý Hoàn tác (Undo)
     * =========================
     */
    public void handleUndo() {
        // Chỉ cho phép Undo khi đang chơi với Bot (gameClient == null)
        if (gameClient == null && bot != null) {

            // 1. Hoàn tác nước đi của Máy tính (Bot)
            boolean undoBot = gameController.undoLastMove();

            // 2. Hoàn tác nước đi của Người chơi
            boolean undoPlayer = gameController.undoLastMove();

            if (undoBot || undoPlayer) {
                // Làm mới lại bàn cờ sau khi xóa dữ liệu trong backend
                gameWindow.refreshBoard();

                // Xóa các dòng lịch sử tương ứng trên giao diện
                if (undoBot) gameWindow.getMoveHistoryPanel().removeLastMove();
                if (undoPlayer) gameWindow.getMoveHistoryPanel().removeLastMove();

                // Trả lại lượt đi cho quân Trắng (người chơi)
                gameController.getGameState().setCurrentTurn(TurnState.WHITE_TURN);
                gameWindow.getPlayerInfoPanel().updateTurn(PieceColor.WHITE);

                // Tắt highlight nước đi cũ
                gameWindow.getBoardPanel().highlightLastMove(null, null);

                // Khởi động lại đồng hồ cho quân Trắng
                gameWindow.getGameTimer().stopAll();
                gameWindow.getGameTimer().startWhiteTimer();
                gameWindow.getTimerPanel().setWhiteActive(true);

                gameWindow.getStatusPanel().setStatus("Đã hoàn tác nước đi!");
            }
        } else {
            // Cảnh báo nếu người chơi cố tình bấm Undo khi đánh Online
            JOptionPane.showMessageDialog(gameWindow,
                    "Không thể hoàn tác trong chế độ chơi Online!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /*
     * =========================
     * GameControlPanel listeners
     * =========================
     */
    private void attachControlPanelListeners() {
    }

    /*
     * =========================
     * PauseMenu listeners
     * =========================
     */
    private void attachPauseMenuListeners() {
        PauseMenu pauseMenu = gameWindow.getPauseMenu();
        pauseMenu.getResumeButton()   .addActionListener(e -> onResume());
        pauseMenu.getSettingsButton() .addActionListener(e -> new SettingsWindow(gameWindow));
        pauseMenu.getSurrenderButton().addActionListener(e -> onSurrender());
        pauseMenu.getExitButton()     .addActionListener(e -> onExitMatch());
    }

    private void onResume() {
        gameController.resumeFromPause();
        gameWindow.hidePauseMenu();
        GameState state = gameController.getGameState();
        if (state.getCurrentTurn() == TurnState.WHITE_TURN) {
            gameWindow.getGameTimer().startWhiteTimer();
        } else {
            gameWindow.getGameTimer().startBlackTimer();
        }
    }

    private void onSurrender() {
        boolean confirmed = gameWindow.showSurrenderDialog();
        if (!confirmed) return;

        // Gửi lên server nếu đang multiplayer
        if (gameClient != null) {
            gameClient.sendResign();
        } else {
            // Offline — xử lý local
            PieceColor loser = "WHITE".equals(myColor)
                    ? PieceColor.WHITE : PieceColor.BLACK;
            gameController.handleSurrender(loser);
            gameWindow.getGameTimer().stopAll();
            gameWindow.hidePauseMenu();
            String winner = (loser == PieceColor.WHITE) ? "Black" : "White";
            gameWindow.showGameOverDialog("Bạn bỏ cuộc. " + winner + " thắng!");
        }
    }

    private void onExitMatch() {
        boolean confirmed = gameWindow.showLeaveRoomDialog();
        if (!confirmed) return;

        if (gameClient != null) {
            // Thay vì dùng gameClient.sendLeave() khiến server xử thua luôn,
            // Ta ngắt kết nối mạng trực tiếp. Server sẽ tưởng bạn bị rớt mạng
            // và tự động hiện Bảng Chờ cho người đối diện.
            try {
                gameClient.disconnect();
            } catch (Exception e) {
                // Nếu class GameClient không có hàm disconnect(),
                // bạn chỉ cần XÓA dòng gameClient.sendLeave() đi là được.
            }
        }

        gameWindow.getGameTimer().stopAll();

        // KHÔNG gọi gameController.endMatch() nữa để phòng không bị xóa
        // Chỉ đóng cửa sổ hiện tại
        gameWindow.dispose();

        MainWindow mainWindow = gameWindow.getMainWindow();
        if (mainWindow != null) {
            SwingUtilities.invokeLater(() -> mainWindow.setVisible(true));
        } else {
            SwingUtilities.invokeLater(MainWindowController::new);
        }
    }

    /*
     * =========================
     * Chat
     * =========================
     */
    private void attachChatListeners() {
        gameWindow.getChatSendButton().addActionListener(e -> sendChatMessage());
        gameWindow.getChatInputField().addActionListener(e -> sendChatMessage());
    }
    private void sendChatMessage() {
        JTextField inputField = gameWindow.getChatInputField();
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        gameWindow.appendChatMessage("You", text);
        inputField.setText("");

        // Gửi lên server
        if (gameClient != null) {
            gameClient.sendChat(text);
        }
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
        gameWindow.getRootPane().getActionMap().put(
                "togglePause", new AbstractAction() {
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
            gameWindow.getGameTimer().stopAll();
            gameWindow.showPauseMenu();
        }
    }

    /*
     * =========================
     * NetworkHandler — xử lý message từ server
     * =========================
     */
    private class NetworkHandler implements GameClientCallback {
        @Override
        public void onSyncState(String boardData, String currentTurn, boolean isCheck, boolean isGameOver, String matchState, String moveHistoryData, String lastMoveHighlight) {
            System.out.println("[UI] Nhận lệnh đồng bộ bàn cờ. Lượt: " + currentTurn);

            gameManager.getBoard().clearBoard();

            // 1. ĐỒNG BỘ BÀN CỜ
            if (boardData != null && !boardData.isEmpty()) {
                String[] piecesData = boardData.split(";");
                for (String pData : piecesData) {
                    if (pData.isEmpty()) continue;
                    String[] parts = pData.split(",");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    String symbol = parts[2];
                    PieceColor color = PieceColor.valueOf(parts[3]);

                    backend.pieces.Piece piece = null;
                    Position pos = new Position(r, c);

                    switch (symbol.toUpperCase()) {
                        case "P" -> piece = new backend.pieces.Pawn(color, pos);
                        case "R" -> piece = new backend.pieces.Rook(color, pos);
                        case "N" -> piece = new backend.pieces.Knight(color, pos);
                        case "B" -> piece = new backend.pieces.Bishop(color, pos);
                        case "Q" -> piece = new backend.pieces.Queen(color, pos);
                        case "K" -> piece = new backend.pieces.King(color, pos);
                    }
                    if (piece != null) gameManager.getBoard().getTileByCoordinate(r, c).setPiece(piece);
                }
            }

            // 2. ĐỒNG BỘ LỊCH SỬ NƯỚC ĐI
            gameWindow.getMoveHistoryPanel().clearHistory();
            if (moveHistoryData != null && !moveHistoryData.isEmpty()) {
                String[] moves = moveHistoryData.split("\\|");
                for (String m : moves) {
                    if (!m.isEmpty()) gameWindow.getMoveHistoryPanel().addMove(m);
                }
            }

            // 3. ĐỒNG BỘ HIGHLIGHT NƯỚC ĐI CUỐI
            if (lastMoveHighlight != null && !lastMoveHighlight.isEmpty()) {
                String[] parts = lastMoveHighlight.split(",");
                int fr = Integer.parseInt(parts[0]);
                int fc = Integer.parseInt(parts[1]);
                int tr = Integer.parseInt(parts[2]);
                int tc = Integer.parseInt(parts[3]);
                gameWindow.getBoardPanel().highlightLastMove(new Position(fr, fc), new Position(tr, tc));
            } else {
                gameWindow.getBoardPanel().highlightLastMove(null, null);
            }

            TurnState turn = "WHITE".equals(currentTurn) ? TurnState.WHITE_TURN : TurnState.BLACK_TURN;
            gameController.getGameState().setCurrentTurn(turn);
            PieceColor turnColor = (turn == TurnState.WHITE_TURN) ? PieceColor.WHITE : PieceColor.BLACK;

            gameWindow.refreshBoard();
            gameWindow.getPlayerInfoPanel().updateTurn(turnColor);

            if (isCheck) {
                gameWindow.getStatusPanel().setStatus(currentTurn + " đang bị chiếu!");
            } else {
                gameWindow.getStatusPanel().setStatus("Đã đồng bộ! Lượt của " + currentTurn);
            }

            gameWindow.getGameTimer().stopAll();
            if (turnColor == PieceColor.WHITE) {
                gameWindow.getGameTimer().startWhiteTimer();
                gameWindow.getTimerPanel().setWhiteActive(true);
            } else {
                gameWindow.getGameTimer().startBlackTimer();
                gameWindow.getTimerPanel().setWhiteActive(false);
            }
        }

        @Override
        public void onGameStart(String color, String opponent) {}

        @Override
        public void onOpponentMove(int fromRow, int fromCol,
                                   int toRow,   int toCol,
                                   String piece, String moveType,
                                   String promotion) {
            Position from = new Position(fromRow, fromCol);
            Position to   = new Position(toRow,   toCol);

            backend.pieces.Piece p =
                    gameManager.getBoard().getTile(from).getPiece();
            if (p == null) return;

            Move move = new Move(from, to, p, MoveType.valueOf(moveType));
            move.setPromotionChoice(promotion);

            gameController.handleMove(move);
            gameWindow.getBoardPanel().highlightLastMove(from, to);
            gameWindow.refreshBoard();
            onMoveExecuted();
        }

        @Override
        public void onMoveResult(String result) {
            StatusPanel status = gameWindow.getStatusPanel();
            switch (result) {
                case "CHECK"     -> status.setStatus("Đối thủ đang bị chiếu!");
                case "CHECKMATE" -> status.setStatus("Chiếu bí!");
                case "INVALID"   -> status.setStatus("Nước đi không hợp lệ!");
                default          -> {}
            }
        }

        @Override
        public void onChatReceived(String sender, String content) {
            gameWindow.appendChatMessage(sender, content);
        }
        @Override
        public void onOpponentDisconnected(String playerName) {
            gameWindow.getStatusPanel().setStatus(playerName + " mất kết nối — đang chờ...");
            gameWindow.getGameTimer().stopAll();

            // Truyền tên đối thủ vào hàm này để nó in lên bảng
            gameWindow.showDisconnectDialog(playerName);
        }

        @Override
        public void onOpponentReconnected(String playerName) {
            gameWindow.getStatusPanel()
                    .setStatus(playerName + " đã kết nối lại!");
            gameWindow.hideDisconnectDialog();
            onResume();
        }

        @Override
        public void onGameOver(String result, String reason) {
            gameWindow.getGameTimer().stopAll();

            // THÊM DÒNG NÀY: Dọn dẹp bảng chờ đi (nếu nó đang hiển thị)
            gameWindow.hideDisconnectDialog();

            String msg = switch (reason) {
                case "CHECKMATE" ->
                        "WIN".equals(result) ? "Bạn thắng! Chiếu bí!"
                                : "Bạn thua! Chiếu bí!";
                case "RESIGN" ->
                        "WIN".equals(result) ? "Đối thủ bỏ cuộc! Bạn thắng!"
                                : "Bạn đã bỏ cuộc!";
                case "LEAVE" ->
                        "WIN".equals(result) ? "Đối thủ đã rời phòng! Trận đấu kết thúc." // Đã đổi lại câu từ
                                : "Bạn đã rời phòng!";
                case "DISCONNECT_TIMEOUT" ->
                        "WIN".equals(result) ? "Đối thủ mất kết nối quá lâu! Trận đấu hủy."
                                : "Bạn bị timeout!";
                case "STALEMATE" -> "Hòa! Stalemate!";
                case "DRAW"      -> "Hòa!";
                default          -> result;
            };

            // Nó sẽ tự bật cái bảng Game Over lên để báo cho bạn
            gameWindow.showGameOverDialog(msg);
        }

        @Override
        public void onError(String description) {
            gameWindow.getStatusPanel().setStatus("Lỗi: " + description);
        }

        @Override
        public void onDisconnectedFromServer() {
            gameWindow.getStatusPanel().setStatus("Mất kết nối server!");

            // Truyền chữ "Server" vào để thông báo là đang chờ Server kết nối lại
            gameWindow.showDisconnectDialog("Server");
        }
    }

    /*
     * =========================
     * Hiệu ứng Rung màn hình khi bị Chiếu tướng
     * =========================
     */
    private void shakeWindow() {
        java.awt.Point originalLocation = gameWindow.getLocation();
        new Thread(() -> {
            try {
                for (int i = 0; i < 6; i++) {
                    Thread.sleep(35);
                    int dx = (i % 2 == 0) ? 15 : -15;
                    SwingUtilities.invokeLater(() ->
                            gameWindow.setLocation(originalLocation.x + dx, originalLocation.y)
                    );
                }
                SwingUtilities.invokeLater(() -> gameWindow.setLocation(originalLocation));
            } catch (Exception ignored) {}
        }).start();
    }
}