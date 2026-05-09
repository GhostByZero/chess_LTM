package ui.panel;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    private final JTextArea chatArea;

    private final JTextField messageField;

    private final JButton sendButton;

    public ChatPanel() {

        setLayout(new BorderLayout());

        setPreferredSize(
                new Dimension(300, 0)
        );

        /*
         * =========================
         * Chat Area
         * =========================
         */

        chatArea =
                new JTextArea();

        chatArea.setEditable(false);

        chatArea.setLineWrap(true);

        JScrollPane scrollPane =
                new JScrollPane(chatArea);

        /*
         * =========================
         * Input Area
         * =========================
         */

        JPanel inputPanel =
                new JPanel(
                        new BorderLayout()
                );

        messageField =
                new JTextField();

        sendButton =
                new JButton("Send");

        inputPanel.add(
                messageField,
                BorderLayout.CENTER
        );

        inputPanel.add(
                sendButton,
                BorderLayout.EAST
        );

        /*
         * =========================
         * Add Components
         * =========================
         */

        add(scrollPane, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.SOUTH);
    }

    /*
     * =========================
     * Chat Methods
     * =========================
     */

    public void appendMessage(
            String message
    ) {

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