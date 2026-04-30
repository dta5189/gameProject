package kittquest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int SCREEN_WIDTH  = 800;
    public static final int SCREEN_HEIGHT = 500;
    public static final int FPS           = 60;

    public enum GameState { MENU, PLAYING, GAME_OVER, WIN }
    private GameState gameState = GameState.MENU;

    private Timer gameTimer;

    private Player       player;
    private Enemy        enemy;
    private ScorePanel   scorePanel;
    public static LevelManager level;
    private SoundManager sound;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        initGame();
    }

    private void initGame() {
        player     = new Player();
        enemy      = new Enemy();
        scorePanel = new ScorePanel();
        level      = new LevelManager();
        sound      = new SoundManager();
    }

    public void startGameLoop() {
        int delay = 1000 / FPS;
        gameTimer = new Timer(delay, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) update();
        repaint();
    }

    private void update() {
        player.update();
        enemy.update();
        level.update(player);  // pass player so camera follows

        if (enemy.isCatchingPlayer(player)) {
            scorePanel.loseLife();
            sound.playHurtSound();
            if (scorePanel.getLives() <= 0) {
                gameState = GameState.GAME_OVER;
                sound.playGameOverSound();
            } else {
                player.respawn();
            }
        }

        if (level.playerReachedGoal(player)) {
            gameState = GameState.WIN;
            sound.playWinSound();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (gameState) {
            case MENU:      drawMenu(g2d);     break;
            case PLAYING:   drawGame(g2d);     break;
            case GAME_OVER: drawGameOver(g2d); break;
            case WIN:       drawWin(g2d);      break;
        }
    }

    private void drawGame(Graphics2D g) {
        level.draw(g);
        player.draw(g);
        enemy.draw(g);
        scorePanel.draw(g);
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(new Color(30, 30, 60));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        drawCentered(g, "Kitty Quest", SCREEN_HEIGHT / 2 - 60);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCentered(g, "Press ENTER to Start", SCREEN_HEIGHT / 2 + 10);
        drawCentered(g, "Arrow Keys to Move  |  SPACE to Jump", SCREEN_HEIGHT / 2 + 50);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(60, 0, 0));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        drawCentered(g, "GAME OVER", SCREEN_HEIGHT / 2 - 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCentered(g, "Score: " + scorePanel.getScore(), SCREEN_HEIGHT / 2 + 10);
        drawCentered(g, "Press ENTER to Restart", SCREEN_HEIGHT / 2 + 50);
    }

    private void drawWin(Graphics2D g) {
        g.setColor(new Color(0, 50, 0));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        drawCentered(g, "YOU WIN!", SCREEN_HEIGHT / 2 - 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCentered(g, "Final Score: " + scorePanel.getScore(), SCREEN_HEIGHT / 2 + 10);
        drawCentered(g, "Press ENTER to Play Again", SCREEN_HEIGHT / 2 + 50);
    }

    private void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (SCREEN_WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            if (gameState == GameState.MENU ||
                    gameState == GameState.GAME_OVER ||
                    gameState == GameState.WIN) {
                initGame();
                gameState = GameState.PLAYING;
                sound.playBackgroundMusic();
            }
        }
        if (gameState == GameState.PLAYING) player.keyPressed(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState == GameState.PLAYING) player.keyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}