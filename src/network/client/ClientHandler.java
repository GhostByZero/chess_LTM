package network.client;

import network.common.Message;
import network.common.MessageFactory;
import network.packet.MovePacket;
import network.packet.ChatPacket;

import javax.swing.SwingUtilities;

public class ClientHandler {

    // volatile để thread ServerListener thấy thay đổi ngay lập tức
    private volatile GameClientCallback callback;
    private          GameClient         gameClient;

    public ClientHandler(GameClientCallback callback) {
        this.callback = callback;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    /*
     * =========================
     * Đổi callback — dùng bởi GameClient.setTempCallback()
     * =========================
     */

    public void setCallback(GameClientCallback newCallback) {
        this.callback = newCallback;
    }

    /*
     * =========================
     * Phân loại message và gọi callback
     * =========================
     */

    public void handle(Message msg) {
        // Tất cả callback chạy trên EDT thread của Swing
        SwingUtilities.invokeLater(() -> dispatch(msg));
    }

    private void dispatch(Message msg) {
        // Guard — nếu callback chưa được gắn thì bỏ qua
        if (callback == null) return;

        switch (msg.getType()) {

            case START -> {
                String myColor  = msg.get("color");
                String opponent = msg.get("opponent");
                System.out.println("[Client] START | Màu: " + myColor
                        + " | Đối thủ: " + opponent);
                callback.onGameStart(myColor, opponent);
            }

            case MOVE -> {
                MovePacket packet = MovePacket.fromMessage(msg);
                System.out.println("[Client] MOVE: " + packet);
                callback.onOpponentMove(
                        packet.fromRow, packet.fromCol,
                        packet.toRow,   packet.toCol,
                        packet.piece,   packet.moveType,
                        packet.promotion);
            }

            case MOVE_RESULT -> {
                String result = msg.get("result");
                System.out.println("[Client] MOVE_RESULT: " + result);
                callback.onMoveResult(result);
            }

            case CHAT -> {
                ChatPacket packet = ChatPacket.fromMessage(msg);
                System.out.println("[Client] CHAT: " + packet);
                callback.onChatReceived(packet.sender, packet.content);
            }

            case DISCONNECT -> {
                String player = msg.get("player");
                System.out.println("[Client] DISCONNECT: " + player);
                callback.onOpponentDisconnected(player);
            }

            case RECONNECT -> {
                String player = msg.get("player");
                System.out.println("[Client] RECONNECT: " + player);
                callback.onOpponentReconnected(player);
            }

            // ── THÊM MỚI: Xử lý đồng bộ toàn bộ bàn cờ ──
            case SYNC_STATE -> {
                String boardData   = msg.get("boardData");
                String currentTurn = msg.get("currentTurn");
                boolean isCheck    = msg.getBoolean("isCheck");
                boolean isGameOver = msg.getBoolean("isGameOver");
                String matchState  = msg.get("matchState");
                // Thêm 2 dòng bóc tách mới
                String moveHistoryData   = msg.get("moveHistoryData");
                String lastMoveHighlight = msg.get("lastMoveHighlight");

                callback.onSyncState(boardData, currentTurn, isCheck, isGameOver, matchState, moveHistoryData, lastMoveHighlight);
            }

            case GAME_OVER -> {
                String result = msg.get("result");
                String reason = msg.get("reason");
                System.out.println("[Client] GAME_OVER: "
                        + result + " | " + reason);
                callback.onGameOver(result, reason);
            }

            case PING -> {
                // Trả pong ngay lập tức
                if (gameClient != null) {
                    gameClient.sendRaw(
                            MessageFactory.pong(System.currentTimeMillis()));
                }
            }

            case ERROR -> {
                String desc = msg.get("description");
                System.err.println("[Client] ERROR: " + desc);
                callback.onError(desc);
            }

            default ->
                    System.out.println("[Client] Unknown: " + msg.getType());
        }
    }

    /*
     * =========================
     * Gọi khi ServerListener mất kết nối
     * =========================
     */

    public void onConnectionLost() {
        SwingUtilities.invokeLater(() -> {
            if (callback != null) callback.onDisconnectedFromServer();
        });
    }
}