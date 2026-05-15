package network.common;

public class MessageFactory {

    public static Message join(String playerName) {
        return new Message(MessageType.JOIN)
                .put("playerName", playerName);
    }

    public static Message start(String color, String opponentName) {
        return new Message(MessageType.START)
                .put("color",    color)
                .put("opponent", opponentName);
    }

    public static Message move(int fromRow, int fromCol,
                               int toRow,   int toCol,
                               String piece, String moveType,
                               String promotion) {
        return new Message(MessageType.MOVE)
                .put("fromRow",   fromRow)
                .put("fromCol",   fromCol)
                .put("toRow",     toRow)
                .put("toCol",     toCol)
                .put("piece",     piece)
                .put("moveType",  moveType)
                .put("promotion", promotion);
    }

    public static Message moveResult(String result) {
        return new Message(MessageType.MOVE_RESULT)
                .put("result", result);
    }

    public static Message chat(String sender, String content) {
        return new Message(MessageType.CHAT)
                .put("sender",  sender)
                .put("content", content);
    }

    public static Message disconnect(String playerName) {
        return new Message(MessageType.DISCONNECT)
                .put("player", playerName);
    }

    public static Message reconnect(String playerName) {
        return new Message(MessageType.RECONNECT)
                .put("player", playerName);
    }

    public static Message ping() {
        return new Message(MessageType.PING)
                .put("timestamp", System.currentTimeMillis());
    }

    public static Message pong(long timestamp) {
        return new Message(MessageType.PONG)
                .put("timestamp", timestamp);
    }

    public static Message leave() {
        return new Message(MessageType.LEAVE);
    }

    public static Message resign() {
        return new Message(MessageType.RESIGN);
    }

    public static Message gameOver(String result, String reason) {
        return new Message(MessageType.GAME_OVER)
                .put("result", result)
                .put("reason", reason);
    }

    public static Message error(String description) {
        return new Message(MessageType.ERROR)
                .put("description", description);
    }

    // ── THÊM MỚI: Tạo gói tin đồng bộ toàn bộ bàn cờ ──
    public static Message syncState(String boardData, String currentTurn,
                                    boolean isCheck, boolean isGameOver, String matchState,
                                    String moveHistoryData, String lastMoveHighlight) {
        Message msg = new Message(network.common.MessageType.SYNC_STATE);
        msg.put("boardData", boardData);
        msg.put("currentTurn", currentTurn);
        msg.put("isCheck", String.valueOf(isCheck));
        msg.put("isGameOver", String.valueOf(isGameOver));
        msg.put("matchState", matchState);
        // Thêm 2 dòng mới này
        msg.put("moveHistoryData", moveHistoryData);
        msg.put("lastMoveHighlight", lastMoveHighlight);
        return msg;
    }
}