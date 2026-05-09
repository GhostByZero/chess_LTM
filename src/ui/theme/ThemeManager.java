package ui.theme;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    /*
     * =========================
     * Apply Window Theme
     * =========================
     */

    public static void applyWindowTheme(
            JFrame frame
    ) {

        frame.getContentPane()
                .setBackground(
                        ColorTheme.WINDOW_BACKGROUND
                );
    }

    /*
     * =========================
     * Apply Panel Theme
     * =========================
     */

    public static void applyPanelTheme(
            JPanel panel
    ) {

        panel.setBackground(
                ColorTheme.PANEL_BACKGROUND
        );
    }

    /*
     * =========================
     * Apply Button Theme
     * =========================
     */

    public static void applyButtonTheme(
            JButton button
    ) {

        button.setBackground(
                ColorTheme.BUTTON_BACKGROUND
        );

        button.setForeground(
                ColorTheme.BUTTON_TEXT
        );

        button.setFont(
                FontManager.BUTTON_FONT
        );

        button.setFocusPainted(false);
    }

    /*
     * =========================
     * Apply Label Theme
     * =========================
     */

    public static void applyTitleTheme(
            JLabel label
    ) {

        label.setForeground(
                ColorTheme.PRIMARY_TEXT
        );

        label.setFont(
                FontManager.TITLE_FONT
        );
    }

    /*
     * =========================
     * Apply Normal Text Theme
     * =========================
     */

    public static void applyTextTheme(
            JLabel label
    ) {

        label.setForeground(
                ColorTheme.PRIMARY_TEXT
        );

        label.setFont(
                FontManager.NORMAL_FONT
        );
    }

    /*
     * =========================
     * Apply ComboBox Theme
     * =========================
     */

    public static void applyComboBoxTheme(
            JComboBox<?> comboBox
    ) {

        comboBox.setFont(
                FontManager.NORMAL_FONT
        );

        comboBox.setBackground(
                Color.WHITE
        );
    }

    /*
     * =========================
     * Apply CheckBox Theme
     * =========================
     */

    public static void applyCheckBoxTheme(
            JCheckBox checkBox
    ) {

        checkBox.setBackground(
                ColorTheme.PANEL_BACKGROUND
        );

        checkBox.setForeground(
                ColorTheme.PRIMARY_TEXT
        );

        checkBox.setFont(
                FontManager.NORMAL_FONT
        );
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private ThemeManager() {

    }
}
