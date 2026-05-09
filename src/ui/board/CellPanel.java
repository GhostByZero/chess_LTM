package ui.board;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class CellPanel extends JPanel {
    private final BoardPanel boardPanel;
    private final Position position;
    private boolean highlighted;
    private boolean selected;
    public CellPanel(BoardPanel boardPanel, Position position) {
        this.boardPanel = boardPanel;
        this.position = position;
        this.highlighted = false;
        this.selected = false;
        initalizeCell();
    }

    /*
     * =========================
     * Cell Initialization
     * =========================
     */

    private void initalizeCell() {
        setPreferredSize(new Dimension(80, 80));
        assignCellColor();
    }

    /*
     * =========================
     * Draw Cell
     * =========================
     */
    public void drawCell(Board board) {
        removeAll(); assignCellColor();

        Tile tile = board.getTile(position);

        if (tile != null && tile.isOccupied()) {
            Piece piece = tile.getPiece();
            JLabel pieceLabel = PieceRenderer.renderPiece(piece);
            add(pieceLabel);
        }
        repaint(); revalidate();
    }

    /*
     * =========================
     * Cell Coloring
     * =========================
     */

    private void assignCellColor() {
        boolean lightTile = (position.getRow() + position.getCol()) % 2 == 0;
        if (selected) { setBackground(new Color(255, 215, 0)); return; }

        if (highlighted) { setBackground( new Color(144, 238, 144) ); return;}

        if (lightTile) {
            setBackground(new Color(240, 217, 181));
        }
        else {
            setBackground(new Color(181, 136, 99));
        }
    }

    /*
     * =========================
     * Highlight
     * =========================
     */

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        assignCellColor();
        repaint();
    }

    /*
     * =========================
     * Selection
     * =========================
     */

    public void setSelected(boolean selected) {
        this.selected = selected;
        assignCellColor();
        repaint();
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public Position getPosition() {
        return position;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public boolean isSelected() {
        return selected;
    }
}
