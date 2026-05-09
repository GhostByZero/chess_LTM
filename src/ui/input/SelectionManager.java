package ui.input;

import backend.board.Position;
import backend.move.Move;
import ui.board.BoardPanel;
import ui.board.CellPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionManager {
    private Position selectedPosition;
    private final List<Position> highlightedPositions;

    /*
     * [FIX #2] Map từ ô đích -> Move object đầy đủ (với đúng MoveType).
     * Thay thế cách cũ: chỉ lưu Position rồi tạo new Move(NORMAL) khi click.
     * MoveValidator.isMoveLegal() đã mutate moveType (CASTLING/EN_PASSANT/PROMOTION)
     * trên các Move object này trong quá trình generateMoves() -- cần giữ nguyên.
     */
    private final Map<Position, Move> legalMoveMap;

    public SelectionManager() {
        this.highlightedPositions = new ArrayList<>();
        this.legalMoveMap         = new HashMap<>();
    }

    /*
     * =========================
     * Selection
     * =========================
     */

    public void select(Position position) {
        this.selectedPosition = position;
    }

    public void clearSelection() {
        this.selectedPosition = null;
    }

    public boolean hasSelection() {
        return selectedPosition != null;
    }

    /*
     * =========================
     * Highlight
     * =========================
     */

    /**
     * Highlight mot o dich, dong thoi luu Move object day du tuong ung.
     * Dung thay the highlight(Position) cu khi co danh sach legal moves.
     */
    public void highlight(Move move) {
        Position to = move.getTo();
        highlightedPositions.add(to);
        legalMoveMap.put(to, move);
    }

    /**
     * Overload giu lai de khong break code khac (neu co), nhung khong dung
     * trong flow chinh nua -- khong co Move object nen khong vao legalMoveMap.
     */
    public void highlight(Position position) {
        highlightedPositions.add(position);
    }

    public void clearHighlights() {
        highlightedPositions.clear();
        legalMoveMap.clear();
    }

    /**
     * Kiem tra mot o co nam trong danh sach nuoc di hop le khong.
     */
    public boolean isHighlighted(Position position) {
        return highlightedPositions.contains(position);
    }

    /**
     * [FIX #2] Tra ve Move object day du (dung MoveType) cho o dich.
     * Tra ve null neu o do khong phai legal move -- caller phai kiem tra.
     */
    public Move getLegalMove(Position to) {
        return legalMoveMap.get(to);
    }

    /*
     * =========================
     * Apply Highlight To UI
     * =========================
     */

    public void applyHighlights(BoardPanel boardPanel) {
        clearBoardHighlights(boardPanel);
        for (Position position : highlightedPositions) {
            CellPanel cell = boardPanel.getCell(position.getRow(), position.getCol());
            cell.setHighlighted(true);
        }
    }

    public void clearBoardHighlights(BoardPanel boardPanel) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardPanel.getCell(row, col).setHighlighted(false);
            }
        }
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public Position getSelectedPosition() {
        return selectedPosition;
    }

    public List<Position> getHighlightedPositions() {
        return highlightedPositions;
    }
}
