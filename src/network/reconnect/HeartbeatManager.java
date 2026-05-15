package network.reconnect;

import network.common.MessageFactory;
import network.protocol.Protocol;
import network.server.SessionManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartbeatManager {

    private final SessionManager         session;
    private final ScheduledExecutorService scheduler;
    private       ScheduledFuture<?>     heartbeatTask;
    private       boolean                running = false;

    // Callback — gọi khi phát hiện session timeout
    private Runnable onTimeout;

    public HeartbeatManager(SessionManager session) {
        this.session   = session;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Heartbeat-" + session.getPlayerName());
            t.setDaemon(true); // tự tắt khi main thread kết thúc
            return t;
        });
    }

    /*
     * =========================
     * Start — bắt đầu gửi ping
     * =========================
     */
    public synchronized void start() {
        if (running) return;
        running = true;

        heartbeatTask = scheduler.scheduleAtFixedRate(
                this::sendPingAndCheck,
                Protocol.PING_INTERVAL,       // delay ban đầu
                Protocol.PING_INTERVAL,       // interval
                TimeUnit.MILLISECONDS
        );

        System.out.println("[Heartbeat] Bắt đầu cho: "
                + session.getPlayerName());
    }

    /*
     * =========================
     * Stop — dừng heartbeat
     * =========================
     */
    public synchronized void stop() {
        if (!running) return;
        running = false;

        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }
        scheduler.shutdownNow();

        System.out.println("[Heartbeat] Dừng cho: "
                + session.getPlayerName());
    }

    /*
     * =========================
     * Gửi ping và kiểm tra timeout
     * =========================
     */
    private void sendPingAndCheck() {
        if (!session.isConnected()) {
            handleTimeout();
            return;
        }

        // Kiểm tra pong timeout trước khi gửi ping mới
        if (session.isPongTimeout()) {
            System.out.println("[Heartbeat] TIMEOUT: "
                    + session.getPlayerName()
                    + " — không phản hồi trong "
                    + (Protocol.PING_TIMEOUT / 1000) + "s");
            handleTimeout();
            return;
        }

        // Gửi ping xuống client
        session.send(MessageFactory.ping());
        System.out.println("[Heartbeat] PING → " + session.getPlayerName());
    }

    /*
     * =========================
     * Xử lý timeout
     * =========================
     */
    private void handleTimeout() {
        stop();
        if (onTimeout != null) {
            onTimeout.run();
        }
    }

    /*
     * =========================
     * Callback setter
     * =========================
     */
    public void setOnTimeout(Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    public boolean isRunning() { return running; }
}