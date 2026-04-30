package kittquest;

import java.awt.*;
import java.util.ArrayList;

public class LevelManager {

    private ArrayList<Rectangle> platforms = new ArrayList<>();
    private Rectangle goal = new Rectangle(1550, 240, 30, 70);

    public static int cameraX = 0;
    private static final int LEVEL_WIDTH = 1600;

    private int[][] clouds = {
            {80,   60, 90,  35},
            {320,  40, 110, 40},
            {580,  70, 80,  30},
            {850,  50, 100, 38},
            {1100, 45, 90,  32},
            {1350, 65, 80,  30},
    };

    private int[][] hills = {
            {-60,  360, 280, 140},
            {200,  370, 260, 130},
            {480,  355, 220, 140},
            {720,  365, 250, 130},
            {980,  360, 240, 140},
            {1230, 370, 260, 130},
    };

    public LevelManager() {
        buildLevel();
    }

    private void buildLevel() {
        platforms.add(new Rectangle(0,    450, LEVEL_WIDTH, 50));
        platforms.add(new Rectangle(200,  370, 130, 18));
        platforms.add(new Rectangle(420,  310, 130, 18));
        platforms.add(new Rectangle(620,  350, 120, 18));
        platforms.add(new Rectangle(800,  280, 140, 18));
        platforms.add(new Rectangle(1000, 330, 130, 18));
        platforms.add(new Rectangle(1180, 270, 120, 18));
        platforms.add(new Rectangle(1380, 300, 130, 18));
        platforms.add(new Rectangle(1520, 260, 130, 18));
    }

    public void update(Player player) {
        cameraX = player.x - GamePanel.SCREEN_WIDTH / 3;
        if (cameraX < 0) cameraX = 0;
        if (cameraX > LEVEL_WIDTH - GamePanel.SCREEN_WIDTH)
            cameraX = LEVEL_WIDTH - GamePanel.SCREEN_WIDTH;
    }

    public void update() {}

    public void draw(Graphics2D g) {
        drawBackground(g);
        drawClouds(g);
        drawPlatforms(g);
        drawGoal(g);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(135, 206, 250));
        g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT - 50);

        for (int[] h : hills) {
            int screenX = h[0] - (int)(cameraX * 0.3);
            g.setColor(new Color(110, 180, 80));
            g.fillOval(screenX, h[1], h[2], h[3]);
            g.setColor(new Color(130, 200, 90));
            g.fillOval(screenX + h[2]/4, h[1] - 10, h[2]/2, h[3]/2);
        }
    }

    private void drawClouds(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 220));
        for (int[] c : clouds) {
            int screenX = c[0] - (int)(cameraX * 0.5);
            int cy = c[1], cw = c[2], ch = c[3];
            g.fillOval(screenX, cy, cw, ch);
            g.fillOval(screenX + cw/4, cy - ch/3, (int)(cw*0.6), (int)(ch*0.8));
            g.fillOval(screenX + cw/2, cy, (int)(cw*0.6), ch);
        }
    }

    private void drawPlatforms(Graphics2D g) {
        for (int i = 0; i < platforms.size(); i++) {
            Rectangle p = platforms.get(i);
            int sx = p.x - cameraX;

            if (i == 0) {
                g.setColor(new Color(139, 90, 43));
                g.fillRect(sx, p.y + 12, p.width, p.height - 12);
                g.setColor(new Color(80, 160, 50));
                g.fillRect(sx, p.y, p.width, 14);
                g.setColor(new Color(100, 200, 60));
                for (int gx = sx + 8; gx < sx + p.width; gx += 18)
                    g.fillOval(gx, p.y - 4, 10, 8);
            } else {
                g.setColor(new Color(139, 90, 43));
                g.fillRect(sx, p.y + 8, p.width, p.height - 8);
                g.setColor(new Color(80, 160, 50));
                g.fillRoundRect(sx, p.y, p.width, 12, 6, 6);
                g.setColor(new Color(100, 200, 60));
                for (int gx = sx + 6; gx < sx + p.width - 6; gx += 14)
                    g.fillOval(gx, p.y - 3, 8, 7);
                g.setColor(new Color(0, 0, 0, 40));
                g.fillRoundRect(sx + 4, p.y + p.height + 2, p.width - 4, 6, 4, 4);
            }
        }
    }

    private void drawGoal(Graphics2D g) {
        int fx = goal.x - cameraX + goal.width / 2;
        int fy = goal.y;

        g.setColor(new Color(180, 180, 180));
        g.setStroke(new BasicStroke(4));
        g.drawLine(fx, fy, fx, fy + goal.height);
        g.setStroke(new BasicStroke(1));

        int[] flagX = {fx, fx + 28, fx};
        int[] flagY = {fy, fy + 12, fy + 24};
        g.setColor(new Color(220, 50, 50));
        g.fillPolygon(flagX, flagY, 3);
        g.setColor(Color.YELLOW);
        g.fillOval(fx + 8, fy + 8, 8, 8);

        g.setColor(new Color(139, 90, 43));
        g.fillRoundRect(fx - 10, fy + goal.height - 6, 20, 10, 4, 4);
    }

    public boolean playerReachedGoal(Player player) {
        return goal.intersects(player.getBounds());
    }

    public ArrayList<Rectangle> getPlatforms() {
        return platforms;
    }

    public int getLevelWidth() {
        return LEVEL_WIDTH;
    }
}