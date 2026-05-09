package util.converter;

import backend.pieces.Piece;
import backend.pieces.*;

public class PieceConverter {

    /*
     * =========================
     * Piece -> Symbol
     * =========================
     */

    public static String pieceToSymbol(
            Piece piece
    ) {

        if (piece == null) {
            return "";
        }

        /*
         * White Pieces
         */

        if (piece instanceof King) {
            return piece.isWhite() ? "♔" : "♚";
        }

        if (piece instanceof Queen) {
            return piece.isWhite() ? "♕" : "♛";
        }

        if (piece instanceof Rook) {
            return piece.isWhite() ? "♖" : "♜";
        }

        if (piece instanceof Bishop) {
            return piece.isWhite() ? "♗" : "♝";
        }

        if (piece instanceof Knight) {
            return piece.isWhite() ? "♘" : "♞";
        }

        if (piece instanceof Pawn) {
            return piece.isWhite() ? "♙" : "♟";
        }

        return "";
    }

    /*
     * =========================
     * Piece -> Name
     * =========================
     */

    public static String pieceToName(
            Piece piece
    ) {

        if (piece == null) {
            return "None";
        }

        return piece.getClass()
                .getSimpleName();
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private PieceConverter() {

    }
}