package ui.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DragHandler extends MouseAdapter {
    private boolean dragging;

    public DragHandler() {
        this.dragging = false;
    }

    /*
     * =========================
     * Mouse Pressed
     * =========================
     */

    @Override
    public void mousePressed(MouseEvent e) {
        dragging = true;
    }

    /*
     * =========================
     * Mouse Released
     * =========================
     */

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public boolean isDragging() {
        return dragging;
    }
}