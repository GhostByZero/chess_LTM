package ui.input;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.controller.GameController;
import backend.move.Move;
import backend.move.MoveGenerator;
import backend.move.MoveResult;
import backend.move.MoveType;
import backend.pieces.Piece;
import backend.pieces.PieceColor;
import backend.state.TurnState;

import ui.board.BoardPanel;

import javax.swing.JOptionPane;
import java.util.List;

public class InputHandler {
    private final BoardPanel boardPanel;
    private final GameController gameController;
    private final SelectionManager selectionManager;

    /*
     * [FIX #1] Khong giu MoveGenerator rieng trong InputHandler.
     * Lay truc tiep tu ChessEngine de dung cung instance da duoc
     * khoi tao voi MoveManager -- dam bao isMoveLegal() simulate dung.
     * Truoc: new MoveGenerator(board) -- constructor 1-arg dung MoveValidator
     *        khong co MoveManager, nen khong loc duoc nuoc di tu chieu.
     */

    /*
     * Callback duoc goi sau moi nuoc di thanh cong.
     * GameWindowController gan vao bang setOnMoveExecuted().
     */
    private Runnable onMoveExecuted;

    public InputHandler(BoardPanel boardPanel, GameController gameController) {
        this.boardPanel       = boardPanel;
        this.gameController   = gameController;
        this.selectionManager = new SelectionManager();
    }

    /*
     * =========================
     * Callback setter
     * =========================
     */

    public void setOnMoveExecuted(Runnable callback) {
        this.onMoveExecuted = callback;
    }

    /*
     * =========================
     * Handle Cell Click
     * =========================
     */

    public void handleCellClick(Position clickedPosition) {
        Board board       = boardPanel.getBoard();
        Tile  clickedTile = board.getTile(clickedPosition);

        /*
         * Chua chon quan nao
         */
        if (!selectionManager.hasSelection()) {
            trySelect(clickedPosition, clickedTile);
            return;
        }

        /*
         * Dang co quan duoc chon
         */
        Position from     = selectionManager.getSelectedPosition();
        Tile     fromTile = board.getTile(from);

        if (fromTile == null || !fromTile.isOccupied()) {
            resetSelection();
            return;
        }

        Piece selectedPiece = fromTile.getPiece();

        /*
         * Click vao quan cung mau -> chuyen selection sang quan moi
         */
        if (clickedTile != null
                && clickedTile.isOccupied()
                && clickedTile.getPiece().getColor() == selectedPiece.getColor()) {

            resetSelection();
            trySelect(clickedPosition, clickedTile);
            return;
        }

        /*
         * Click vao o khong nam trong danh sach nuoc di hop le -> reset
         */
        if (!selectionManager.isHighlighted(clickedPosition)) {
            resetSelection();
            return;
        }

        /*
         * [FIX #2] Lay dung Move object (voi MoveType da duoc set boi MoveValidator)
         * thay vi tao new Move(from, to, piece, MoveType.NORMAL).
         *
         * MoveValidator.isMoveLegal() mutate moveType tren Move object trong
         * generateMoves() -- nen cac Move trong legalMoveMap da co dung:
         *   CASTLING    -- neu la nuoc nhap thanh
         *   EN_PASSANT  -- neu la nuoc bat qua duong
         *   PROMOTION   -- neu la nuoc phong cap
         *   CAPTURE     -- neu an quan thuong
         *   NORMAL      -- nuoc di binh thuong
         */
        Move move = selectionManager.getLegalMove(clickedPosition);

        if (move == null) {
            /*
             * Fallback phong thu: khong tim duoc Move object (khong nen xay ra
             * vi isHighlighted() da check truoc), nhung reset cho an toan.
             */
            resetSelection();
            return;
        }

        /*
         * [FIX #6] Neu la nuoc phong cap (PROMOTION), hoi nguoi choi muon
         * phong thanh quan gi truoc khi gui move xuong backend.
         * Backend doc move.getPromotionChoice() trong executePromotion().
         * Default la "QUEEN" neu dong dialog ma khong chon.
         */
        if (move.getMoveType() == MoveType.PROMOTION) {
            String choice = askPromotionChoice();
            move.setPromotionChoice(choice);
        }

        MoveResult result = gameController.handleMove(move);

        resetSelection();
        boardPanel.drawBoard();

        /*
         * Thong bao cho UI neu nuoc di thanh cong
         */
        if (result != MoveResult.INVALID && onMoveExecuted != null) {
            onMoveExecuted.run();
        }
    }

    /*
     * =========================
     * Try Select Piece
     * =========================
     */

    private void trySelect(Position position, Tile tile) {
        if (tile == null || !tile.isOccupied()) {
            return;
        }

        Piece piece = tile.getPiece();

        /*
         * Chi cho chon quan dung luot
         */
        if (!isCorrectTurn(piece.getColor())) {
            return;
        }

        /*
         * [FIX #1] Lay MoveGenerator tu ChessEngine (da co MoveManager)
         * thay vi khoi tao new MoveGenerator(board) rieng.
         * Dam bao generateMoves() loc duoc nuoc di tu chieu va detect
         * dung MoveType cho castling/en_passant/promotion.
         */
        MoveGenerator moveGenerator = gameController
                .getGameManager()
                .getChessEngine()
                .getMoveGenerator();

        List<Move> legalMoves = moveGenerator.generateMoves(piece);

        if (legalMoves.isEmpty()) {
            return;
        }

        selectionManager.select(position);
        boardPanel.getCell(position.getRow(), position.getCol()).setSelected(true);

        selectionManager.clearHighlights();

        /*
         * [FIX #2] Highlight bang Move object (khong phai chi Position)
         * de luu duoc MoveType vao legalMoveMap.
         */
        for (Move move : legalMoves) {
            selectionManager.highlight(move);
        }

        selectionManager.applyHighlights(boardPanel);
    }

    /*
     * =========================
     * Turn Check
     * =========================
     */

    private boolean isCorrectTurn(PieceColor color) {
        TurnState current = gameController
                .getGameState()
                .getCurrentTurn();

        return (color == PieceColor.WHITE && current == TurnState.WHITE_TURN)
                || (color == PieceColor.BLACK && current == TurnState.BLACK_TURN);
    }

    /*
     * =========================
     * Promotion Dialog
     * [FIX #6]
     * =========================
     */

    /**
     * Hien thi dialog de nguoi choi chon quan phong cap.
     * Tra ve ten quan (QUEEN/ROOK/BISHOP/KNIGHT).
     * Neu nguoi choi dong dialog ma khong chon, mac dinh Queen.
     */
    private String askPromotionChoice() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                boardPanel,
                "Choose promotion piece:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice < 0) return "QUEEN";
        return options[choice].toUpperCase();
    }

    /*
     * =========================
     * Reset Selection
     * =========================
     */

    private void resetSelection() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardPanel.getCell(row, col).setSelected(false);
            }
        }

        selectionManager.clearBoardHighlights(boardPanel);
        selectionManager.clearHighlights();
        selectionManager.clearSelection();
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}
