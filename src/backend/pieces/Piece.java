package backend.pieces;

import backend.board.Position;

public abstract class Piece {
    protected PieceColor color;
    protected Position position;
    protected boolean moved;

    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
        this.moved = false;
    }

    public PieceColor getColor() {
        return color;
    }
    public Position getPosition() {
        return position;
    }
    public boolean hasMoved() {
        return moved;
    }
    public void setMoved(boolean moved) {
        this.moved = moved;
    }
    public void setPosition(Position position) {this.position = position;}
    public abstract String getSymbol();

    public boolean isWhite() {
        return color == PieceColor.WHITE;
    }
    public boolean isBlack() {
        return color == PieceColor.BLACK;
    }
}
