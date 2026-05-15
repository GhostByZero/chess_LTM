package ui.dialog;

import ui.window.GameWindow;
import javax.swing.*;
import java.awt.*;

public class DisconnectDialog extends JDialog {
    private final GameWindow gameWindow;
    private JLabel messageLabel;

    public DisconnectDialog(GameWindow gameWindow) {
        // Tham số 'true' ở cuối giúp bảng này block mọi tương tác với bàn cờ phía dưới
        super(gameWindow, "Chờ kết nối", true);
        this.gameWindow = gameWindow;
        initializeUI();
    }

    private void initializeUI() {
        setSize(380, 150);
        setLocationRelativeTo(gameWindow);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Không cho tắt lén bằng dấu X
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(40, 40, 40));

        // Nhãn hiển thị thông báo (Sẽ được cập nhật tên sau)
        messageLabel = new JLabel("Đang chờ đối thủ kết nối lại...", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(messageLabel, BorderLayout.CENTER);

        // Nút Rời phòng dành cho người ở lại
        JButton leaveButton = new JButton("Rời phòng luôn");
        leaveButton.setBackground(new Color(200, 50, 50));
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.setFont(new Font("Arial", Font.BOLD, 12));
        leaveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Gắn sự kiện khi bấm nút
        leaveButton.addActionListener(e -> handleLeaveRoom());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.add(leaveButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Hàm này được gọi từ GameWindow để hiển thị tên người bị văng
    public void showWithPlayerName(String playerName) {
        messageLabel.setText("Đang chờ " + playerName + " kết nối lại...");
        setVisible(true);
    }

    // Xử lý logic khi người ở lại không muốn chờ nữa
    private void handleLeaveRoom() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn rời phòng không?",
                "Xác nhận rời phòng",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Đóng bảng chờ

            // Báo cho Server biết mình cũng đi đây
            if (gameWindow.getGameClient() != null) {
                gameWindow.getGameClient().sendLeave();
            }

            // Dọn dẹp game và quay về Màn hình chính
            gameWindow.getGameTimer().stopAll();
            gameWindow.getGameController().endMatch();
            gameWindow.dispose();

            if (gameWindow.getMainWindow() != null) {
                SwingUtilities.invokeLater(() -> gameWindow.getMainWindow().setVisible(true));
            }
        }
    }
}