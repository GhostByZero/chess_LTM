package ui.panel;

import ui.theme.ColorTheme;
import ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class GameControlPanel extends JPanel {

    private final TimerPanel timerPanel;

    private final JButton    pauseButton;
    private final JButton    surrenderButton;
    private final JButton    exitButton;

    public GameControlPanel(TimerPanel timerPanel) {
        this.timerPanel = timerPanel;

        setLayout(new BorderLayout(0, 12));
<<<<<<< HEAD
        setBorder(new EmptyBorder(12, 10    , 12, 10));
=======
        setBorder(new EmptyBorder(12, 10, 12, 10));
>>>>>>> aeb8f54727ad993b994f47e75c9ecfb1e8f78213
        setPreferredSize(new Dimension(200, 0));
        ThemeManager.applyPanelTheme(this);


        add(timerPanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        buttonPanel.setOpaque(false);

        pauseButton     = createButton("⏸  Pause");
        surrenderButton = createButton("🏳  Surrender");
        exitButton      = createButton("✖  Exit Match");

        buttonPanel.add(pauseButton);
        buttonPanel.add(surrenderButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 42));
        ThemeManager.applyButtonTheme(button);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }


    public TimerPanel getTimerPanel()     { return timerPanel;     }
    public JButton    getPauseButton()    { return pauseButton;    }
    public JButton    getSurrenderButton(){ return surrenderButton; }
    public JButton    getExitButton()     { return exitButton;     }
}
