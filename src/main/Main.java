package main;

import ui.window.MainWindowController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(MainWindowController::new);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }
}