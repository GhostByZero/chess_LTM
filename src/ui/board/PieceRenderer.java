package ui.board;

import backend.pieces.*;

import javax.swing.*;
import java.awt.*;

public class PieceRenderer {
    private static final Font PIECE_FONT = new Font("SansSerif", Font.PLAIN, 48);

    /*
     * =========================
     * Render Piece
     * =========================
     */

    public static JLabel renderPiece(Piece piece) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(PIECE_FONT);
        label.setText(getPieceSymbol(piece));
        return label;
    }

    /*
     * =========================
     * Unicode Chess Symbols
     * =========================
     */

    private static String getPieceSymbol(Piece piece) {
        if (piece == null) return "";

        boolean white = piece.getColor() == PieceColor.WHITE;
        if (piece instanceof King) {
            return white ? "♔" : "♚";
        }

        if (piece instanceof Queen) {
            return white ? "♕" : "♛";
        }

        if (piece instanceof Rook) {
            return white ? "♖" : "♜";
        }

        if (piece instanceof Bishop) {
            return white ? "♗" : "♝";
        }

        if (piece instanceof Knight) {
            return white ? "♘" : "♞";
        }

        if (piece instanceof Pawn) {
            return white ? "♙" : "♟";
        }
        return "";
    }
}
