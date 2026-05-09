package ui.window;

import javax.swing.*;
import java.awt.*;

public class WaitingRoomWindow extends JFrame {
    private JLabel statusLabel;
    private JButton cancelButton;
    public WaitingRoomWindow() {
        initializeWindow();
        initializeComponents();
        setVisible(true);
    }
    private void initializeWindow() {
        setTitle("Waiting Room");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new  BorderLayout());
        setResizable(false);
    }
    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        statusLabel = new JLabel("Đang tìm đối thủ...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);

        cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(200, 45));
        cancelButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy = 0;
        mainPanel.add(statusLabel, gbc);

        gbc.gridy = 1;         // ← thêm dòng này
        mainPanel.add(cancelButton, gbc);  // ← thêm dòng này

        add(mainPanel, BorderLayout.CENTER);
    }
    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }
}
