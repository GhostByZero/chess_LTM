package ui.panel;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final DefaultListModel<String> moveModel;

    private final JList<String> moveList;

    public MoveHistoryPanel() {

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(220, 0));

        ThemeManager.applyPanelTheme(this);

        JLabel title = new JLabel("Move History");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(ColorTheme.PRIMARY_TEXT);
        title.setFont(FontManager.NORMAL_FONT);

        moveModel = new DefaultListModel<>();

        moveList = new JList<>(moveModel);
        moveList.setBackground(ColorTheme.PANEL_BACKGROUND);
        moveList.setForeground(ColorTheme.PRIMARY_TEXT);
        moveList.setFont(FontManager.SMALL_FONT);

        JScrollPane scrollPane = new JScrollPane(moveList);
        scrollPane.getViewport().setBackground(ColorTheme.PANEL_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorTheme.BUTTON_BACKGROUND));

        add(title,      BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }


    /*
     * =========================
     * History Methods
     * =========================
     */

    public void addMove(String move) {
        moveModel.addElement(move);

        int lastIndex = moveModel.getSize() - 1;
        moveList.ensureIndexIsVisible(lastIndex);
    }

    public void clearHistory() {
        moveModel.clear();
    }
    public void removeLastMove() {
        if (!moveModel.isEmpty()) {
            moveModel.remove(moveModel.getSize() - 1);
        }
    }
}