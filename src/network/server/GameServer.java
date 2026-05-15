package network.server;

import network.protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    public static void main(String[] args) {
        System.out.println("=== Chess Server v" + Protocol.VERSION
                + " | Port " + Protocol.PORT + " ===");

        try (ServerSocket serverSocket = new ServerSocket(Protocol.PORT)) {

            while (true) {
                // Chờ client kết nối
                Socket socket = serverSocket.accept();
                System.out.println("[Server] Client kết nối: "
                        + socket.getInetAddress().getHostAddress());

                try {
                    // Mỗi client chạy trên 1 thread riêng
                    SessionManager session = new SessionManager(socket);
                    new Thread(session).start();
                } catch (IOException e) {
                    System.err.println("[Server] Lỗi tạo session: "
                            + e.getMessage());
                    socket.close();
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] Lỗi khởi động: " + e.getMessage());
        }
    }
}