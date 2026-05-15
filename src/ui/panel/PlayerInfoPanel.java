package ui.panel;

import backend.pieces.PieceColor;
import ui.theme.FontManager;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    private final int SIDE_WIDTH = 220;

    private final JLabel whiteNameLabel;
    private final JLabel blackNameLabel;

    private final JLabel whiteBadge;
    private final JLabel blackBadge;

    private final JLabel whiteStatus;
    private final JLabel blackStatus;

<<<<<<< HEAD
<<<<<<< HEAD
    public PlayerInfoPanel(String myColor) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        whiteNameLabel = createNameLabel("Player 1");
        blackNameLabel = createNameLabel("Player 2");

        whiteBadge = createBadge();
        blackBadge = createBadge();

        whiteStatus = createStatusLabel("Online");
        blackStatus = createStatusLabel("Online");

        // Set trạng thái mặc định (Lượt của Trắng)
        updateTurn(PieceColor.WHITE);

        // Tự động sắp xếp: Đối thủ ở trên, Mình ở dưới
        if ("BLACK".equals(myColor)) {
            add(buildPlayerSection("W", whiteNameLabel, "Quân trắng", true, whiteBadge, whiteStatus));
            add(buildDivider());
            add(buildPlayerSection("B", blackNameLabel, "Quân đen", false, blackBadge, blackStatus));
        } else {
            add(buildPlayerSection("B", blackNameLabel, "Quân đen", false, blackBadge, blackStatus));
            add(buildDivider());
            add(buildPlayerSection("W", whiteNameLabel, "Quân trắng", true, whiteBadge, whiteStatus));
        }
        add(buildDivider());
=======
        setLayout(new GridLayout(3, 1));

        setPreferredSize(new Dimension(250, 120));

        ThemeManager.applyPanelTheme(this);

        whitePlayerLabel = new JLabel("White: Player 1");
        blackPlayerLabel = new JLabel("Black: Player 2");
        turnLabel        = new JLabel("Turn: WHITE");

=======
        setLayout(new GridLayout(3, 1));

        setPreferredSize(new Dimension(250, 120));

        ThemeManager.applyPanelTheme(this);

        whitePlayerLabel = new JLabel("White: Player 1");
        blackPlayerLabel = new JLabel("Black: Player 2");
        turnLabel        = new JLabel("Turn: WHITE");

>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
        ThemeManager.applyTextTheme(whitePlayerLabel);
        ThemeManager.applyTextTheme(blackPlayerLabel);
        ThemeManager.applyTextTheme(turnLabel);

        add(whitePlayerLabel);
        add(blackPlayerLabel);
        add(turnLabel);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
    }

    public void setWhitePlayerName(String name) { whiteNameLabel.setText(name); }
    public void setBlackPlayerName(String name) { blackNameLabel.setText(name); }

    public void updateTurn(PieceColor color) {
<<<<<<< HEAD
<<<<<<< HEAD
        if (color == PieceColor.WHITE) {
            whiteBadge.setVisible(true);
            blackBadge.setVisible(false);
            whiteStatus.setText("Online · Đang đi...");
            blackStatus.setText("Online · Chờ tới lượt...");
        } else {
            whiteBadge.setVisible(false);
            blackBadge.setVisible(true);
            whiteStatus.setText("Online · Chờ tới lượt...");
            blackStatus.setText("Online · Đang đi...");
        }
=======
        turnLabel.setText("Turn: " + color);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
=======
        turnLabel.setText("Turn: " + color);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
    }

    // ==========================================
    // CÁC HÀM XÂY DỰNG GIAO DIỆN CON
    // ==========================================

<<<<<<< HEAD
<<<<<<< HEAD
    private JLabel createNameLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FontManager.SMALL_FONT != null ? FontManager.SMALL_FONT.deriveFont(Font.BOLD, 12f) : new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(221, 221, 221));
        return lbl;
    }

    private JLabel createBadge() {
        JLabel badge = new JLabel("Lượt");
        badge.setFont(new Font("Arial", Font.BOLD, 9));
        badge.setForeground(new Color(26, 26, 10));
        badge.setBackground(new Color(224, 185, 74));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(2, 6, 2, 6));
        return badge;
    }

    private JLabel createStatusLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl.setForeground(new Color(136, 136, 136));
        return lbl;
    }

    private JPanel buildPlayerSection(String avatarText, JLabel nameLabel, String colorLabel, boolean isWhite, JLabel badge, JLabel statusLabel) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dòng 1: Avatar + Tên + Badge Lượt
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(new Color(51, 51, 51));
        row.setBorder(new EmptyBorder(8, 10, 8, 10));
        row.setMaximumSize(new Dimension(SIDE_WIDTH, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        }
        avatar.setBorder(BorderFactory.createLineBorder(isWhite ? new Color(180,180,180) : new Color(90,90,90), 1));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(nameLabel);

        JLabel colorLbl = new JLabel(colorLabel);
        colorLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        colorLbl.setForeground(new Color(119, 119, 119));
        info.add(colorLbl);

        row.add(avatar, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        row.add(badge, BorderLayout.EAST);

        // Dòng 2: Trạng thái (Online dot)
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        statusRow.setOpaque(false);
        statusRow.setMaximumSize(new Dimension(SIDE_WIDTH, 20));
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(7, 7));
        dot.setBackground(new Color(93,184,93));
        dot.setOpaque(true);

        statusRow.add(dot);
        statusRow.add(statusLabel);

        wrap.add(row);
        wrap.add(statusRow);
        return wrap;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(58, 58, 58));
        sep.setBackground(new Color(58, 58, 58));
        sep.setMaximumSize(new Dimension(SIDE_WIDTH, 1));
        return sep;
=======
    public void setWhitePlayerName(String name) {
        whitePlayerLabel.setText("White: " + name);
    }

    public void setBlackPlayerName(String name) {
        blackPlayerLabel.setText("Black: " + name);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
=======
    public void setWhitePlayerName(String name) {
        whitePlayerLabel.setText("White: " + name);
    }

    public void setBlackPlayerName(String name) {
        blackPlayerLabel.setText("Black: " + name);
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
    }
}