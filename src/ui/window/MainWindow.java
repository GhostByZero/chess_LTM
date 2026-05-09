package ui.window;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 600;

    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;

    public MainWindow() {
        initializeWindow();
        initializeComponents();
        setVisible(true);
    }

    private void initializeWindow() {
        setTitle("Multiplayer Chess");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        mainPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        playButton = createButton("Play Multiplayer");

        settingsButton = createButton("Settings");

        exitButton = createButton("Exit");

        gbc.gridy = 0;
        mainPanel.add(playButton, gbc);

        gbc.gridy = 1;
        mainPanel.add(settingsButton, gbc);

        gbc.gridy = 2;
        mainPanel.add(exitButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFocusPainted(false);
        return button;
    }

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
