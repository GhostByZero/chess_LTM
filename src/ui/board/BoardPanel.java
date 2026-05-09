package ui.board;

import backend.board.Board;
import backend.board.Position;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final CellPanel[][]  cells;
    private final Board board;
    public BoardPanel(Board board) {
        this.board = board;
        this.cells = new CellPanel[8][8];
        initalizeBoard();
    }

    /*
     * =========================
     * Board Initialization
     * =========================
     */

    private void initalizeBoard() {
        setLayout(new GridLayout(8, 8));
        setPreferredSize(new Dimension(640, 640));
        createCells(); drawBoard();
    }
    private void createCells() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                CellPanel cell = new CellPanel(this, new Position(row, col));
                cells[row][col] = cell;
                add(cell);
            }
        }
    }

    /*
     * =========================
     * Draw Board
     * =========================
     */

    public void drawBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cells[row][col].drawCell(board);
            }
        }
        repaint(); revalidate();
    }

    /*
     * =========================
     * Cell Access
     * =========================
     */

    public CellPanel getCell(int row, int col) {
        return cells[row][col];
    }

    public Board getBoard() {
        return board;
    }
}
