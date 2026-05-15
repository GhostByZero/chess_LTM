package network.client;

public interface GameClientCallback {

    // Game bắt đầu — myColor: "WHITE"/"BLACK", opponent: tên đối thủ
    void onGameStart(String myColor, String opponentName);

    // Đối thủ vừa đi nước cờ
    void onOpponentMove(int fromRow, int fromCol,
                        int toRow,   int toCol,
                        String piece, String moveType,
                        String promotion);

    // Kết quả nước đi của mình:
    // SUCCESS / CHECK / CHECKMATE / STALEMATE / DRAW / INVALID
    void onMoveResult(String result);

    // Nhận tin nhắn chat
    void onChatReceived(String sender, String content);

    // Đối thủ mất kết nối — hiện dialog chờ
    void onOpponentDisconnected(String playerName);

    // Đối thủ kết nối lại — đóng dialog chờ
    void onOpponentReconnected(String playerName);

    // ── THÊM MỚI: Đồng bộ trạng thái toàn bộ bàn cờ (dùng khi reconnect) ──
    void onSyncState(String boardData, String currentTurn,
                     boolean isCheck, boolean isGameOver, String matchState,
                     String moveHistoryData, String lastMoveHighlight);

    // Game kết thúc
    // result: WIN / LOSE / DRAW
    // reason: CHECKMATE / STALEMATE / RESIGN / LEAVE / DISCONNECT_TIMEOUT / DRAW
    void onGameOver(String result, String reason);

    // Lỗi từ server (nước đi sai, chưa đến lượt...)
    void onError(String description);

    // Mất kết nối hoàn toàn với server
    void onDisconnectedFromServer();
}