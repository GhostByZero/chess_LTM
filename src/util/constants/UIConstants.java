package util.constants;

public final class UIConstants {

    /*
     * =========================
     * Window
     * =========================
     */

    public static final int WINDOW_WIDTH = 1400;

    public static final int WINDOW_HEIGHT = 900;

    /*
     * =========================
     * Board
     * =========================
     */

    public static final int TILE_SIZE = 80;

    public static final int BOARD_PIXEL_SIZE =
            TILE_SIZE * 8;

    /*
     * =========================
     * Panels
     * =========================
     */

    public static final int CHAT_PANEL_WIDTH = 300;

    public static final int HISTORY_PANEL_WIDTH = 220;

    public static final int TIMER_PANEL_HEIGHT = 100;

    public static final int STATUS_PANEL_HEIGHT = 40;

    /*
     * =========================
     * Animation
     * =========================
     */

    public static final int ANIMATION_FPS = 60;

    public static final int ANIMATION_DELAY =
            1000 / ANIMATION_FPS;

    /*
     * =========================
     * Fonts
     * =========================
     */

    public static final int TITLE_FONT_SIZE = 32;

    public static final int NORMAL_FONT_SIZE = 18;

    public static final int SMALL_FONT_SIZE = 14;

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private UIConstants() {

    }
}