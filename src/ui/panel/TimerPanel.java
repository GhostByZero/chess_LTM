package ui.panel;

import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {

    private final JLabel whiteTimerLabel;

    private final JLabel blackTimerLabel;

    public TimerPanel() {

        setLayout(
                new GridLayout(2, 1)
        );

        setPreferredSize(
                new Dimension(200, 100)
        );

        whiteTimerLabel =
                new JLabel(
                        "White: 10:00"
                );

        blackTimerLabel =
                new JLabel(
                        "Black: 10:00"
                );

        whiteTimerLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        blackTimerLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(whiteTimerLabel);

        add(blackTimerLabel);
    }

    /*
     * =========================
     * Timer Updates
     * =========================
     */

    public void updateWhiteTimer(
            String time
    ) {

        whiteTimerLabel.setText(
                "White: " + time
        );
    }

    public void updateBlackTimer(
            String time
    ) {

        blackTimerLabel.setText(
                "Black: " + time
        );
    }
}