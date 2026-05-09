package backend.pieces;

import backend.board.Position;

public class King extends Piece {
    public King(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "K" : "k";
    }
}