package backend.pieces;

import backend.board.Position;

public class Bishop extends Piece {
    public Bishop(PieceColor color, Position position) {
        super(color, position);
    }
    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "B" : "b";
    }
}
