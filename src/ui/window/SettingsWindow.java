package ui.window;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * [FIX #7] Doi tu JFrame sang JDialog.
 *
 * Ly do: JFrame mo doc lap, nguoi choi van interact duoc voi cua so phia sau
 * (GameWindow hoac MainWindow) trong khi Settings dang mo.
 * JDialog voi modal=true block input ve cua so cha cho den khi Settings dong.
 *
 * Caller truyen owner Frame vao constructor -- SettingsWindow hien len
 * giua man hinh so voi owner va block owner cho den khi dispose().
 */
public class SettingsWindow extends JDialog {

    /*
     * =========================
     * Preference key constants
     * =========================
     */

    private static final String PREF_RESOLUTION    = "resolution";
    private static final String PREF_FULLSCREEN     = "fullscreen";
    private static final String PREF_MUSIC          = "music";
    private static final String DEFAULT_RESOLUTION  = "1280x720";

    /*
     * =========================
     * Components
     * =========================
     */

    private JComboBox<String> resolutionBox;
    private JCheckBox fullscreenCheckBox;
    private JCheckBox musicCheckBox;
    private JButton saveButton;
    private JButton backButton;

    /*
     * =========================
     * Persistent storage
     * =========================
     */

    private final Preferences prefs =
            Preferences.userNodeForPackage(SettingsWindow.class);

    /*
     * =========================
     * Constructor
     * =========================
     */

    /**
     * @param owner Frame cha (MainWindow hoac GameWindow).
     *              Dung lam anchor de dat vi tri dialog va block input.
     *              Co the null -- luc do dialog float tu do nhu truoc.
     */
    public SettingsWindow(Frame owner) {
        super(owner, "Settings", true);   // modal = true
        initializeWindow(owner);
        initializeComponents();
        loadSavedSettings();
        attachListeners();
        setResizable(false);
        setVisible(true);
    }

    /*
     * =========================
     * Window setup
     * =========================
     */

    private void initializeWindow(Frame owner) {
        setSize(480, 320);
        setLocationRelativeTo(owner);     // center tren owner, khong phai man hinh
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /*
     * =========================
     * Components
     * =========================
     */

    private void initializeComponents() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.insets = new Insets(8, 0, 8, 16);
        labelGbc.gridx  = 0;

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.anchor  = GridBagConstraints.WEST;
        fieldGbc.fill    = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        fieldGbc.insets  = new Insets(8, 0, 8, 0);
        fieldGbc.gridx   = 1;

        // -- Resolution --
        labelGbc.gridy = 0;
        settingsPanel.add(new JLabel("Resolution:"), labelGbc);

        resolutionBox = new JComboBox<>(new String[]{
                "1280x720",
                "1600x900",
                "1920x1080"
        });
        fieldGbc.gridy = 0;
        settingsPanel.add(resolutionBox, fieldGbc);

        // -- Fullscreen --
        labelGbc.gridy = 1;
        settingsPanel.add(new JLabel("Fullscreen:"), labelGbc);

        fullscreenCheckBox = new JCheckBox();
        fieldGbc.gridy = 1;
        settingsPanel.add(fullscreenCheckBox, fieldGbc);

        // -- Music --
        labelGbc.gridy = 2;
        settingsPanel.add(new JLabel("Enable Music:"), labelGbc);

        musicCheckBox = new JCheckBox();
        fieldGbc.gridy = 2;
        settingsPanel.add(musicCheckBox, fieldGbc);

        add(settingsPanel, BorderLayout.CENTER);

        // -- Buttons --
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        backButton = new JButton("Back");
        saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(90, 36));
        backButton.setPreferredSize(new Dimension(90, 36));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Load saved preferences
     * =========================
     */

    private void loadSavedSettings() {
        resolutionBox.setSelectedItem(prefs.get(PREF_RESOLUTION, DEFAULT_RESOLUTION));
        fullscreenCheckBox.setSelected(prefs.getBoolean(PREF_FULLSCREEN, false));
        musicCheckBox.setSelected(prefs.getBoolean(PREF_MUSIC, true));
    }

    /*
     * =========================
     * Listeners
     * =========================
     */

    private void attachListeners() {
        saveButton.addActionListener(e -> onSave());
        backButton.addActionListener(e -> dispose());
    }

    private void onSave() {
        prefs.put(PREF_RESOLUTION, (String) resolutionBox.getSelectedItem());
        prefs.putBoolean(PREF_FULLSCREEN, fullscreenCheckBox.isSelected());
        prefs.putBoolean(PREF_MUSIC, musicCheckBox.isSelected());

        /*
         * Apply resolution va fullscreen ngay lap tuc len owner Frame.
         * Music chua co SoundLoader nen chi luu Preference, apply sau.
         */
        applyToOwner();

        JOptionPane.showMessageDialog(
                this,
                "Settings saved!",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE
        );
        dispose();
    }

    /*
     * =========================
     * Apply settings to owner frame
     * =========================
     */

    private void applyToOwner() {
        if (getOwner() == null) return;

        java.awt.Frame owner = (java.awt.Frame) getOwner();

        boolean fullscreen = fullscreenCheckBox.isSelected();

        if (fullscreen) {
            /*
             * Extended state MAXIMIZED_BOTH thay vi GraphicsDevice.setFullScreenWindow()
             * vi GraphicsDevice fullscreen exclusive se che het taskbar va co the
             * gay van de khi alt-tab. MAXIMIZED_BOTH an toan hon tren moi platform.
             */
            owner.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        } else {
            owner.setExtendedState(java.awt.Frame.NORMAL);

            String resolution = (String) resolutionBox.getSelectedItem();
            if (resolution != null) {
                String[] parts = resolution.split("x");
                if (parts.length == 2) {
                    try {
                        int w = Integer.parseInt(parts[0].trim());
                        int h = Integer.parseInt(parts[1].trim());
                        owner.setSize(w, h);
                        owner.setLocationRelativeTo(null); // re-center
                    } catch (NumberFormatException ignored) {
                        // resolution string khong dung format, bo qua
                    }
                }
            }
        }
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JComboBox<String> getResolutionBox()  { return resolutionBox;       }
    public JCheckBox getFullscreenCheckBox()      { return fullscreenCheckBox;  }
    public JCheckBox getMusicCheckBox()           { return musicCheckBox;        }
    public JButton getSaveButton()               { return saveButton;           }
}
