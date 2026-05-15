package network.server;

import backend.board.Board;
import backend.board.Position;
import backend.controller.GameController;
import backend.engine.GameManager;
import backend.move.Move;
import backend.move.MoveResult;
import backend.move.MoveType;
import backend.pieces.Piece;
import backend.pieces.PieceColor;
import network.common.Message;
import network.common.MessageFactory;
import network.packet.MovePacket;
import network.sync.GameSynchronizer;

public class MatchRoom {

    // ĐÃ XÓA CHỮ 'FINAL' Ở ĐÂY ĐỂ CÓ THỂ CẬP NHẬT LẠI KHI RECONNECT
    private SessionManager white;
    private SessionManager black;

    private final GameController gameController;
    private final GameSynchronizer gameSynchronizer;

    private boolean gameOver     = false;
    private boolean whitePaused  = false;
    private boolean blackPaused  = false;

    public MatchRoom(SessionManager white, SessionManager black) {
        this.white          = white;
        this.black          = black;
        this.gameController = new GameController(new GameManager());

        this.gameSynchronizer = new GameSynchronizer(gameController);

        white.setRoom(this);
        black.setRoom(this);
    }

    // Hàm mới: Kiểm tra tên người chơi để Reconnect
    public boolean hasPlayer(String name) {
        return (white != null && name.equals(white.getPlayerName())) ||
                (black != null && name.equals(black.getPlayerName()));
    }

    public void startGame() {
        white.send(MessageFactory.start("WHITE", black.getPlayerName()));
        black.send(MessageFactory.start("BLACK", white.getPlayerName()));
        gameController.startMatch();

        System.out.println("[Room] Bắt đầu: "
                + white.getPlayerName() + " vs " + black.getPlayerName());
    }

    public synchronized void relay(SessionManager sender, Message msg) {
        if (gameOver) return;

        if (isPaused() && msg.getType() != network.common.MessageType.PING
                && msg.getType() != network.common.MessageType.PONG) {
            sender.send(MessageFactory.error(
                    "Game đang tạm dừng — chờ đối thủ kết nối lại!"));
            return;
        }

        switch (msg.getType()) {
            case MOVE   -> handleMove(sender, msg);
            case CHAT   -> getOpponent(sender).send(msg);
            case RESIGN -> handleResign(sender);
            case LEAVE  -> handleLeave(sender);
            default     -> {}
        }
    }

    private void handleMove(SessionManager sender, Message msg) {
        PieceColor color     = getColorOf(sender);
        boolean    whiteTurn = gameController.getGameManager()
                .getTurnManager().isWhiteTurn();

        if ((color == PieceColor.WHITE) != whiteTurn) {
            sender.send(MessageFactory.error("Chưa đến lượt của bạn!"));
            return;
        }

        MovePacket packet = MovePacket.fromMessage(msg);
        Position   from   = new Position(packet.fromRow, packet.fromCol);
        Position   to     = new Position(packet.toRow,   packet.toCol);

        Piece piece = gameController.getGameManager()
                .getBoard().getTile(from).getPiece();
        if (piece == null) {
            sender.send(MessageFactory.error("Không có quân tại ô đó!"));
            return;
        }

        MoveType type = MoveType.valueOf(packet.moveType);
        Move move = new Move(from, to, piece, type);
        move.setPromotionChoice(packet.promotion);

        MoveResult result = gameController.handleMove(move);

        if (result == MoveResult.INVALID) {
            sender.send(MessageFactory.error("Nước đi không hợp lệ!"));
            return;
        }

        getOpponent(sender).send(MessageFactory.move(
                packet.fromRow, packet.fromCol,
                packet.toRow,   packet.toCol,
                piece.getSymbol(),
                move.getMoveType().name(),
                packet.promotion));

        sender.send(MessageFactory.moveResult(result.name()));

        switch (result) {
            case CHECKMATE -> endGame("CHECKMATE");
            case STALEMATE -> endGame("STALEMATE");
            case DRAW      -> endGame("DRAW");
            default        -> {}
        }
    }

    private void handleResign(SessionManager sender) {
        PieceColor loser = getColorOf(sender);
        gameController.handleSurrender(loser);
        gameOver = true;

        sender.send(MessageFactory.gameOver("LOSE", "RESIGN"));
        getOpponent(sender).send(MessageFactory.gameOver("WIN", "RESIGN"));

        RoomManager.getInstance().removeRoom(this);
        System.out.println("[Room] " + sender.getPlayerName() + " bỏ cuộc.");
    }

