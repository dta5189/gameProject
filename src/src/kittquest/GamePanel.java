package kittquest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int SCREEN_WIDTH  = 800;
    public static final int SCREEN_HEIGHT = 500;
    public static final int FPS           = 60;

    public enum GameState { MENU, CHARACTER_SELECT, PLAYING, LEVEL_TRANSITION, GAME_OVER, WIN }
    private GameState gameState = GameState.MENU;

    private Timer gameTimer;

    private Player            player;
    private Enemy             enemy;
    private ScorePanel        scorePanel;
    public static LevelManager level;
    public static SoundManager sound;

    public static int currentLevel    = 1;
    private static final int MAX_LEVELS = 3;
    public static int selectedCharacter = 0;

    private int transitionTimer = 0;
    private static final int TRANSITION_FRAMES = 140;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        initGame();
    }

    private void initGame() {
        LevelManager.cameraX = 0;
        player     = new Player();
        enemy      = new Enemy(currentLevel);
        scorePanel = new ScorePanel();
        level      = new LevelManager(currentLevel);
        sound      = new SoundManager();
    }

    private void loadLevel(int lvl) {
        LevelManager.cameraX = 0;
        player = new Player();
        enemy  = new Enemy(lvl);
        level  = new LevelManager(lvl);
    }

    public void startGameLoop() {
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) update();
        if (gameState == GameState.LEVEL_TRANSITION) {
            transitionTimer++;
            if (transitionTimer >= TRANSITION_FRAMES) {
                transitionTimer = 0;
                loadLevel(currentLevel);
                gameState = GameState.PLAYING;
                sound.playBackgroundMusic();
            }
        }
        repaint();
    }

    private void update() {
        player.update();
        enemy.update();
        level.update(player);

        // Check enemy / projectile collision
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

        // Check goal reached
        if (level.playerReachedGoal(player)) {
            scorePanel.addScore(500);
            sound.playWinSound();
            if (currentLevel >= MAX_LEVELS) {
                gameState = GameState.WIN;
            } else {
                currentLevel++;              // advance ONCE here
                gameState = GameState.LEVEL_TRANSITION;
                transitionTimer = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (gameState) {
            case MENU:             drawMenu(g2d);            break;
            case CHARACTER_SELECT: drawCharacterSelect(g2d); break;
            case PLAYING:          drawGame(g2d);            break;
            case LEVEL_TRANSITION: drawTransition(g2d);      break;
            case GAME_OVER:        drawGameOver(g2d);         break;
            case WIN:              drawWin(g2d);              break;
        }
    }

    private void drawGame(Graphics2D g) {
        level.draw(g);
        player.draw(g);
        enemy.draw(g);
        scorePanel.draw(g, currentLevel);
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(new Color(15, 15, 40));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(255, 255, 255, 120));
        int[][] stars = {{50,30},{150,80},{300,20},{500,60},{650,35},{750,90},{100,150},{400,130},{700,160},{200,50},{600,110}};
        for (int[] s : stars) g.fillOval(s[0], s[1], 3, 3);

        g.setFont(new Font("Arial", Font.BOLD, 56));
        drawShadowText(g, "KittyQuest", SCREEN_HEIGHT/2 - 110, new Color(255,200,0), new Color(180,120,0));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        drawShadowText(g, "Escaping Oakes", SCREEN_HEIGHT/2 - 60, new Color(255,120,120), new Color(180,60,60));

        drawMenuCat(g, 100, SCREEN_HEIGHT/2 - 130, new Color(255,165,0));
        drawMenuCat(g, 620, SCREEN_HEIGHT/2 - 130, new Color(180,180,200));

        long t = System.currentTimeMillis();
        int alpha = (int)(Math.abs(Math.sin(t/600.0))*255);
        g.setColor(new Color(255,255,255,alpha));
        g.setFont(new Font("Arial", Font.PLAIN, 22));
        drawCentered(g, "Press ENTER to Start", SCREEN_HEIGHT/2 + 20);
        g.setColor(new Color(180,180,180));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCentered(g, "Arrow Keys to Move  |  SPACE to Jump", SCREEN_HEIGHT/2 + 58);
        drawCentered(g, "Escape Professor Oakes across 3 worlds!", SCREEN_HEIGHT/2 + 88);
    }

    private void drawMenuCat(Graphics2D g, int x, int y, Color color) {
        g.setColor(color);
        g.fillOval(x, y, 40, 35);
        g.fillRoundRect(x+5, y+28, 30, 20, 8, 8);
        g.setColor(color.darker());
        g.fillPolygon(new int[]{x+5,x+12,x+2},   new int[]{y+5,y+5,y-8},  3);
        g.fillPolygon(new int[]{x+28,x+35,x+38},  new int[]{y+5,y-8,y+5}, 3);
        g.setColor(Color.WHITE);
        g.fillOval(x+8,y+10,8,8); g.fillOval(x+22,y+10,8,8);
        g.setColor(Color.BLACK);
        g.fillOval(x+11,y+12,4,4); g.fillOval(x+25,y+12,4,4);
    }

    private void drawCharacterSelect(Graphics2D g) {
        g.setColor(new Color(20,20,50));
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,36));
        drawCentered(g,"Choose Your Kitten!",70);
        g.setFont(new Font("Arial",Font.PLAIN,18));
        g.setColor(new Color(180,180,180));
        drawCentered(g,"LEFT/RIGHT to browse  |  ENTER to confirm",110);

        String[] names    = {"Blaze","Storm","Snowball"};
        String[] taglines = {"Fast & Fierce","Stealthy Shadow","Fluffy & Bold"};
        Color[]  colors   = {new Color(255,165,0),new Color(140,140,170),new Color(240,240,255)};
        int[]    positions= {130,330,530};

        for (int i=0;i<3;i++) {
            int cx=positions[i], cy=200;
            boolean sel=(i==selectedCharacter);
            g.setColor(sel?new Color(60,60,120):new Color(40,40,80));
            g.fillRoundRect(cx-10,cy-30,140,200,16,16);
            if (sel) {
                g.setColor(new Color(255,200,0));
                g.setStroke(new BasicStroke(3));
                g.drawRoundRect(cx-10,cy-30,140,200,16,16);
                g.setStroke(new BasicStroke(1));
            }
            drawSelectCat(g,cx+30,cy,colors[i]);
            g.setColor(sel?new Color(255,200,0):Color.WHITE);
            g.setFont(new Font("Arial",Font.BOLD,20));
            drawCenteredAt(g,names[i],cx+60,cy+110);
            g.setColor(new Color(180,180,200));
            g.setFont(new Font("Arial",Font.PLAIN,14));
            drawCenteredAt(g,taglines[i],cx+60,cy+132);
            if (sel) {
                g.setColor(new Color(255,200,0));
                g.setFont(new Font("Arial",Font.BOLD,13));
                drawCenteredAt(g,"SELECTED",cx+60,cy+155);
            }
        }
        long t=System.currentTimeMillis();
        int alpha=(int)(Math.abs(Math.sin(t/600.0))*255);
        g.setColor(new Color(255,255,255,alpha));
        g.setFont(new Font("Arial",Font.BOLD,22));
        drawCentered(g,"Press ENTER to Play!",440);
    }

    private void drawSelectCat(Graphics2D g,int x,int y,Color color) {
        g.setColor(color); g.fillRoundRect(x,y+20,50,40,12,12); g.fillOval(x+5,y,42,38);
        g.setColor(color.darker());
        g.fillPolygon(new int[]{x+7,x+15,x+3},  new int[]{y+6,y+6,y-10},3);
        g.fillPolygon(new int[]{x+35,x+43,x+47},new int[]{y+6,y-10,y+6},3);
        g.setColor(new Color(255,180,180));
        g.fillPolygon(new int[]{x+8,x+14,x+5},  new int[]{y+5,y+5,y-5},3);
        g.fillPolygon(new int[]{x+36,x+42,x+46},new int[]{y+5,y-5,y+5},3);
        g.setColor(Color.WHITE); g.fillOval(x+10,y+12,12,12); g.fillOval(x+29,y+12,12,12);
        g.setColor(new Color(30,100,30)); g.fillOval(x+13,y+15,7,7); g.fillOval(x+32,y+15,7,7);
        g.setColor(Color.WHITE); g.fillOval(x+17,y+15,3,3); g.fillOval(x+36,y+15,3,3);
        g.setColor(new Color(255,100,150));
        g.fillPolygon(new int[]{x+25,x+22,x+28},new int[]{y+27,y+24,y+24},3);
        g.setColor(new Color(100,100,100)); g.setStroke(new BasicStroke(1.2f));
        g.drawLine(x+5,y+25,x+20,y+27); g.drawLine(x+5,y+29,x+20,y+29);
        g.drawLine(x+31,y+27,x+47,y+25); g.drawLine(x+31,y+29,x+47,y+29);
        g.setStroke(new BasicStroke(1));
        g.setColor(color); g.fillOval(x+5,y+55,16,10); g.fillOval(x+29,y+55,16,10);
        g.setColor(new Color(255,180,180)); g.fillOval(x+8,y+58,5,5); g.fillOval(x+32,y+58,5,5);
    }

    private void drawTransition(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);

        // Level complete text
        g.setColor(new Color(255,200,0));
        g.setFont(new Font("Arial",Font.BOLD,48));
        drawCentered(g,"Level "+(currentLevel-1)+" Complete!",SCREEN_HEIGHT/2-60);

        // Next level preview
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,26));
        String nextText = "";
        String subText  = "";
        if (currentLevel == 2) { nextText="Next: City Rooftops"; subText="Watch out — bigger gaps, faster enemies!"; }
        if (currentLevel == 3) { nextText="Next: The Dark Dungeon"; subText="WARNING: Professor Oakes awaits..."; }
        drawCentered(g,nextText,SCREEN_HEIGHT/2);

        if (currentLevel == 3) g.setColor(new Color(255,100,100));
        else g.setColor(new Color(200,200,200));
        g.setFont(new Font("Arial",Font.PLAIN,20));
        drawCentered(g,subText,SCREEN_HEIGHT/2+38);

        // Progress bar
        int barW=400,barX=(SCREEN_WIDTH-barW)/2,barY=SCREEN_HEIGHT/2+90;
        g.setColor(new Color(50,50,50));
        g.fillRoundRect(barX,barY,barW,20,10,10);
        int fill=(int)((transitionTimer/(float)TRANSITION_FRAMES)*barW);
        g.setColor(currentLevel==3?new Color(200,50,50):new Color(255,200,0));
        g.fillRoundRect(barX,barY,fill,20,10,10);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(35,0,0));
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD,56));
        drawCentered(g,"GAME OVER",SCREEN_HEIGHT/2-60);
        g.setColor(new Color(255,180,180));
        g.setFont(new Font("Arial",Font.PLAIN,22));
        drawCentered(g,"Professor Oakes caught you!",SCREEN_HEIGHT/2-10);
        g.setColor(Color.WHITE);
        drawCentered(g,"Score: "+scorePanel.getScore(),SCREEN_HEIGHT/2+28);
        g.setColor(new Color(200,200,200));
        g.setFont(new Font("Arial",Font.PLAIN,20));
        drawCentered(g,"Press ENTER to Try Again",SCREEN_HEIGHT/2+72);
    }

    private void drawWin(Graphics2D g) {
        g.setColor(new Color(0,25,0));
        g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial",Font.BOLD,52));
        drawCentered(g,"YOU ESCAPED!",SCREEN_HEIGHT/2-90);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.PLAIN,22));
        drawCentered(g,"Professor Oakes has been defeated!",SCREEN_HEIGHT/2-35);
        drawCentered(g,"KittyQuest Complete!",SCREEN_HEIGHT/2+5);
        g.setColor(new Color(255,200,0));
        g.setFont(new Font("Arial",Font.BOLD,30));
        drawCentered(g,"Final Score: "+scorePanel.getScore(),SCREEN_HEIGHT/2+55);
        g.setColor(new Color(200,200,200));
        g.setFont(new Font("Arial",Font.PLAIN,20));
        drawCentered(g,"Press ENTER to Play Again",SCREEN_HEIGHT/2+105);
    }

    private void drawShadowText(Graphics2D g,String text,int y,Color main,Color shadow) {
        FontMetrics fm=g.getFontMetrics();
        int x=(SCREEN_WIDTH-fm.stringWidth(text))/2;
        g.setColor(shadow); g.drawString(text,x+3,y+3);
        g.setColor(main);   g.drawString(text,x,y);
    }

    private void drawCentered(Graphics2D g,String text,int y) {
        FontMetrics fm=g.getFontMetrics();
        g.drawString(text,(SCREEN_WIDTH-fm.stringWidth(text))/2,y);
    }

    private void drawCenteredAt(Graphics2D g,String text,int cx,int y) {
        FontMetrics fm=g.getFontMetrics();
        g.drawString(text,cx-fm.stringWidth(text)/2,y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key=e.getKeyCode();
        if (gameState==GameState.MENU && key==KeyEvent.VK_ENTER) {
            gameState=GameState.CHARACTER_SELECT;
        }
        else if (gameState==GameState.CHARACTER_SELECT) {
            if (key==KeyEvent.VK_LEFT)  selectedCharacter=Math.max(0,selectedCharacter-1);
            if (key==KeyEvent.VK_RIGHT) selectedCharacter=Math.min(2,selectedCharacter+1);
            if (key==KeyEvent.VK_ENTER) {
                currentLevel=1;
                initGame();
                gameState=GameState.PLAYING;
                sound.playBackgroundMusic();
            }
        }
        else if (key==KeyEvent.VK_ENTER &&
                (gameState==GameState.GAME_OVER||gameState==GameState.WIN)) {
            currentLevel=1;
            gameState=GameState.CHARACTER_SELECT;
        }
        if (gameState==GameState.PLAYING) player.keyPressed(key);
    }

    @Override public void keyReleased(KeyEvent e) { if (gameState==GameState.PLAYING) player.keyReleased(e.getKeyCode()); }
    @Override public void keyTyped(KeyEvent e) {}
}