package ui.panel;

import ui.theme.ColorTheme;
import ui.theme.FontManager;
import ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    private final JTextArea  chatArea;

    private final JTextField messageField;

    private final JButton    sendButton;

    public ChatPanel() {

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(300, 0));

        ThemeManager.applyPanelTheme(this);

        /*
         * =========================
         * Chat Area
         * =========================
         */

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(ColorTheme.WINDOW_BACKGROUND);
        chatArea.setForeground(ColorTheme.PRIMARY_TEXT);
        chatArea.setFont(FontManager.SMALL_FONT);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.getViewport().setBackground(ColorTheme.WINDOW_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorTheme.BUTTON_BACKGROUND));

        /*
         * =========================
         * Input Area
         * =========================
         */

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(ColorTheme.PANEL_BACKGROUND);

        messageField = new JTextField();
        messageField.setBackground(ColorTheme.WINDOW_BACKGROUND);
        messageField.setForeground(ColorTheme.PRIMARY_TEXT);
        messageField.setCaretColor(ColorTheme.PRIMARY_TEXT);
        messageField.setFont(FontManager.SMALL_FONT);

        sendButton = new JButton("Send");
        ThemeManager.applyButtonTheme(sendButton);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton,   BorderLayout.EAST);

        /*
         * =========================
         * Add Components
         * =========================
         */

        add(scrollPane,  BorderLayout.CENTER);
        add(inputPanel,  BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Chat Methods
     * =========================
     */

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public JTextField getMessageField() {
        return messageField;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}