    private void handleLeave(SessionManager sender) {
        gameOver = true;

        sender.send(MessageFactory.gameOver("LOSE", "LEAVE"));
        getOpponent(sender).send(MessageFactory.gameOver("WIN", "LEAVE"));

        RoomManager.getInstance().removeRoom(this);
        System.out.println("[Room] " + sender.getPlayerName() + " rời phòng.");
    }

    public synchronized void handleDisconnect(SessionManager disconnected) {
        if (gameOver) return;

        gameController.handleDisconnect();

        if (disconnected == white) whitePaused = true;
        else                       blackPaused = true;

        getOpponent(disconnected).send(
                MessageFactory.disconnect(disconnected.getPlayerName()));

        System.out.println("[Room] " + disconnected.getPlayerName()
                + " mất kết nối — game tạm dừng.");

        startReconnectTimer(disconnected);
    }

    // --- SỬA ĐỔI CHÍNH TẠI ĐÂY ---
    public synchronized void handleReconnect(SessionManager newSession) {
        if (gameOver) return;

        // 1. Cập nhật lại đường truyền mới cho người chơi
        if (newSession.getPlayerName().equals(white.getPlayerName())) {
            this.white = newSession;
            whitePaused = false;
        } else {
            this.black = newSession;
            blackPaused = false;
        }

        // 2. QUAN TRỌNG: Gửi lệnh START để màn hình của họ thoát khỏi "Waiting Room" và mở bàn cờ
        newSession.send(MessageFactory.start(
                newSession == white ? "WHITE" : "BLACK",
                getOpponent(newSession).getPlayerName()
        ));

        gameController.handleReconnect();

        // 3. Báo đối thủ biết người kia đã kết nối lại (đóng Dialog)
        getOpponent(newSession).send(
                MessageFactory.reconnect(newSession.getPlayerName()));

        // 4. Gửi SYNC_STATE để xếp lại cờ
        gameSynchronizer.syncToClient(newSession);

        System.out.println("[Room] " + newSession.getPlayerName()
                + " đã kết nối lại — đã sync state.");
    }

    private void startReconnectTimer(SessionManager disconnected) {
        new Thread(() -> {
            try {
                Thread.sleep(network.protocol.Protocol.RECONNECT_TIMEOUT);
            } catch (InterruptedException ignored) {}

            synchronized (this) {
                // Kiểm tra bằng tên người chơi thay vì object cũ
                boolean isStillDisconnected = disconnected == white ? whitePaused : blackPaused;
                if (!gameOver && isStillDisconnected) {
                    gameOver = true;
                    SessionManager opponent = getOpponent(disconnected);
                    opponent.send(MessageFactory.gameOver(
                            "WIN", "DISCONNECT_TIMEOUT"));
                    RoomManager.getInstance().removeRoom(this);
                    System.out.println("[Room] " + disconnected.getPlayerName()
                            + " timeout — " + opponent.getPlayerName() + " thắng!");
                }
            }
        }).start();
    }

    private void endGame(String reason) {
        gameOver = true;
        PieceColor winner = gameController.getGameState().getWinner();

        String whiteResult, blackResult;

        if (reason.equals("STALEMATE") || reason.equals("DRAW")) {
            whiteResult = blackResult = "DRAW";
        } else {
            whiteResult = (winner == PieceColor.WHITE) ? "WIN" : "LOSE";
            blackResult = (winner == PieceColor.BLACK) ? "WIN" : "LOSE";
        }

        white.send(MessageFactory.gameOver(whiteResult, reason));
        black.send(MessageFactory.gameOver(blackResult, reason));
        RoomManager.getInstance().removeRoom(this);

        System.out.println("[Room] Kết thúc: " + reason
                + (winner != null ? " | Thắng: " + winner : " | Hòa"));
    }

    private PieceColor getColorOf(SessionManager s) {
        return s == white ? PieceColor.WHITE : PieceColor.BLACK;
    }

    private SessionManager getOpponent(SessionManager s) {
        return s == white ? black : white;
    }

    public boolean isPaused()   { return whitePaused || blackPaused; }
    public boolean isGameOver() { return gameOver; }
}