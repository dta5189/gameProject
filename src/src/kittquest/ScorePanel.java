package kittquest;

import java.awt.*;

/**
 * ScorePanel.java - Score, Lives & HUD
 * Author: [Member 4's Name]
 *
 * TODO - Member 4:
 *  - Track score and lives
 *  - Draw HUD on screen
 */
public class ScorePanel {

    private int score = 0;
    private int lives = 3;

    public ScorePanel() {}

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Lives: " + lives, 700, 30);
    }

    public void addScore(int points) {
        score += points;
    }

    public void loseLife() {
        lives--;
    }

    public int getScore() { return score; }
    public int getLives() { return lives; }

    public void reset() {
        score = 0;
        lives = 3;
    }
}