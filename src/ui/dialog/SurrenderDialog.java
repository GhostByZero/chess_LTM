package ui.dialog;

import javax.swing.*;
import java.awt.*;

public class SurrenderDialog extends JDialog {

    private boolean surrendered;

    public SurrenderDialog(
            Frame owner
    ) {

        super(
                owner,
                "Surrender",
                true
        );

        surrendered = false;

        setLayout(
                new BorderLayout()
        );

        setSize(400, 180);

        setLocationRelativeTo(owner);

        /*
         * =========================
         * Message
         * =========================
         */

        JLabel messageLabel =
                new JLabel(
                        "Do you really want to surrender?"
                );

        messageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        /*
         * =========================
         * Buttons
         * =========================
         */

        JPanel buttonPanel =
                new JPanel();

        JButton surrenderButton =
                new JButton("Surrender");

        JButton cancelButton =
                new JButton("Cancel");

        surrenderButton.addActionListener(e -> {

            surrendered = true;

            dispose();
        });

        cancelButton.addActionListener(e -> {

            surrendered = false;

            dispose();
        });

        buttonPanel.add(surrenderButton);

        buttonPanel.add(cancelButton);

        add(messageLabel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public boolean isSurrendered() {

        return surrendered;
    }
}