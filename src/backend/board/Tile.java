package backend.board;

import backend.pieces.Piece;

public class Tile {
    private final Position position;
    private Piece piece;
    public Tile(Position position) {
        this.position = position;
    }
    public Position getPosition() {
        return position;
    }
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public boolean isOccupied() {
        return piece != null;
    }
    public void clearTile() {
        this.piece = null;
    }
}
