package ui.board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class BoardAnimator{
    private final Timer animationTimer;
    private boolean animating;
    public BoardAnimator(){
        this.animating = false;
        this.animationTimer = new Timer(16, new AnimationLoop());
    }

    /*
     * =========================
     * Start Animation
     * =========================
     */

    public void startAnimation() {
        if (animating) {
            return;
        }
        animating = true;
        animationTimer.start();
    }
    /*
     * =========================
     * Stop Animation
     * =========================
     */

    public void stopAnimation() {
        animating = false;
        animationTimer.stop();
    }

    /*
     * =========================
     * Animation State
     * =========================
     */

    public boolean isAnimating() {
        return animating;
    }

    /*
     * =========================
     * Internal Animation Loop
     * =========================
     */

    private static class AnimationLoop implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            /*
             * Future:
             * move animation
             * fade effect
             * capture animation
             * check effect
             */
        }
    }
}
