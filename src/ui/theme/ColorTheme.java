package ui.theme;

import java.awt.*;

public class ColorTheme {
    /*
     * =========================
     * Board Colors
     * =========================
     */

    public static final Color LIGHT_TILE =
            new Color(240, 217, 181);

    public static final Color DARK_TILE =
            new Color(181, 136, 99);

    /*
     * =========================
     * Selection Colors
     * =========================
     */

    public static final Color SELECTED_TILE =
            new Color(246, 246, 105);

    public static final Color HIGHLIGHT_TILE =
            new Color(186, 202, 68);

    /*
     * =========================
     * Window Colors
     * =========================
     */

    public static final Color WINDOW_BACKGROUND =
            new Color(30, 30, 30);

    public static final Color PANEL_BACKGROUND =
            new Color(45, 45, 45);

    /*
     * =========================
     * Text Colors
     * =========================
     */

    public static final Color PRIMARY_TEXT =
            Color.WHITE;

    public static final Color SECONDARY_TEXT =
            new Color(180, 180, 180);

    /*
     * =========================
     * Button Colors
     * =========================
     */

    public static final Color BUTTON_BACKGROUND =
            new Color(70, 70, 70);

    public static final Color BUTTON_HOVER =
            new Color(90, 90, 90);

    public static final Color BUTTON_TEXT =
            Color.WHITE;

    /*
     * =========================
     * Match State Colors
     * =========================
     */

    public static final Color CHECK_COLOR =
            new Color(220, 50, 50);

    public static final Color WIN_COLOR =
            new Color(50, 180, 80);

    public static final Color LOSE_COLOR =
            new Color(180, 50, 50);

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private ColorTheme() {

    }
}
