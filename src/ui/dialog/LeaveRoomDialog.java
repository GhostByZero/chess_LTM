package ui.dialog;

import javax.swing.*;
import java.awt.*;

public class LeaveRoomDialog extends JDialog {

    private boolean confirmed;

    public LeaveRoomDialog(
            Frame owner
    ) {

        super(
                owner,
                "Leave Room",
                true
        );

        this.confirmed = false;

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
                        "Are you sure you want to leave the room?"
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

        JButton yesButton =
                new JButton("Yes");

        JButton noButton =
                new JButton("No");

        yesButton.addActionListener(e -> {

            confirmed = true;

            dispose();
        });

        noButton.addActionListener(e -> {

            confirmed = false;

            dispose();
        });

        buttonPanel.add(yesButton);

        buttonPanel.add(noButton);

        add(messageLabel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public boolean isConfirmed() {

        return confirmed;
    }
}