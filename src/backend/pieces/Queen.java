package backend.pieces;

import backend.board.Position;

public class Queen extends Piece {
    public Queen(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public String getSymbol() {
        return color == PieceColor.WHITE ? "Q" : "q";
    }
}
