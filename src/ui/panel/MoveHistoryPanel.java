package ui.panel;

import javax.swing.*;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final DefaultListModel<String> moveModel;

    private final JList<String> moveList;

    public MoveHistoryPanel() {

        setLayout(new BorderLayout());

        setPreferredSize(
                new Dimension(220, 0)
        );

        JLabel title =
                new JLabel("Move History");

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        moveModel =
                new DefaultListModel<>();

        moveList =
                new JList<>(moveModel);

        JScrollPane scrollPane =
                new JScrollPane(moveList);

        add(title, BorderLayout.NORTH);

        add(scrollPane, BorderLayout.CENTER);
    }

    /*
     * =========================
     * History Methods
     * =========================
     */

    public void addMove(
            String move
    ) {

        moveModel.addElement(move);

        /*
         * Tu dong cuon xuong nuoc di moi nhat.
         * ensureIndexIsVisible() dam bao item cuoi luon hien thi
         * ma khong lam mat scroll position neu nguoi choi dang cuon len xem lich su.
         */
        int lastIndex = moveModel.getSize() - 1;
        moveList.ensureIndexIsVisible(lastIndex);
    }

    public void clearHistory() {

        moveModel.clear();
    }
}