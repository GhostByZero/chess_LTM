package ui.window;

import javax.swing.*;

public class MainWindowController {

    private final MainWindow mainWindow;

    public MainWindowController() {
        this.mainWindow = new MainWindow();
        attachListeners();
    }

    /*
     * =========================
     * Gắn tất cả listener
     * =========================
     */

    private void attachListeners() {
        attachPlayButton();
        attachSettingsButton();
        attachExitButton();
    }

    /*
     * =========================
     * Play button
     * =========================
     */

    private void attachPlayButton() {
        mainWindow.getPlayButton().addActionListener(e -> onPlayClicked());
    }

    private void onPlayClicked() {
        mainWindow.setVisible(false);

        WaitingRoomWindow waitingRoom = new WaitingRoomWindow();

        /*
         * [FIX #8] Luu Timer vao bien de co the stop() trong Cancel listener.
         * Truoc: Timer la local variable -> Cancel khong the dung no -> openGameWindow()
         * van chay sau 2 giay du nguoi choi da quay ve main menu.
         */
        Timer matchTimer = new Timer(2000, e -> {
            waitingRoom.dispose();
            openGameWindow();
        });
        matchTimer.setRepeats(false);
        matchTimer.start();

        waitingRoom.getCancelButton().addActionListener(e -> {
            matchTimer.stop();         // dam bao Timer khong fire sau khi Cancel
            waitingRoom.dispose();
            mainWindow.setVisible(true);
        });
    }

    private void openGameWindow() {
        /*
         * Truyen mainWindow vao GameWindow de GameWindow co the tra nguoi choi
         * ve dung mainWindow nay (setVisible true) thay vi tao MainWindow moi.
         * Tranh memory leak: mainWindow cu bi giu an trong heap.
         */
        GameWindow gameWindow = new GameWindow(mainWindow);
        gameWindow.showGameScreen();
    }

    /*
     * =========================
     * Settings button
     * =========================
     */

    private void attachSettingsButton() {
        mainWindow.getSettingsButton().addActionListener(e -> onSettingsClicked());
    }

    private void onSettingsClicked() {
        new SettingsWindow(mainWindow);
    }

    /*
     * =========================
     * Exit button
     * =========================
     */

    private void attachExitButton() {
        mainWindow.getExitButton().addActionListener(e -> onExitClicked());
    }

    private void onExitClicked() {
        int confirm = JOptionPane.showConfirmDialog(
                mainWindow,
                "Are you sure you want to quit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
