package ui.menu;

import javax.swing.*;
import java.awt.*;

public class PauseMenu extends JPanel {
    private JButton resumeButton;
    private JButton settingsButton;
    private JButton surrenderButton;
    private JButton exitButton;

    public PauseMenu() {
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
        setBackground(new Color(20, 20, 20, 220));
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

        // 1. Đổi tiêu đề thành MENU
        JLabel titleLabel = new JLabel("MENU");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        // 2. Việt hóa tên các nút
        resumeButton    = createButton("Tiếp tục");
        surrenderButton = createButton("Bỏ cuộc");
        exitButton      = createButton("Rời phòng");
        settingsButton  = createButton("Cài đặt kích thước");

        // 3. Sắp xếp lại thứ tự hiển thị: Tiếp tục -> Bỏ cuộc -> Rời phòng -> Cài đặt
        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(resumeButton, gbc);

        gbc.gridy = 2;
        add(surrenderButton, gbc);

        gbc.gridy = 3;
        add(exitButton, gbc);

        gbc.gridy = 4;
        add(settingsButton, gbc);
    }

    /*
     * =========================
     * Button Factory
     * =========================
     */

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(240, 50));
        button.setFocusPainted(false);
        return button;
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JButton getResumeButton() {
        return resumeButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getSurrenderButton() {
        return surrenderButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}