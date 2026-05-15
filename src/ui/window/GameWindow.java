package ui.window;

import backend.controller.GameController;
import backend.engine.GameManager;

import network.client.GameClient;

import ui.board.BoardPanel;
import ui.dialog.DisconnectDialog;
import ui.dialog.GameOverDialog;
import ui.dialog.LeaveRoomDialog;
import ui.dialog.SurrenderDialog;
import ui.input.BoardMouseListener;
import ui.input.InputHandler;
import ui.menu.PauseMenu;
<<<<<<< HEAD
=======

import ui.panel.ChatPanel;
import ui.panel.GameControlPanel;
<<<<<<< HEAD
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
=======
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
import ui.panel.MoveHistoryPanel;
import ui.panel.PlayerInfoPanel;
import ui.panel.StatusPanel;
import ui.panel.TimerPanel;
import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;
import util.constants.GameConstants;
import util.timer.GameTimer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameWindow extends JFrame {

    private static final int WINDOW_WIDTH  = 1100;
    private static final int WINDOW_HEIGHT = 760;

    // Kích thước ô cờ — 63px/ô = 504px tổng
    private static final int CELL_SIZE     = 63;
    private static final int BOARD_PX      = CELL_SIZE * 8; // 504
    private static final int SIDE_WIDTH    = 220;

    /*
     * =========================
     * Backend
     * =========================
     */
    private GameManager    gameManager;
    private GameController gameController;

    /*
     * =========================
     * Network
     * =========================
     */
    private GameClient gameClient;
    private String     myColor;

    /*
     * =========================
     * MainWindow reference
     * =========================
     */
    private final MainWindow mainWindow;

    /*
     * =========================
     * Layout
     * =========================
     */
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JPanel     gameScreen;

    /*
     * =========================
     * Board
     * =========================
     */
    private BoardPanel boardPanel;

    /*
     * =========================
     * Menu
     * =========================
     */
    private PauseMenu pauseMenu;

    /*
     * =========================
     * Panels
     * =========================
     */
    private JButton undoButton;
    private PlayerInfoPanel  playerInfoPanel;
    private MoveHistoryPanel moveHistoryPanel;
    private StatusPanel      statusPanel;
    private TimerPanel       timerPanel;
    private GameControlPanel gameControlPanel;
<<<<<<< HEAD

    /*
     * Chat: input ở LEFT, hiển thị tin nhắn ở CENTER bên dưới bàn cờ
     */
    private JTextField chatInputField;
    private JButton    chatSendButton;
    private JTextArea  chatDisplayArea;
=======
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213

    /*
     * =========================
     * Timer
     * =========================
     */
    private GameTimer gameTimer;

    /*
     * =========================
     * Constructor — PvE (Chơi với máy)
     * =========================
     */
    public GameWindow(MainWindow mainWindow, boolean isHardMode) {
        this.mainWindow = mainWindow;
        this.gameClient = null;
        this.myColor    = "WHITE"; // Mặc định người chơi sẽ cầm cờ Trắng
        initialize();
        new GameWindowController(this, isHardMode);
    }

    /*
     * =========================
     * Input
     * =========================
     */
    private InputHandler       inputHandler;
    private BoardMouseListener boardMouseListener;

    /*
     * =========================
     * Dialogs
     * =========================
     */
    private DisconnectDialog disconnectDialog;
    private GameOverDialog   gameOverDialog;
    private LeaveRoomDialog  leaveRoomDialog;
    private SurrenderDialog  surrenderDialog;

    /*
     * =========================
     * GlassPane
     * =========================
     */
    private JPanel glassPanePause;

    /*
     * =========================
     * Constructor — Offline
     * =========================
     */
    public GameWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.gameClient = null;
        this.myColor    = "WHITE";
        initialize();
        new GameWindowController(this);
    }

    /*
     * =========================
     * Constructor — Multiplayer
     * =========================
     */
    public GameWindow(MainWindow mainWindow,
                      GameClient client,
                      String myColor,
                      String opponentName) {
        this.mainWindow = mainWindow;
        this.gameClient = client;
        this.myColor    = myColor;
        initialize();
        new GameWindowController(this, client, myColor, opponentName);
    }

    /*
     * =========================
     * Khởi tạo chung
     * =========================
     */
    private void initialize() {
        initializeWindow();
        initializeBackend();
        initializeUI();
        initializeDialogs();
        initializeInput();
        initializeGlassPane();
        setVisible(true);
    }

    private void initializeWindow() {
        setTitle("Multiplayer Chess");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(new Color(30, 30, 30));
    }

    private void initializeBackend() {
        gameManager    = new GameManager();
        gameController = new GameController(gameManager);
    }

    /*
     * =========================
     * UI — CardLayout: MAIN_MENU | GAME
     * =========================
     */
    private void initializeUI() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(30, 30, 30));

        // Main menu giữ nguyên từ MainWindowController
        pauseMenu = new PauseMenu();

        buildGameScreen();

        contentPanel.add(buildMainMenuPanel(), "MAIN_MENU");
        contentPanel.add(gameScreen,           "GAME");

        add(contentPanel);
        showMainMenu();
    }

    // Panel main menu đơn giản bên trong GameWindow (chỉ dùng khi offline)
    private JPanel buildMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MULTIPLAYER CHESS");
        ThemeManager.applyTitleTheme(title);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton playBtn = new JButton("Play");
        ThemeManager.applyButtonTheme(playBtn);
        playBtn.setPreferredSize(new Dimension(220, 48));
        playBtn.addActionListener(e -> showGameScreen());

        JButton settingsBtn = new JButton("Settings");
        ThemeManager.applyButtonTheme(settingsBtn);
        settingsBtn.setPreferredSize(new Dimension(220, 48));
        settingsBtn.addActionListener(e -> new SettingsWindow(this));

        JButton exitBtn = new JButton("Exit");
        ThemeManager.applyButtonTheme(exitBtn);
        exitBtn.setPreferredSize(new Dimension(220, 48));
        exitBtn.addActionListener(e -> System.exit(0));

        gbc.gridy = 0; panel.add(title,      gbc);
        gbc.gridy = 1; panel.add(playBtn,    gbc);
        gbc.gridy = 2; panel.add(settingsBtn, gbc);
        gbc.gridy = 3; panel.add(exitBtn,    gbc);
        return panel;
    }

    /*
     * =========================
     * Game Screen — layout v7
     *
     *  ┌──────────┬──────────────────┬──────────┐
     *  │  LEFT    │   CENTER         │  RIGHT   │
     *  │  220px   │   co giãn        │  220px   │
     *  │          │  [bàn cờ 504px]  │          │
     *  │ Player2  │  [chat display]  │ Settings │
     *  │ Player1  │                  │ Timer    │
     *  │ ChatInput│                  │ History  │
     *  └──────────┴──────────────────┴──────────┘
     * =========================
     */
    private void buildGameScreen() {
        gameScreen = new JPanel(new BorderLayout());
        gameScreen.setBackground(new Color(30, 30, 30));

        // Status bar trên cùng
        statusPanel = new StatusPanel();
        gameScreen.add(buildStatusBar(), BorderLayout.NORTH);

        // 3 cột chính
        JPanel centerRow = new JPanel(new BorderLayout());
        centerRow.setBackground(new Color(30, 30, 30));

        centerRow.add(buildLeftPanel(),   BorderLayout.WEST);
        centerRow.add(buildCenterPanel(), BorderLayout.CENTER);
        centerRow.add(buildRightPanel(),  BorderLayout.EAST);

        gameScreen.add(centerRow, BorderLayout.CENTER);
    }

    /*
     * Status bar
     */
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(42, 42, 42));
        bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(58,58,58)));
        bar.setPreferredSize(new Dimension(0, 34));

        statusPanel = new StatusPanel();
        statusPanel.setStatus("Lượt của WHITE");
        bar.add(statusPanel, BorderLayout.CENTER);
        return bar;
    }

    /*
     * ===== LEFT PANEL 220px =====
     * Player2 info + trạng thái
     * divider
     * Player1 info + trạng thái
     * divider
     * Chat input
     */
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 45, 45));
        panel.setPreferredSize(new Dimension(SIDE_WIDTH, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(58, 58, 58)));

        panel.add(buildSectionLabel("Người chơi"));

        // Sử dụng PlayerInfoPanel động, tự sắp xếp ai ở trên ai ở dưới
        playerInfoPanel = new PlayerInfoPanel(myColor);
        panel.add(playerInfoPanel);

        panel.add(buildDivider());

        // --- THÊM NÚT UNDO BÊN TRÁI (KHÔNG LÀM LỆCH FORM) ---
        panel.add(buildSectionLabel("Hành động"));

        // Tạo lớp bọc giống hệt khung Chat để ép thẳng hàng
        JPanel undoWrap = new JPanel();
        undoWrap.setLayout(new BoxLayout(undoWrap, BoxLayout.Y_AXIS));
        undoWrap.setBackground(new Color(45, 45, 45));
        undoWrap.setBorder(new EmptyBorder(0, 12, 10, 12)); // Căn lề trái phải 12px
        undoWrap.setMaximumSize(new Dimension(SIDE_WIDTH, 45));
        undoWrap.setAlignmentX(Component.LEFT_ALIGNMENT); // QUAN TRỌNG: Ép sát lề trái

        undoButton = new JButton("↩ Quay lại (Undo)");
        ui.theme.ThemeManager.applyButtonTheme(undoButton);
        undoButton.setFont(new Font("Arial", Font.BOLD, 12));
        undoButton.setMaximumSize(new Dimension(SIDE_WIDTH - 24, 32));
        undoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        undoButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        undoWrap.add(undoButton);
        panel.add(undoWrap);

        panel.add(buildDivider());
        // ----------------------------------------------------

        // Chat input
        panel.add(buildSectionLabel("Gửi tin nhắn"));
        panel.add(buildChatInputArea());

        // Glue đẩy mọi thứ lên trên
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Arial", Font.BOLD, 9));
        lbl.setForeground(new Color(120, 120, 120));
        lbl.setBorder(new EmptyBorder(10, 12, 4, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildPlayerRow(String avatarText, String name,
                                  String colorLabel, boolean isWhite,
                                  boolean showTurnBadge) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(new Color(51, 51, 51));
        row.setBorder(new EmptyBorder(8, 10, 8, 10));
        row.setMaximumSize(new Dimension(SIDE_WIDTH, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar
        JLabel avatar = new JLabel(avatarText, SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 12));
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setOpaque(true);
        if (isWhite) {
            avatar.setBackground(new Color(232, 232, 232));
            avatar.setForeground(new Color(51, 51, 51));
        } else {
            avatar.setBackground(new Color(68, 68, 68));
            avatar.setForeground(new Color(200, 200, 200));
            avatar.setBorder(BorderFactory.createLineBorder(new Color(102,102,102)));
        }
        // Membuat avatar jadi lingkaran
        avatar.setBorder(BorderFactory.createLineBorder(
                isWhite ? new Color(180,180,180) : new Color(90,90,90), 1));

        // Nama + warna
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(FontManager.SMALL_FONT.deriveFont(Font.BOLD, 12f));
        nameLabel.setForeground(new Color(221, 221, 221));

        JLabel colorLbl = new JLabel(colorLabel);
        colorLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        colorLbl.setForeground(new Color(119, 119, 119));

        info.add(nameLabel);
        info.add(colorLbl);

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);

        if (showTurnBadge) {
            JLabel badge = new JLabel("Lượt");
            badge.setFont(new Font("Arial", Font.BOLD, 9));
            badge.setForeground(new Color(26, 26, 10));
            badge.setBackground(new Color(224, 185, 74));
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(2, 6, 2, 6));
            row.add(badge, BorderLayout.EAST);
        }

        return row;
    }

    private JPanel buildStatusDot(String text, boolean online) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(SIDE_WIDTH, 20));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(7, 7));
        dot.setBackground(online ? new Color(93,184,93) : new Color(224,82,82));
        dot.setOpaque(true);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl.setForeground(new Color(136, 136, 136));

        row.add(dot);
        row.add(lbl);
        return row;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(58, 58, 58));
        sep.setBackground(new Color(58, 58, 58));
        sep.setMaximumSize(new Dimension(SIDE_WIDTH, 1));
        return sep;
    }

    private JPanel buildChatInputArea() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(new Color(38, 38, 38));
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.setMaximumSize(new Dimension(SIDE_WIDTH, 80));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);

        chatInputField = new JTextField();
        chatInputField.setFont(new Font("Arial", Font.PLAIN, 11));
        chatInputField.setBackground(new Color(51, 51, 51));
        chatInputField.setForeground(new Color(204, 204, 204));
        chatInputField.setCaretColor(Color.WHITE);
        chatInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(85,85,85)),
                new EmptyBorder(4, 8, 4, 8)));

        chatSendButton = new JButton("Gửi");
        chatSendButton.setFont(new Font("Arial", Font.BOLD, 11));
        chatSendButton.setBackground(new Color(58, 90, 138));
        chatSendButton.setForeground(new Color(170, 205, 221));
        chatSendButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        chatSendButton.setFocusPainted(false);
        chatSendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        row.add(chatInputField, BorderLayout.CENTER);
        row.add(chatSendButton, BorderLayout.EAST);

        JLabel hint = new JLabel("💬 Tin nhắn hiển thị bên dưới bàn cờ");
        hint.setFont(new Font("Arial", Font.ITALIC, 9));
        hint.setForeground(new Color(85, 85, 85));
        hint.setBorder(new EmptyBorder(4, 0, 0, 0));

        wrap.add(row);
        wrap.add(hint);
        return wrap;
    }

    /*
     * ===== CENTER PANEL =====
     * Trên: bàn cờ căn giữa
     * Dưới: chat display area
     */
    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        // Bàn cờ
        boardPanel = new BoardPanel(gameManager.getBoard());
        boardPanel.setPreferredSize(new Dimension(BOARD_PX, BOARD_PX));

        JPanel boardWrap = new JPanel(new GridBagLayout());
        boardWrap.setBackground(new Color(30, 30, 30));
        boardWrap.add(boardPanel);

        // Chat display (tin nhắn đã gửi)
        JPanel chatDisplay = buildChatDisplayArea();

        panel.add(boardWrap,   BorderLayout.CENTER);
        panel.add(chatDisplay, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildChatDisplayArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 25));
        panel.setPreferredSize(new Dimension(0, 140));
        panel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,
                new Color(42,42,42)));

        JLabel title = new JLabel("  💬 Tin nhắn");
        title.setFont(new Font("Arial", Font.BOLD, 9));
        title.setForeground(new Color(85, 85, 85));
        title.setBorder(new EmptyBorder(5, 8, 3, 8));
        title.setPreferredSize(new Dimension(0, 22));

        chatDisplayArea = new JTextArea();
        chatDisplayArea.setEditable(false);
        chatDisplayArea.setLineWrap(true);
        chatDisplayArea.setWrapStyleWord(true);
        chatDisplayArea.setFont(new Font("Arial", Font.PLAIN, 11));
        chatDisplayArea.setBackground(new Color(25, 25, 25));
        chatDisplayArea.setForeground(new Color(187, 187, 187));
        chatDisplayArea.setBorder(new EmptyBorder(4, 12, 4, 12));

        JScrollPane scroll = new JScrollPane(chatDisplayArea);
        scroll.setBorder(null);
        scroll.setBackground(new Color(25, 25, 25));
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(title,  BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /*
     * ===== RIGHT PANEL 220px =====
     * ⚙ Settings button
     * Timer Player2 / Player1
     * Lịch sử nước đi
     */
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 45, 45));
        panel.setPreferredSize(new Dimension(SIDE_WIDTH, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(58, 58, 58)));

        panel.add(buildSectionLabel("Menu"));
        panel.add(buildSettingsButton());
        panel.add(buildSettingsHint());
        panel.add(buildDivider());

        panel.add(buildSectionLabel("Thời gian"));
        timerPanel = new TimerPanel();
        timerPanel.setMaximumSize(new Dimension(SIDE_WIDTH, 120));
        timerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(timerPanel);

        panel.add(buildDivider());
        panel.add(buildSectionLabel("Lịch sử nước đi"));

        moveHistoryPanel = new MoveHistoryPanel();
        moveHistoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Chiếm phần còn lại
        panel.add(moveHistoryPanel);

        return panel;
    }

    private JPanel buildSettingsButton() {
        JPanel btn = new JPanel(new BorderLayout(8, 0));
        btn.setBackground(new Color(58, 58, 90));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90,90,138)),
                new EmptyBorder(9, 11, 9, 11)));
        btn.setMaximumSize(new Dimension(SIDE_WIDTH - 16, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 1. Đổi tên thành Menu
        JLabel lbl = new JLabel("⚙ MENU");
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setForeground(new Color(170, 170, 204));

        JLabel arrow = new JLabel("▶");
        arrow.setFont(new Font("Arial", Font.PLAIN, 11));
        arrow.setForeground(new Color(102, 102, 136));

        btn.add(lbl,   BorderLayout.CENTER);
        btn.add(arrow, BorderLayout.EAST);

        // 2. Sửa lại sự kiện Click để bật thẳng bảng lớn, bỏ qua bảng nhỏ
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                togglePauseFromDialog(); // Gọi hàm này sẽ hiện luôn bảng Pause lớn
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(SIDE_WIDTH, 50));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(btn);
        return wrap;
    }

    private JLabel buildSettingsHint() {
        JLabel lbl = new JLabel(
                "<html><font color='#555555'>Tạm dừng · Bỏ cuộc · Rời phòng · Kích thước</font></html>");
        lbl.setFont(new Font("Arial", Font.PLAIN, 9));
        lbl.setBorder(new EmptyBorder(0, 12, 6, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /*
     * Dialog điều khiển — mở khi click nút Settings
     */

    // Gọi từ dialog — delegate sang GameWindowController
    private void togglePauseFromDialog() {
        if (gameController.isGamePaused()) {
            gameController.resumeFromPause();
            hidePauseMenu();
        } else {
            gameController.pauseMatch();
            gameTimer.stopAll();
            showPauseMenu();
        }
    }

    private void triggerSurrender() {
        boolean ok = showSurrenderDialog();
        if (ok) {
            if (gameClient != null) gameClient.sendResign();
            else {
                gameController.handleSurrender(
                        "WHITE".equals(myColor)
                                ? backend.pieces.PieceColor.WHITE
                                : backend.pieces.PieceColor.BLACK);
                gameTimer.stopAll();
                showGameOverDialog("Bạn đã bỏ cuộc!");
            }
        }
    }

    private void triggerLeave() {
        boolean ok = showLeaveRoomDialog();
        if (ok) {
            if (gameClient != null) gameClient.sendLeave();
            gameTimer.stopAll();
            gameController.endMatch();
            dispose();
            if (mainWindow != null)
                SwingUtilities.invokeLater(() -> mainWindow.setVisible(true));
            else
                SwingUtilities.invokeLater(MainWindowController::new);
        }
    }

    /*
     * =========================
     * Dialogs init
     * =========================
     */
<<<<<<< HEAD
=======

    private void initializeGameScreen() {
        gameScreen = new JPanel(new BorderLayout());

        boardPanel = new BoardPanel(gameManager.getBoard());

        JPanel topPanel   = new JPanel(new BorderLayout());
        JPanel leftPanel  = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());

        gameScreen.add(topPanel,   BorderLayout.NORTH);
        gameScreen.add(leftPanel,  BorderLayout.WEST);
        gameScreen.add(rightPanel, BorderLayout.EAST);
        gameScreen.add(boardPanel, BorderLayout.CENTER);
    }

    /*
     * =========================
     * Panels Initialization
     * =========================
     */

    private void initializePanels() {
        chatPanel        = new ChatPanel();
        moveHistoryPanel = new MoveHistoryPanel();
        playerInfoPanel  = new PlayerInfoPanel();
        statusPanel      = new StatusPanel();
        timerPanel       = new TimerPanel();
        gameControlPanel = new GameControlPanel(timerPanel);

        gameTimer = new GameTimer(GameConstants.DEFAULT_MATCH_TIME);

        attachPanelsToGameScreen();
    }

    private void attachPanelsToGameScreen() {
        BorderLayout layout = (BorderLayout) gameScreen.getLayout();

        JPanel topPanel    = (JPanel) layout.getLayoutComponent(BorderLayout.NORTH);
        JPanel leftPanel   = (JPanel) layout.getLayoutComponent(BorderLayout.WEST);
        JPanel rightPanel  = (JPanel) layout.getLayoutComponent(BorderLayout.EAST);

        // Status bar o tren board
        topPanel.add(statusPanel, BorderLayout.CENTER);

        // Trai: thong tin nguoi choi + lich su nuoc di
        leftPanel.add(playerInfoPanel,  BorderLayout.NORTH);
        leftPanel.add(moveHistoryPanel, BorderLayout.CENTER);
        leftPanel.add(chatPanel,        BorderLayout.SOUTH);

        // Phai: timer ca hai ben + nut Pause / Surrender / Exit
        rightPanel.add(gameControlPanel, BorderLayout.CENTER);
    }

    /*
     * =========================
     * Dialogs Initialization
     * =========================
     */

>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
    private void initializeDialogs() {
        disconnectDialog = new DisconnectDialog(this);
        leaveRoomDialog  = new LeaveRoomDialog(this);
        surrenderDialog  = new SurrenderDialog(this);
    }

    /*
     * =========================
     * Input init
     * =========================
     */
    private void initializeInput() {
        inputHandler       = new InputHandler(boardPanel, gameController);
        boardMouseListener = new BoardMouseListener(boardPanel, inputHandler);
    }

    /*
     * =========================
     * GlassPane — Pause overlay
     * =========================
     */
    private void initializeGlassPane() {
        glassPanePause = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        glassPanePause.setOpaque(false);
        glassPanePause.add(pauseMenu);
        glassPanePause.setVisible(false);
        setGlassPane(glassPanePause);
    }

    /*
     * =========================
     * Screen Switching
     * =========================
     */
    public void showMainMenu()   { cardLayout.show(contentPanel, "MAIN_MENU"); }
    public void showGameScreen() { cardLayout.show(contentPanel, "GAME");      }
    public void showPauseMenu()  { glassPanePause.setVisible(true);            }
    public void hidePauseMenu()  { glassPanePause.setVisible(false);           }

    /*
     * =========================
     * Chat — append tin nhắn vào display area
     * =========================
     */
    public void appendChatMessage(String sender, String content) {
        SwingUtilities.invokeLater(() -> {
            chatDisplayArea.append(sender + ": " + content + "\n");
            // Auto scroll xuống cuối
            chatDisplayArea.setCaretPosition(
                    chatDisplayArea.getDocument().getLength());
        });
    }

    /*
     * =========================
     * Dialogs
     * =========================
     */
    public void showDisconnectDialog(String playerName) {
        disconnectDialog.showWithPlayerName(playerName);
    }
    public void hideDisconnectDialog()      { disconnectDialog.setVisible(false); }

    public void showGameOverDialog(String result) {
        gameOverDialog = new GameOverDialog(this, result);
        gameOverDialog.setVisible(true);
    }

    public boolean showLeaveRoomDialog() {
        leaveRoomDialog.setVisible(true);
        return leaveRoomDialog.isConfirmed();
    }

    public boolean showSurrenderDialog() {
        surrenderDialog.setVisible(true);
        return surrenderDialog.isSurrendered();
    }

    /*
     * =========================
     * Refresh board
     * =========================
     */
    public void refreshBoard() {
        boardPanel.drawBoard();
        repaint();
    }

    /*
     * =========================
     * Getters
     * =========================
     */
    public GameController    getGameController()  { return gameController;   }
    public GameManager       getGameManager()      { return gameManager;      }
    public BoardPanel        getBoardPanel()       { return boardPanel;       }
    public PlayerInfoPanel   getPlayerInfoPanel()  { return playerInfoPanel;  }
    public MoveHistoryPanel  getMoveHistoryPanel() { return moveHistoryPanel; }
    public StatusPanel       getStatusPanel()      { return statusPanel;      }
    public TimerPanel        getTimerPanel()       { return timerPanel;       }
    public GameTimer         getGameTimer()        { return gameTimer != null ? gameTimer : (gameTimer = new GameTimer(GameConstants.DEFAULT_MATCH_TIME)); }
    public PauseMenu         getPauseMenu()        { return pauseMenu;        }
    public InputHandler      getInputHandler()     { return inputHandler;     }
    public MainWindow        getMainWindow()       { return mainWindow;       }
    public GameClient        getGameClient()       { return gameClient;       }
    public String            getMyColor()          { return myColor;          }

<<<<<<< HEAD
<<<<<<< HEAD
    // Chat getters — dùng bởi GameWindowController
    public JTextField getChatInputField()  { return chatInputField;  }
    public JButton    getChatSendButton()  { return chatSendButton;  }


    // ChatPanel không còn dùng nữa — mình dùng chatInputField + chatDisplayArea
    public ui.panel.ChatPanel getChatPanel() { return null; }
    public JButton getUndoButton() {
        return undoButton;
    }
=======
    public GameController    getGameController()    { return gameController;    }
    public GameManager       getGameManager()        { return gameManager;       }
    public BoardPanel        getBoardPanel()         { return boardPanel;        }
    public ChatPanel         getChatPanel()          { return chatPanel;         }
    public MoveHistoryPanel  getMoveHistoryPanel()   { return moveHistoryPanel;  }
    public PlayerInfoPanel   getPlayerInfoPanel()    { return playerInfoPanel;   }
    public StatusPanel       getStatusPanel()        { return statusPanel;       }
    public TimerPanel        getTimerPanel()         { return timerPanel;        }
    public GameControlPanel  getGameControlPanel()   { return gameControlPanel;  }
    public GameTimer         getGameTimer()          { return gameTimer;         }
    public PauseMenu         getPauseMenu()          { return pauseMenu;         }
    public InputHandler      getInputHandler()       { return inputHandler;      }
    public MainWindow        getMainWindow()         { return mainWindow;        }
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
}
=======
    public GameController    getGameController()    { return gameController;    }
    public GameManager       getGameManager()        { return gameManager;       }
    public BoardPanel        getBoardPanel()         { return boardPanel;        }
    public ChatPanel         getChatPanel()          { return chatPanel;         }
    public MoveHistoryPanel  getMoveHistoryPanel()   { return moveHistoryPanel;  }
    public PlayerInfoPanel   getPlayerInfoPanel()    { return playerInfoPanel;   }
    public StatusPanel       getStatusPanel()        { return statusPanel;       }
    public TimerPanel        getTimerPanel()         { return timerPanel;        }
    public GameControlPanel  getGameControlPanel()   { return gameControlPanel;  }
    public GameTimer         getGameTimer()          { return gameTimer;         }
    public PauseMenu         getPauseMenu()          { return pauseMenu;         }
    public InputHandler      getInputHandler()       { return inputHandler;      }
    public MainWindow        getMainWindow()         { return mainWindow;        }
}
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
