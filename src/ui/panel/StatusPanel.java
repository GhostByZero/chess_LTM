package ui.panel;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {

    private final JLabel statusLabel;

    public StatusPanel() {

        setLayout(
                new BorderLayout()
        );

        setPreferredSize(
                new Dimension(0, 40)
        );

        statusLabel =
                new JLabel(
                        "Game Started"
                );

        statusLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(statusLabel, BorderLayout.CENTER);
    }

    /*
     * =========================
     * Status Update
     * =========================
     */

    public void setStatus(
            String status
    ) {

        statusLabel.setText(status);
    }

    /*
     * =========================
     * Getter
     * =========================
     */

    public JLabel getStatusLabel() {

        return statusLabel;
    }
}