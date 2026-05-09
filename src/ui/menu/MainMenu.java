package ui.menu;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;

    public MainMenu() {
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
        setBackground(new Color(30, 30, 30));
    }

    /*
     * =========================
     * Components
     * =========================
     */

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("MULTIPLAYER CHESS");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

        titleLabel.setForeground(Color.WHITE);

        playButton = createButton("Play");
        settingsButton = createButton("Settings");
        exitButton = createButton("Exit");

        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(playButton, gbc);

        gbc.gridy = 2;
        add(settingsButton, gbc);

        gbc.gridy = 3;
        add(exitButton, gbc);
    }

    /*
     * =========================
     * Button Factory
     * =========================
     */

    private JButton createButton(String text) {
        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(220, 50));
        button.setFocusPainted(false);

        return button;
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}
