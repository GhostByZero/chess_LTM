package ui.input;

import backend.board.Position;
import ui.board.BoardPanel;
import ui.board.CellPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardMouseListener extends MouseAdapter {
    private final BoardPanel boardPanel;
    private final InputHandler inputHandler;

    public BoardMouseListener(BoardPanel boardPanel, InputHandler inputHandler) {
        this.boardPanel = boardPanel;
        this.inputHandler = inputHandler;
        registerListeners();
    }

    /*
     * =========================
     * Register Cell Listeners
     * =========================
     */

    private void registerListeners() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                CellPanel cell = boardPanel.getCell(row, col);
                cell.addMouseListener(this);
            }
        }
    }

    /*
     * =========================
     * Mouse Click
     * =========================
     */

    @Override
    public void mouseClicked(MouseEvent e) {
        CellPanel clickedCell = (CellPanel) e.getSource();
        Position clickedPosition = clickedCell.getPosition();
        inputHandler.handleCellClick(clickedPosition);
    }
}
