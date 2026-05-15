package network.client;

import network.common.Message;
import network.common.MessageFactory;
import network.protocol.Protocol;

import java.io.*;
import java.net.Socket;

public class GameClient {

    private Socket         socket;
    private BufferedReader reader;
    private PrintWriter    writer;
    private ServerListener listener;

    private final String             playerName;
    private       GameClientCallback callback;
    private final ClientHandler      handler;

    private boolean connected = false;

    public GameClient(String playerName, GameClientCallback callback) {
        this.playerName = playerName;
        this.callback   = callback;
        this.handler    = new ClientHandler(callback);
        this.handler.setGameClient(this);
    }

    /*
     * =========================
     * Kết nối server
     * =========================
     */

    public boolean connect(String host) {
        try {
            socket    = new Socket(host, Protocol.PORT);
            reader    = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            writer    = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true);
            connected = true;

            // Bắt đầu thread đọc message từ server
            listener = new ServerListener(reader, handler);
            new Thread(listener, "ServerListener").start();

            // Gửi JOIN ngay sau khi kết nối
            sendRaw(MessageFactory.join(playerName));

            System.out.println("[GameClient] Kết nối: " + host
                    + " | Tên: " + playerName);
            return true;

        } catch (IOException e) {
            System.err.println("[GameClient] Kết nối thất bại: "
                    + e.getMessage());
            return false;
        }
    }

    /*
     * =========================
     * Các method GUI gọi
     * =========================
     */

    public void sendMove(int fromRow, int fromCol,
                         int toRow,   int toCol,
                         String piece, String moveType,
                         String promotion) {
        sendRaw(MessageFactory.move(
                fromRow, fromCol, toRow, toCol,
                piece, moveType,
                promotion != null ? promotion : "QUEEN"));
    }

    public void sendChat(String content) {
        sendRaw(MessageFactory.chat(playerName, content));
    }

    public void sendResign() {
        sendRaw(MessageFactory.resign());
    }

    public void sendLeave() {
        sendRaw(MessageFactory.leave());
    }

    /*
     * =========================
     * Gửi message thô
     * =========================
     */

    public void sendRaw(Message msg) {
        if (writer != null && connected) {
            writer.println(msg.toJson());
        }
    }

    /*
     * =========================
     * Đổi callback sau khi khởi tạo
     * Dùng khi WaitingRoom bắt START, sau đó GameWindow gắn callback thật
     * =========================
     */

    public void setTempCallback(GameClientCallback newCallback) {
        this.handler.setCallback(newCallback);
    }

    /*
     * =========================
     * Ngắt kết nối
     * =========================
     */

    public void disconnect() {
        connected = false;
        if (listener != null) listener.stop();
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        System.out.println("[GameClient] Đã ngắt kết nối.");
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public boolean isConnected()  { return connected;  }
    public String  getPlayerName(){ return playerName; }
}