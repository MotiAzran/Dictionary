/*
 * Moti Azran
 */

import javax.swing.*;

/**
 * Main program class
 */
public class Main {
    /**
     * Program entry point, creates main program window
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DictionaryWindow window = new DictionaryWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
