package ui.board;

import backend.board.Board;
import backend.board.Position;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final CellPanel[][] cells;
    private final Board board;

    // THÊM MỚI: Lưu vị trí nước đi cuối cùng để tô sáng
    private Position lastFrom = null;
    private Position lastTo   = null;

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
        createCells();
        drawBoard();
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
     * Draw & Highlight
     * =========================
     */

    // THÊM MỚI: Hàm để GameWindowController gọi sau mỗi nước đi
    public void highlightLastMove(Position from, Position to) {
        this.lastFrom = from;
        this.lastTo   = to;
        drawBoard(); // Vẽ lại bàn cờ để màu sắc mới được áp dụng
    }

    public void drawBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                CellPanel cell = cells[row][col];
                Position pos = new Position(row, col);

                // Reset màu ô cờ về mặc định trước khi vẽ
                cell.drawCell(board);

                // Nếu ô này là điểm đi hoặc điểm đến của nước cờ cuối cùng -> Tô màu vàng nhạt
                if (pos.equals(lastFrom) || pos.equals(lastTo)) {
                    // Bạn có thể chỉnh mã màu (Red, Green, Blue, Alpha) tùy ý
                    cell.setBackground(new Color(255, 255, 150, 180));
                }
            }
        }
        repaint();
        revalidate();
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