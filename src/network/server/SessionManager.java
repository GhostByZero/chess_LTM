package network.server;

import network.common.Message;
import network.common.MessageType;
import network.protocol.Protocol;

import java.io.*;
import java.net.Socket;

public class SessionManager implements Runnable {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    private String playerName = "Unknown";
    private MatchRoom room;
    private boolean joined = false;

    private long lastPongTime = System.currentTimeMillis();

    public SessionManager(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Message msg = Message.fromJson(line);
                handleMessage(msg);
            }
            // Khi line == null tức là client đã chủ động ngắt kết nối (bấm nút rời)
            handleClientDisconnect();
        } catch (IOException e) {
            // Lỗi mạng, tắt đột ngột
            handleClientDisconnect();
        } finally {
            close();
        }
    }

    // Hàm mới: Gom chung logic xử lý khi mất kết nối
    private void handleClientDisconnect() {
        if (playerName != null && !playerName.equals("Unknown")) {
            System.out.println("[Session] " + playerName + " mất kết nối!");
        }
        if (room != null) {
            room.handleDisconnect(this);
        } else {
            RoomManager.getInstance().cancelWaiting(this);
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.getType()) {
            case JOIN -> {
                if (!joined) {
                    playerName = msg.get("playerName");
                    joined     = true;
                    System.out.println("[Session] JOIN: " + playerName);
                    RoomManager.getInstance().matchmaking(this);
                }
            }
            case PONG -> lastPongTime = System.currentTimeMillis();
            default -> {
                if (room != null) room.relay(this, msg);
            }
        }
    }

    public void send(Message msg) {
        if (writer != null && !socket.isClosed()) {
            writer.println(msg.toJson());
        }
    }

    public void close() {
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    public boolean isConnected() {
        return !socket.isClosed() && socket.isConnected();
    }

    public boolean isPongTimeout() {
        return System.currentTimeMillis() - lastPongTime > Protocol.PING_TIMEOUT;
    }

    public void updatePongTime() { lastPongTime = System.currentTimeMillis(); }

    public String getPlayerName()        { return playerName;      }
    public void   setRoom(MatchRoom room){ this.room = room;        }
    public MatchRoom getRoom()           { return room;             }
    public boolean isJoined()            { return joined;           }
    public long getLastPongTime()        { return lastPongTime;     }
}