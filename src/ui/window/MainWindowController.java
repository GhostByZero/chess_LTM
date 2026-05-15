package ui.window;

import javax.swing.*;

public class MainWindowController {

    private final MainWindow mainWindow;
    // 1. THÊM BIẾN NÀY ĐỂ GHI NHỚ CỬA SỔ HIỆN TẠI
    private GameWindow currentGameWindow = null;

    public MainWindowController() {
        this.mainWindow = new MainWindow();
        attachListeners();
    }

    private void attachListeners() {
        attachPlayButton();
        attachPlayBotButton();
        attachSettingsButton();
        attachExitButton();
    }

    /*
     * =========================
     * Play vs Computer
     * =========================
     */
    private void attachPlayBotButton() {
        if (mainWindow.getPlayBotButton() != null) {
            mainWindow.getPlayBotButton().addActionListener(e -> onPlayBotClicked());
        }
    }

    private void onPlayBotClicked() {
        String[] options = {"Dễ (Đi ngẫu nhiên)", "Khó (Tham lam)"};
        int choice = JOptionPane.showOptionDialog(
                mainWindow, "Chọn độ khó của Máy tính:", "Chế độ PvE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        if (choice >= 0) {
            boolean isHardMode = (choice == 1);
            mainWindow.setVisible(false);

            // 2. LƯU CỬA SỔ VÀO BIẾN KHI CHƠI VỚI MÁY
            currentGameWindow = new GameWindow(mainWindow, isHardMode);
            currentGameWindow.showGameScreen();
        }
    }

    /*
     * =========================
     * Play Multiplayer
     * =========================
     */
    private void attachPlayButton() {
        mainWindow.getPlayButton().addActionListener(e -> onPlayClicked());
    }

    private void onPlayClicked() {
        mainWindow.setVisible(false);
        WaitingRoomWindow waitingRoom = new WaitingRoomWindow();

        waitingRoom.getConnectButton().addActionListener(e -> {
            String playerName = waitingRoom.getPlayerName();
            String host       = waitingRoom.getServerHost();

            if (playerName.isEmpty()) {
                waitingRoom.setStatus("Vui lòng nhập tên!");
                return;
            }

            waitingRoom.setStatus("Đang kết nối " + host + "...");
            waitingRoom.getConnectButton().setEnabled(false);

            new Thread(() -> {
                network.client.GameClient client = new network.client.GameClient(playerName, null);
                boolean ok = client.connect(host);

                SwingUtilities.invokeLater(() -> {
                    if (ok) {
                        waitingRoom.setStatus("Đã kết nối! Đang chờ đối thủ...");
                        waitingRoom.getConnectButton().setEnabled(false);
                        openGameWindowWithClient(waitingRoom, client);
                    } else {
                        waitingRoom.setStatus("Kết nối thất bại! Kiểm tra IP.");
                        waitingRoom.getConnectButton().setEnabled(true);
                    }
                });
            }).start();
        });

        waitingRoom.getCancelButton().addActionListener(e -> {
            waitingRoom.dispose();
            mainWindow.setVisible(true);
        });
    }

    private void openGameWindowWithClient(WaitingRoomWindow waitingRoom, network.client.GameClient client) {
        client.setTempCallback(new network.client.GameClientCallback() {
            @Override public void onSyncState(String bd, String ct, boolean ic, boolean igo, String ms, String mh, String lm) {}

            @Override public void onGameStart(String myColor, String opponentName) {
                // ĐÃ XÓA SwingUtilities.invokeLater Ở ĐÂY ĐỂ TRÁNH TRỄ NHỊP
                if (currentGameWindow != null && currentGameWindow.isDisplayable()) {
                    System.out.println("Đang kết nối lại vào trận cũ, bỏ qua lệnh mở cửa sổ mới...");
                    return;
                }

                waitingRoom.dispose();
                currentGameWindow = new GameWindow(mainWindow, client, myColor, opponentName);
                currentGameWindow.showGameScreen();
            }

            @Override public void onError(String d) {
                waitingRoom.setStatus("Lỗi: " + d);
            }

            @Override public void onDisconnectedFromServer() {
                waitingRoom.setStatus("Mất kết nối server!");
                waitingRoom.getConnectButton().setEnabled(true);
            }

            @Override public void onOpponentMove(int fr, int fc, int tr, int tc, String p, String mt, String pr) {}
            @Override public void onMoveResult(String r)             {}
            @Override public void onChatReceived(String s, String c) {}
            @Override public void onOpponentDisconnected(String p)   {}
            @Override public void onOpponentReconnected(String p)    {}
            @Override public void onGameOver(String r, String reason) {}
        });
    }

    private void attachSettingsButton() { mainWindow.getSettingsButton().addActionListener(e -> new SettingsWindow(mainWindow)); }
    private void attachExitButton() {
        mainWindow.getExitButton().addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(mainWindow, "Are you sure you want to quit?", "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
}