package backend.pieces;

import backend.board.Position;

public class Pawn extends Piece {
    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }
    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "P" : "p";
    }
}
