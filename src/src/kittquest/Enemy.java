
package kittquest;

import java.awt.*;
import java.util.ArrayList;

public class Enemy {

    static class EnemyUnit {
        float x, y;
        int width = 38, height = 42;
        float speed;
        int leftBound, rightBound;
        float velocityX;

        EnemyUnit(int startX, int startY, int left, int right, float spd) {
            x = startX;
            y = startY;
            leftBound = left;
            rightBound = right;
            speed = spd;
            velocityX = spd;
        }

        void update() {
            x += velocityX;
            if (x <= leftBound || x + width >= rightBound) {
                velocityX = -velocityX;
            }
        }

        void draw(Graphics2D g, int cameraX) {
            int sx = (int)x - cameraX;
            if (sx + width < 0 || sx > GamePanel.SCREEN_WIDTH) return;

            boolean facingRight = velocityX > 0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // BODY
            g.setColor(new Color(60, 60, 120));
            g.fillRoundRect(sx + 4, (int)y + 16, 30, 26, 8, 8);
            g.setColor(new Color(80, 80, 160));
            g.fillRect(sx + 14, (int)y + 18, 10, 22);

            // HEAD
            g.setColor(new Color(255, 220, 180));
            g.fillOval(sx + 5, (int)y, 28, 26);

            // CAP
            g.setColor(Color.BLACK);
            g.fillRect(sx + 3, (int)y + 4, 32, 5);
            g.fillRect(sx + 10, (int)y - 8, 18, 13);
            g.setColor(new Color(255, 200, 0));
            g.drawLine(sx + 28, (int)y - 2, sx + 34, (int)y + 8);
            g.fillOval(sx + 32, (int)y + 6, 5, 5);

            // EYES
            g.setColor(Color.WHITE);
            g.fillOval(sx+9,  (int)y+8, 9, 8);
            g.fillOval(sx+20, (int)y+8, 9, 8);
            g.setColor(new Color(30, 30, 30));
            g.fillOval(facingRight ? sx+13 : sx+10, (int)y+10, 5, 5);
            g.fillOval(facingRight ? sx+24 : sx+21, (int)y+10, 5, 5);

            // GLASSES
            g.setColor(new Color(80, 50, 20));
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(sx+8,  (int)y+7, 11, 10);
            g.drawOval(sx+19, (int)y+7, 11, 10);
            g.setStroke(new BasicStroke(1));

            // MUSTACHE
            g.setColor(new Color(80, 50, 20));
            g.fillRoundRect(sx+10, (int)y+18, 18, 5, 4, 4);

            // ARMS
            g.setColor(new Color(60, 60, 120));
            if (facingRight) g.fillRoundRect(sx+30, (int)y+18, 10, 6, 4, 4);
            else             g.fillRoundRect(sx-2,  (int)y+18, 10, 6, 4, 4);

            // LEGS
            g.setColor(new Color(40, 40, 80));
            g.fillRoundRect(sx+7,  (int)y+38, 10, 8, 4, 4);
            g.fillRoundRect(sx+21, (int)y+38, 10, 8, 4, 4);

            // EYEBROWS
            g.setColor(new Color(80, 50, 20));
            g.setStroke(new BasicStroke(2.5f));
            g.drawLine(sx+8,  (int)y+7, sx+16, (int)y+9);
            g.drawLine(sx+22, (int)y+9, sx+30, (int)y+7);
            g.setStroke(new BasicStroke(1));
        }

        Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, width, height);
        }
    }

    private ArrayList<EnemyUnit> enemies = new ArrayList<>();

    public Enemy() {
        spawnEnemies();
    }

    private void spawnEnemies() {
        enemies.add(new EnemyUnit(300,  408, 220,  420,  2.0f));
        enemies.add(new EnemyUnit(680,  408, 600,  800,  2.5f));
        enemies.add(new EnemyUnit(800,  238, 800,  940,  1.8f));
        enemies.add(new EnemyUnit(1050, 408, 950,  1180, 3.0f));
        enemies.add(new EnemyUnit(1400, 408, 1320, 1560, 2.2f));
    }

    public void update() {
        for (EnemyUnit e : enemies) e.update();
    }

    public void draw(Graphics2D g) {
        for (EnemyUnit e : enemies) e.draw(g, LevelManager.cameraX);
    }

    public boolean isCatchingPlayer(Player player) {
        Rectangle playerBounds = player.getBounds();
        for (EnemyUnit e : enemies) {
            if (e.getBounds().intersects(playerBounds)) return true;
        }
        return false;
    }

    public Rectangle getBounds() {
        return enemies.isEmpty() ? new Rectangle() : enemies.get(0).getBounds();
    }
}