package util.timer;

public class GameTimer {

    private final CountdownTimer whiteTimer;

    private final CountdownTimer blackTimer;

    public GameTimer(
            int startSeconds
    ) {

        whiteTimer =
                new CountdownTimer(
                        startSeconds
                );

        blackTimer =
                new CountdownTimer(
                        startSeconds
                );
    }

    /*
     * =========================
     * White Timer
     * =========================
     */

    public void startWhiteTimer() {

        blackTimer.stop();

        whiteTimer.start();
    }

    /*
     * =========================
     * Black Timer
     * =========================
     */

    public void startBlackTimer() {

        whiteTimer.stop();

        blackTimer.start();
    }

    /*
     * =========================
     * Stop All
     * =========================
     */

    public void stopAll() {

        whiteTimer.stop();

        blackTimer.stop();
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public CountdownTimer getWhiteTimer() {

        return whiteTimer;
    }

    public CountdownTimer getBlackTimer() {

        return blackTimer;
    }
}