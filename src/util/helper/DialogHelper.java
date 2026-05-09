package util.helper;

import javax.swing.*;

public class DialogHelper {

    /*
     * =========================
     * Information Dialog
     * =========================
     */

    public static void showInfo(
            String message
    ) {

        JOptionPane.showMessageDialog(
                null,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /*
     * =========================
     * Error Dialog
     * =========================
     */

    public static void showError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /*
     * =========================
     * Confirm Dialog
     * =========================
     */

    public static boolean confirm(
            String message
    ) {

        int result =
                JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

        return result == JOptionPane.YES_OPTION;
    }

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private DialogHelper() {

    }
}