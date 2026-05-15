package ui.window;

import javax.swing.*;
import java.awt.*;

public class WaitingRoomWindow extends JFrame {

    private JLabel  statusLabel;
    private JButton cancelButton;
    private JButton connectButton;  // ← thêm

    // ← thêm 2 field này
    private JTextField nameField;
    private JTextField hostField;

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
        setLayout(new BorderLayout());
        setResizable(false);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel titleLabel = new JLabel("Multiplayer Chess");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Ô nhập tên
        JLabel nameLabel = new JLabel("Tên người chơi:");
        nameLabel.setForeground(Color.WHITE);
        nameField = new JTextField("Player1", 20);

        // Ô nhập IP
        JLabel hostLabel = new JLabel("IP Server:");
        hostLabel.setForeground(Color.WHITE);
        hostField = new JTextField("localhost", 20);

        // Status
        statusLabel = new JLabel("Nhập thông tin để kết nối...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        connectButton = new JButton("Kết nối");
        connectButton.setPreferredSize(new Dimension(200, 45));
        connectButton.setFocusPainted(false);

        cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(200, 45));
        cancelButton.setFocusPainted(false);

        // Add vào panel
        gbc.gridy = 0; mainPanel.add(titleLabel,   gbc);
        gbc.gridy = 1; mainPanel.add(nameLabel,    gbc);
        gbc.gridy = 2; mainPanel.add(nameField,    gbc);
        gbc.gridy = 3; mainPanel.add(hostLabel,    gbc);
        gbc.gridy = 4; mainPanel.add(hostField,    gbc);
        gbc.gridy = 5; mainPanel.add(statusLabel,  gbc);
        gbc.gridy = 6; mainPanel.add(connectButton,gbc);
        gbc.gridy = 7; mainPanel.add(cancelButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ← thêm các getter này
    public String getPlayerName() { return nameField.getText().trim();  }
    public String getServerHost() { return hostField.getText().trim();  }
    public JButton getConnectButton() { return connectButton; }

    public void setStatus(String text) { statusLabel.setText(text); }

    public JLabel  getStatusLabel()  { return statusLabel;  }
    public JButton getCancelButton() { return cancelButton; }
}