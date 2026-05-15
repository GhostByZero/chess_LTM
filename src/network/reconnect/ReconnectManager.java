package network.reconnect;

import network.client.GameClient;
import network.client.GameClientCallback;
import network.protocol.Protocol;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReconnectManager {

    private static final int MAX_ATTEMPTS    = 5;    // thử tối đa 5 lần
    private static final int RETRY_INTERVAL  = 5;    // mỗi 5 giây thử lại

    private final String             host;
    private final String             playerName;
    private final GameClientCallback callback;

    private       GameClient              gameClient;
    private final ScheduledExecutorService scheduler;
    private       ScheduledFuture<?>      retryTask;
    private final AtomicInteger           attempts = new AtomicInteger(0);
    private       boolean                 running  = false;

    // Callback — thông báo kết quả cho UI
    private Runnable onReconnectSuccess;
    private Runnable onReconnectFailed;
    private java.util.function.Consumer<Integer> onAttempt; // gọi mỗi lần thử, truyền số lần

    public ReconnectManager(String host,
                            String playerName,
                            GameClientCallback callback) {
        this.host       = host;
        this.playerName = playerName;
        this.callback   = callback;
        this.scheduler  = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ReconnectManager");
            t.setDaemon(true);
            return t;
        });
    }

    /*
     * =========================
     * Bắt đầu thử reconnect
     * =========================
     */
    public synchronized void startReconnect() {
        if (running) return;
        running = true;
        attempts.set(0);

        System.out.println("[Reconnect] Bắt đầu thử reconnect — host: "
                + host + " | player: " + playerName);

        // Thử ngay lập tức lần đầu, sau đó mỗi RETRY_INTERVAL giây
        retryTask = scheduler.scheduleAtFixedRate(
                this::attemptReconnect,
                0,
                RETRY_INTERVAL,
                TimeUnit.SECONDS
        );
    }

    /*
     * =========================
     * Thử kết nối lại 1 lần
     * =========================
     */
    private void attemptReconnect() {
        int attempt = attempts.incrementAndGet();

        System.out.println("[Reconnect] Lần thử " + attempt
                + "/" + MAX_ATTEMPTS);

        // Thông báo UI đang thử lần thứ mấy
        if (onAttempt != null) {
            onAttempt.accept(attempt);
        }

        // Tạo GameClient mới và thử kết nối
        gameClient = new GameClient(playerName, callback);
        boolean ok = gameClient.connect(host);

        if (ok) {
            // Kết nối thành công!
            System.out.println("[Reconnect] ✅ Kết nối lại thành công"
                    + " sau " + attempt + " lần thử!");
            stop();
            if (onReconnectSuccess != null) {
                onReconnectSuccess.run();
            }
            return;
        }

        System.out.println("[Reconnect] ❌ Lần " + attempt + " thất bại.");

        // Hết số lần thử
        if (attempt >= MAX_ATTEMPTS) {
            System.out.println("[Reconnect] Đã thử " + MAX_ATTEMPTS
                    + " lần — từ bỏ.");
            stop();
            if (onReconnectFailed != null) {
                onReconnectFailed.run();
            }
        }
    }

    /*
     * =========================
     * Dừng retry
     * =========================
     */
    public synchronized void stop() {
        if (!running) return;
        running = false;

        if (retryTask != null) {
            retryTask.cancel(false);
        }
        scheduler.shutdownNow();

        System.out.println("[Reconnect] Đã dừng.");
    }

    /*
     * =========================
     * Callback setters
     * =========================
     */
    public void setOnReconnectSuccess(Runnable onSuccess) {
        this.onReconnectSuccess = onSuccess;
    }

    public void setOnReconnectFailed(Runnable onFailed) {
        this.onReconnectFailed = onFailed;
    }

    public void setOnAttempt(java.util.function.Consumer<Integer> onAttempt) {
        this.onAttempt = onAttempt;
    }

    /*
     * =========================
     * Getters
     * =========================
     */
    public GameClient getNewClient()  { return gameClient;          }
    public int        getAttempts()   { return attempts.get();      }
    public boolean    isRunning()     { return running;             }
}