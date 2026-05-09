package ui.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardHandler extends KeyAdapter {
    private boolean escPressed;

    public KeyboardHandler() {
        this.escPressed = false;
    }

    /*
     * =========================
     * Key Pressed
     * =========================
     */

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escPressed = true;
            /*
             * Future:
             * open pause menu
             */
        }
    }

    /*
     * =========================
     * Key Released
     * =========================
     */

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escPressed = false;
        }
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public boolean isEscPressed() {
        return escPressed;
    }
}
