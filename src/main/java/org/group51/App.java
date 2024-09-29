package org.group51;

import javax.swing.*;

/**
 * Starting point for DevSlide
 * Threads the welcome screen using invoke later
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }
}
