package ui.theme;

import java.awt.*;

public class FontManager {

    /*
     * =========================
     * Title Fonts
     * =========================
     */

    public static final Font TITLE_FONT =
            new Font(
                    "Arial",
                    Font.BOLD,
                    32
            );

    public static final Font SUBTITLE_FONT =
            new Font(
                    "Arial",
                    Font.BOLD,
                    24
            );

    /*
     * =========================
     * Text Fonts
     * =========================
     */

    public static final Font NORMAL_FONT =
            new Font(
                    "Arial",
                    Font.PLAIN,
                    18
            );

    public static final Font SMALL_FONT =
            new Font(
                    "Arial",
                    Font.PLAIN,
                    14
            );

    /*
     * =========================
     * Button Fonts
     * =========================
     */

    public static final Font BUTTON_FONT =
            new Font(
                    "Arial",
                    Font.BOLD,
                    18
            );

    /*
     * =========================
     * Chess Coordinate Font
     * =========================
     */

    public static final Font COORDINATE_FONT =
            new Font(
                    "Consolas",
                    Font.BOLD,
                    14
            );

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private FontManager() {

    }
}
