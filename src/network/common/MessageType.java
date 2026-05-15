package network.common;

public enum MessageType {
    // Kết nối
    JOIN,
    START,

    // Game
    MOVE,
    MOVE_RESULT,

    // Chat
    CHAT,

    // Trạng thái mạng
    DISCONNECT,
    RECONNECT,
    PING,
    PONG,

    // Hành động trong game
    LEAVE,
    RESIGN,
    GAME_OVER,

    // Đồng bộ trạng thái (dùng khi reconnect)
    SYNC_STATE,

    // Lỗi
    ERROR
}