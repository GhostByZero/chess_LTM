package ui.panel;

import backend.pieces.PieceColor;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    private final JLabel whitePlayerLabel;

    private final JLabel blackPlayerLabel;

    private final JLabel turnLabel;

    public PlayerInfoPanel() {

        setLayout(
                new GridLayout(3, 1)
        );

        setPreferredSize(
                new Dimension(250, 120)
        );

        whitePlayerLabel =
                new JLabel("White: Player 1");

        blackPlayerLabel =
                new JLabel("Black: Player 2");

        turnLabel =
                new JLabel("Turn: WHITE");

        add(whitePlayerLabel);

        add(blackPlayerLabel);

        add(turnLabel);
    }

    /*
     * =========================
     * Turn Update
     * =========================
     */

    public void updateTurn(
            PieceColor color
    ) {

        turnLabel.setText(
                "Turn: " + color
        );
    }

    /*
     * =========================
     * Player Names
     * =========================
     */

    public void setWhitePlayerName(
            String name
    ) {

        whitePlayerLabel.setText(
                "White: " + name
        );
    }

    public void setBlackPlayerName(
            String name
    ) {

        blackPlayerLabel.setText(
                "Black: " + name
        );
    }
}