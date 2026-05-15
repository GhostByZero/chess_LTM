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

    // THÊM MỚI: Biến lưu màu của người chơi để khóa màn hình
    private PieceColor myColor = null;

    private Runnable onMoveExecuted;

    public InputHandler(BoardPanel boardPanel, GameController gameController) {
        this.boardPanel       = boardPanel;
        this.gameController   = gameController;
        this.selectionManager = new SelectionManager();
    }

    // THÊM MỚI: Hàm để GameWindowController set màu cho ổ khóa
    public void setMyColor(PieceColor color) {
        this.myColor = color;
    }

    public void setOnMoveExecuted(Runnable callback) {
        this.onMoveExecuted = callback;
    }

    public void handleCellClick(Position clickedPosition) {
        Board board       = boardPanel.getBoard();
        Tile  clickedTile = board.getTile(clickedPosition);

        if (!selectionManager.hasSelection()) {
            trySelect(clickedPosition, clickedTile);
            return;
        }

        Position from     = selectionManager.getSelectedPosition();
        Tile     fromTile = board.getTile(from);

        if (fromTile == null || !fromTile.isOccupied()) {
            resetSelection();
            return;
        }

        Piece selectedPiece = fromTile.getPiece();

        if (clickedTile != null
                && clickedTile.isOccupied()
                && clickedTile.getPiece().getColor() == selectedPiece.getColor()) {

            resetSelection();
            trySelect(clickedPosition, clickedTile);
            return;
        }

        if (!selectionManager.isHighlighted(clickedPosition)) {
            resetSelection();
            return;
        }

        Move move = selectionManager.getLegalMove(clickedPosition);

        if (move == null) {
            resetSelection();
            return;
        }

        if (move.getMoveType() == MoveType.PROMOTION) {
            String choice = askPromotionChoice();
            move.setPromotionChoice(choice);
        }

        MoveResult result = gameController.handleMove(move);

        resetSelection();
        boardPanel.drawBoard();

        if (result != MoveResult.INVALID && onMoveExecuted != null) {
            onMoveExecuted.run();
        }
    }

    private void trySelect(Position position, Tile tile) {
        if (tile == null || !tile.isOccupied()) {
            return;
        }

        Piece piece = tile.getPiece();

        if (!isCorrectTurn(piece.getColor())) {
            return;
        }

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

        for (Move move : legalMoves) {
            selectionManager.highlight(move);
        }

        selectionManager.applyHighlights(boardPanel);
    }

    private boolean isCorrectTurn(PieceColor color) {
        // ─── ĐÂY LÀ CHỐT CHẶN KHÓA QUYỀN ĐIỀU KHIỂN ───
        // Nếu đang chơi online và bấm vào quân không phải màu của mình -> Cấm
        if (this.myColor != null && color != this.myColor) {
            return false;
        }

        TurnState current = gameController
                .getGameState()
                .getCurrentTurn();

        return (color == PieceColor.WHITE && current == TurnState.WHITE_TURN)
                || (color == PieceColor.BLACK && current == TurnState.BLACK_TURN);
    }

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

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}