package network.client;

import network.common.Message;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerListener implements Runnable {

    private final BufferedReader reader;
    private final ClientHandler  handler;
    private volatile boolean     running = true;

    public ServerListener(BufferedReader reader, ClientHandler handler) {
        this.reader  = reader;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                try {
                    Message msg = Message.fromJson(line);
                    // Chuyển sang ClientHandler để xử lý
                    handler.handle(msg);
                } catch (Exception e) {
                    System.err.println("[Listener] Parse lỗi: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("[Listener] Mất kết nối server!");
                handler.onConnectionLost();
            }
        }
    }

    public void stop() {
        running = false;
    }
}