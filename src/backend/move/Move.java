package backend.move;

import backend.board.Position;
import backend.pieces.Piece;

public class Move {
    private final Position from;
    private final Position to;

    private final Piece movedPiece;
    private Piece capturedPiece;

    private MoveType moveType;

    /**
     * Quân được phong cấp khi tốt đến hàng cuối.
     * Mặc định "QUEEN". Người gọi có thể set "ROOK"/"BISHOP"/"KNIGHT" trước khi submit move.
     */
    private String promotionChoice = "QUEEN";

    public Move(Position from, Position to, Piece movedPiece, MoveType moveType) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.moveType = moveType;
    }
    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public String getPromotionChoice() {
        return promotionChoice;
    }

    public void setPromotionChoice(String promotionChoice) {
        this.promotionChoice = (promotionChoice != null) ? promotionChoice : "QUEEN";
    }

    @Override
    public String toString() {
        return movedPiece.getSymbol() + ": " + from + " -> " + to;
    }
}
