package kittquest;

import javax.swing.JFrame;

/**
 * KittyQuest - Main Entry Point
 * Author: David Adeleye (Lead)
 *
 * This class launches the game window.
 * All game logic lives in GamePanel.java
 */
public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame("Kitty Quest");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create the main game panel and add it to the window
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack(); // sizes window to fit GamePanel's preferred size
        window.setLocationRelativeTo(null); // center on screen
        window.setVisible(true);

        // Start the game loop
        gamePanel.startGameLoop();
    } //commit main
}