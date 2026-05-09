package ui.dialog;

import ui.window.MainWindowController;

import javax.swing.*;
import java.awt.*;

public class GameOverDialog extends JDialog {

    private final JLabel  resultLabel;

    private final JButton backToMenuButton;

    private final JButton exitButton;

    public GameOverDialog(
            Frame  owner,
            String result
    ) {

        super(owner, "Game Over", true);

        setLayout(new BorderLayout(0, 8));

        setSize(380, 200);

        setLocationRelativeTo(owner);

        /*
         * DO_NOTHING_ON_CLOSE: buoc nguoi choi phai bam mot trong hai nut,
         * tranh viec dong X roi bi ket lai o GameWindow.
         */
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        /*
         * =========================
         * Result Label
         * =========================
         */

        resultLabel = new JLabel(result);

        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 15f));

        resultLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        /*
         * =========================
         * Button Panel
         * =========================
         */

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));

        backToMenuButton = new JButton("Back to Menu");

        exitButton = new JButton("Exit");

        backToMenuButton.setPreferredSize(new Dimension(140, 36));

        exitButton.setPreferredSize(new Dimension(90, 36));

        /*
         * "Back to Menu": dong dialog + dong GameWindow (owner) + mo lai MainWindow.
         * owner la GameWindow (Frame) -- dispose() no de giai phong hoan toan,
         * tranh Main Window cu bi giu trong memory.
         */
        backToMenuButton.addActionListener(e -> {
            dispose();
            if (owner != null) {
                owner.dispose();
            }
            SwingUtilities.invokeLater(MainWindowController::new);
        });

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(backToMenuButton);

        buttonPanel.add(exitButton);

        add(resultLabel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JButton getBackToMenuButton() { return backToMenuButton; }

    public JButton getExitButton()       { return exitButton;       }
}