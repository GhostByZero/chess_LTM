package ui.panel;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {

    private final JLabel statusLabel;

    public StatusPanel() {

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(0, 40));

        ThemeManager.applyPanelTheme(this);

        statusLabel = new JLabel("Game Started");

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusLabel.setForeground(ColorTheme.PRIMARY_TEXT);

        statusLabel.setFont(FontManager.NORMAL_FONT);

        add(statusLabel, BorderLayout.CENTER);
    }

    /*
     * =========================
     * Status Update
     * =========================
     */

    public void setStatus(String status) {
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