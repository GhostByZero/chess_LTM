package util.timer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CountdownTimer {

    private int remainingSeconds;

    private final Timer timer;

    private boolean running;

    /*
     * Callback duoc goi moi giay khi timer tick.
     * Nhan remainingSeconds hien tai de UI cap nhat label.
     */
    private java.util.function.IntConsumer onTick;

    /*
     * Callback duoc goi khi het gio (remainingSeconds = 0).
     */
    private Runnable onTimeout;

    public CountdownTimer(
            int startSeconds
    ) {

        this.remainingSeconds =
                startSeconds;

        this.running = false;

        this.timer =
                new Timer(
                        1000,
                        new TimerTask()
                );
    }

    /*
     * =========================
     * Start
     * =========================
     */

    public void start() {

        if (running) {
            return;
        }

        running = true;

        timer.start();
    }

    /*
     * =========================
     * Stop
     * =========================
     */

    public void stop() {

        running = false;

        timer.stop();
    }

    /*
     * =========================
     * Reset
     * =========================
     */

    public void reset(
            int seconds
    ) {

        stop();

        remainingSeconds =
                seconds;
    }

    /*
     * =========================
     * Callbacks
     * =========================
     */

    public void setOnTick(java.util.function.IntConsumer onTick) {
        this.onTick = onTick;
    }

    public void setOnTimeout(Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    /*
     * =========================
     * Tick
     * =========================
     */

    private class TimerTask
            implements ActionListener {

        @Override
        public void actionPerformed(
                ActionEvent e
        ) {

            if (remainingSeconds > 0) {
                remainingSeconds--;
                if (onTick != null) {
                    onTick.accept(remainingSeconds);
                }
            } else {
                stop();
                if (onTimeout != null) {
                    onTimeout.run();
                }
            }
        }
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public int getRemainingSeconds() {

        return remainingSeconds;
    }

    public boolean isRunning() {

        return running;
    }
}