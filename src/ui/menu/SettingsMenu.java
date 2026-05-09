package ui.menu;

import javax.swing.*;
import java.awt.*;

public class SettingsMenu extends JPanel {
    private JComboBox<String> resolutionBox;
    private JCheckBox fullscreenCheckBox;
    private JCheckBox musicCheckBox;
    private JButton applyButton;
    public SettingsMenu() {
        initializeMenu();
        initializeComponents();
    }

    /*
     * =========================
     * Menu Initialization
     * =========================
     */

    private void initializeMenu() {
        setLayout(new GridBagLayout());
        setBackground(new Color(40, 40, 40));
    }

    /*
     * =========================
     * Components
     * =========================
     */

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("SETTINGS");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        titleLabel.setForeground(Color.WHITE);

        JLabel resolutionLabel = new JLabel("Resolution");

        resolutionLabel.setForeground(Color.WHITE);

        resolutionBox = new JComboBox<>(
                        new String[]{
                                "1280x720",
                                "1600x900",
                                "1920x1080"
                        });

        fullscreenCheckBox = new JCheckBox("Fullscreen");

        musicCheckBox = new JCheckBox("Enable Music");

        applyButton = new JButton("Apply");

        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(resolutionLabel, gbc);

        gbc.gridy = 2;
        add(resolutionBox, gbc);

        gbc.gridy = 3;
        add(fullscreenCheckBox, gbc);

        gbc.gridy = 4;
        add(musicCheckBox, gbc);

        gbc.gridy = 5;
        add(applyButton, gbc);
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JComboBox<String> getResolutionBox() {
        return resolutionBox;
    }

    public JCheckBox getFullscreenCheckBox() {
        return fullscreenCheckBox;
    }

    public JCheckBox getMusicCheckBox() {
        return musicCheckBox;
    }

    public JButton getApplyButton() {
        return applyButton;
    }
}
