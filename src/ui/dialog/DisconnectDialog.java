package ui.dialog;

import javax.swing.*;
import java.awt.*;

public class DisconnectDialog extends JDialog {

    private final JLabel messageLabel;

    public DisconnectDialog(
            Frame owner
    ) {

        super(
                owner,
                "Connection Lost",
                true
        );

        setLayout(
                new BorderLayout()
        );

        setSize(350, 150);

        setLocationRelativeTo(owner);

        /*
         * =========================
         * Message
         * =========================
         */

        messageLabel =
                new JLabel(
                        "Opponent disconnected. Waiting for reconnection..."
                );

        messageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(messageLabel, BorderLayout.CENTER);
    }

    /*
     * =========================
     * Update Message
     * =========================
     */

    public void setMessage(
            String message
    ) {

        messageLabel.setText(message);
    }
}