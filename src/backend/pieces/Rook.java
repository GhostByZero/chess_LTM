package backend.pieces;

import backend.board.Position;

public class Rook extends Piece {
    public Rook(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "R" : "r";
    }
}
