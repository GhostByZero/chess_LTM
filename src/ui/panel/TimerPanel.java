package ui.panel;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimerPanel extends JPanel {

    private final JLabel whiteTimerLabel;
    private final JLabel blackTimerLabel;

    public TimerPanel() {
        setLayout(new GridLayout(2, 1, 0, 4));
        setBorder(new EmptyBorder(8, 12, 8, 12));
        ThemeManager.applyPanelTheme(this);

        /*
         * Black timer hien thi o TREN (nguoi choi Black o phia doi dien),
         * White timer o DUOI (nguoi choi White nhin vao man hinh).
         * Thu tu add phai khop voi vi tri vat ly tren ban co.
         */
        blackTimerLabel = createTimerLabel("Black: 10:00", ColorTheme.SECONDARY_TEXT);
        whiteTimerLabel = createTimerLabel("White: 10:00", ColorTheme.PRIMARY_TEXT);

        add(blackTimerLabel);
        add(whiteTimerLabel);
    }

    private JLabel createTimerLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(FontManager.NORMAL_FONT.deriveFont(Font.BOLD, 16f));
        label.setOpaque(true);
        label.setBackground(ColorTheme.WINDOW_BACKGROUND);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorTheme.BUTTON_BACKGROUND, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return label;
    }

    /*
     * =========================
     * Timer Updates
     * highlight timer dang chay bang PRIMARY_TEXT,
     * timer dang dung to xuong SECONDARY_TEXT.
     * =========================
     */

    public void updateWhiteTimer(String time) {
        whiteTimerLabel.setText("White: " + time);
    }

    public void updateBlackTimer(String time) {
        blackTimerLabel.setText("Black: " + time);
    }

    public void setWhiteActive(boolean active) {
        whiteTimerLabel.setForeground(active ? ColorTheme.WIN_COLOR   : ColorTheme.SECONDARY_TEXT);
        blackTimerLabel.setForeground(active ? ColorTheme.SECONDARY_TEXT : ColorTheme.WIN_COLOR);
    }
}