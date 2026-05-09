package backend.pieces;

import backend.board.Position;

public class Knight extends Piece {
    public Knight(PieceColor color, Position position) {
        super(color, position);
    }
    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "N" : "n";
    }
}
