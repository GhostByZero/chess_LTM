package ui.window;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH  = 900;
    private static final int WINDOW_HEIGHT = 600;

    private JButton playButton;
    private JButton playBotButton; // 1. Khai báo thêm biến này
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
        getContentPane().setBackground(ColorTheme.WINDOW_BACKGROUND);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ColorTheme.WINDOW_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("MULTIPLAYER CHESS");
        ThemeManager.applyTitleTheme(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

<<<<<<< HEAD
        // 2. Khởi tạo riêng biệt từng nút
        playButton     = createButton("Play Multiplayer");
        playBotButton  = createButton("Play vs Computer");
        settingsButton = createButton("Settings");
        exitButton     = createButton("Exit");

        // 3. Thêm các nút vào giao diện theo thứ tự từ trên xuống
        gbc.gridy = 0; mainPanel.add(titleLabel,    gbc);
        gbc.gridy = 1; mainPanel.add(playButton,    gbc);
        gbc.gridy = 2; mainPanel.add(playBotButton, gbc); // Thêm nút Bot vào đây
        gbc.gridy = 3; mainPanel.add(settingsButton, gbc);
        gbc.gridy = 4; mainPanel.add(exitButton,    gbc);
=======
        playButton     = createButton("Play Multiplayer");
        settingsButton = createButton("Settings");
        exitButton     = createButton("Exit");

        gbc.gridy = 0; mainPanel.add(titleLabel,    gbc);
        gbc.gridy = 1; mainPanel.add(playButton,    gbc);
        gbc.gridy = 2; mainPanel.add(settingsButton, gbc);
        gbc.gridy = 3; mainPanel.add(exitButton,    gbc);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        ThemeManager.applyButtonTheme(button);
        return button;
    }

    public JButton getPlayButton()     { return playButton;     }
<<<<<<< HEAD
    public JButton getPlayBotButton()  { return playBotButton;  } // Hết lỗi đỏ ở đây
    public JButton getSettingsButton() { return settingsButton; }
    public JButton getExitButton()     { return exitButton;     }
}
=======
    public JButton getSettingsButton() { return settingsButton; }
    public JButton getExitButton()     { return exitButton;     }
}
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
