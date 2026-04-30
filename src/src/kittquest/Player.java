package kittquest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player {

    public int x, y;
    public int width  = 40;
    public int height = 40;

    private float velocityX = 0;
    private float velocityY = 0;
    private final float SPEED   = 4.5f;
    private final float JUMP    = -13f;
    private final float GRAVITY = 0.5f;

    private boolean onGround    = false;
    private boolean movingLeft  = false;
    private boolean movingRight = false;
    private boolean facingRight = true;

    private final int SPAWN_X = 80;
    private final int SPAWN_Y = 380;

    public Player() {
        x = SPAWN_X;
        y = SPAWN_Y;
    }

    public void update() {
        if (movingLeft)       velocityX = -SPEED;
        else if (movingRight) velocityX =  SPEED;
        else                  velocityX =  0;

        velocityY += GRAVITY;
        x += (int) velocityX;
        y += (int) velocityY;

        onGround = false;
        ArrayList<Rectangle> platforms = GamePanel.level != null
                ? GamePanel.level.getPlatforms()
                : new ArrayList<>();

        for (Rectangle platform : platforms) {
            if (getBounds().intersects(platform)) {
                if (velocityY > 0 && y + height - (int)velocityY <= platform.y) {
                    y = platform.y - height;
                    velocityY = 0;
                    onGround = true;
                } else if (velocityY < 0 && y - (int)velocityY >= platform.y + platform.height) {
                    y = platform.y + platform.height;
                    velocityY = 0;
                } else {
                    if (velocityX > 0) x = platform.x - width;
                    else if (velocityX < 0) x = platform.x + platform.width;
                    velocityX = 0;
                }
            }
        }

        if (x < 0) x = 0;
        int levelWidth = GamePanel.level != null ? GamePanel.level.getLevelWidth() : GamePanel.SCREEN_WIDTH;
        if (x + width > levelWidth) x = levelWidth - width;

        if (y > GamePanel.SCREEN_HEIGHT) respawn();
    }

    public void draw(Graphics2D g) {
        int sx = x - LevelManager.cameraX;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = facingRight ? 0 : 4;

        // TAIL
        g.setColor(new Color(255, 140, 0));
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (facingRight) g.drawArc(sx - 14, y + 18, 22, 22, 180, -180);
        else             g.drawArc(sx + width - 8, y + 18, 22, 22, 0, 180);
        g.setStroke(new BasicStroke(1));

        // BODY
        g.setColor(new Color(255, 165, 0));
        g.fillRoundRect(sx + dx, y + 14, 36, 26, 12, 12);
        g.setColor(new Color(220, 130, 0));
        g.fillRoundRect(sx + dx + 8,  y + 18, 5, 18, 4, 4);
        g.fillRoundRect(sx + dx + 18, y + 18, 5, 18, 4, 4);

        // JETPACK
        if (!onGround) {
            int jpx = facingRight ? sx - 6 : sx + width - 6;
            g.setColor(new Color(70, 70, 180));
            g.fillRoundRect(jpx, y + 16, 12, 18, 4, 4);
            g.setColor(new Color(150, 150, 220));
            g.fillRoundRect(jpx + 2, y + 18, 8, 6, 2, 2);
            g.setColor(new Color(255, 80, 0));
            g.fillOval(jpx + 1, y + 33, 10, 12);
            g.setColor(Color.YELLOW);
            g.fillOval(jpx + 3, y + 36, 6, 8);
        }

        // HEAD
        g.setColor(new Color(255, 165, 0));
        g.fillOval(sx + dx + 2, y - 2, 32, 30);

        // EARS
        g.setColor(new Color(255, 130, 0));
        g.fillPolygon(new int[]{sx+dx+4, sx+dx+10, sx+dx+1},  new int[]{y+2, y+2, y-14}, 3);
        g.fillPolygon(new int[]{sx+dx+24,sx+dx+30, sx+dx+35}, new int[]{y+2, y-14,y+2},  3);
        g.setColor(new Color(255, 180, 180));
        g.fillPolygon(new int[]{sx+dx+5, sx+dx+9,  sx+dx+3},  new int[]{y+1, y+1, y-9},  3);
        g.fillPolygon(new int[]{sx+dx+25,sx+dx+29, sx+dx+33}, new int[]{y+1, y-9, y+1},  3);

        // EYES
        g.setColor(Color.WHITE);
        g.fillOval(sx+dx+6,  y+6, 10, 10);
        g.fillOval(sx+dx+20, y+6, 10, 10);
        g.setColor(new Color(40, 20, 0));
        if (facingRight) { g.fillOval(sx+dx+9, y+8, 6, 6); g.fillOval(sx+dx+23, y+8, 6, 6); }
        else             { g.fillOval(sx+dx+7, y+8, 6, 6); g.fillOval(sx+dx+21, y+8, 6, 6); }
        g.setColor(Color.WHITE);
        g.fillOval(sx+dx+12, y+8, 3, 3);
        g.fillOval(sx+dx+26, y+8, 3, 3);

        // NOSE
        g.setColor(new Color(255, 100, 150));
        g.fillPolygon(new int[]{sx+dx+15, sx+dx+12, sx+dx+18}, new int[]{y+19, y+16, y+16}, 3);

        // WHISKERS
        g.setColor(new Color(80, 80, 80));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(sx+dx+4,  y+17, sx+dx+12, y+19);
        g.drawLine(sx+dx+2,  y+20, sx+dx+12, y+20);
        g.drawLine(sx+dx+18, y+19, sx+dx+32, y+17);
        g.drawLine(sx+dx+18, y+20, sx+dx+34, y+20);
        g.setStroke(new BasicStroke(1));

        // MOUTH
        g.setColor(new Color(180, 80, 100));
        g.drawArc(sx+dx+11, y+19, 7, 5, 180, 180);
        g.drawArc(sx+dx+18, y+19, 7, 5, 180, 180);

        // PAWS
        g.setColor(new Color(255, 145, 0));
        g.fillOval(sx+dx+4,  y+37, 12, 8);
        g.fillOval(sx+dx+20, y+37, 12, 8);
        g.setColor(new Color(255, 180, 180));
        g.fillOval(sx+dx+6,  y+39, 4, 4);
        g.fillOval(sx+dx+22, y+39, 4, 4);
    }

    public void keyPressed(int key) {
        if (key == KeyEvent.VK_LEFT)  { movingLeft = true;  facingRight = false; }
        if (key == KeyEvent.VK_RIGHT) { movingRight = true; facingRight = true;  }
        if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) && onGround) {
            velocityY = JUMP;
            onGround  = false;
        }
    }

    public void keyReleased(int key) {
        if (key == KeyEvent.VK_LEFT)  movingLeft  = false;
        if (key == KeyEvent.VK_RIGHT) movingRight = false;
    }

    public void respawn() {
        x = SPAWN_X; y = SPAWN_Y;
        velocityX = 0; velocityY = 0;
        movingLeft = false; movingRight = false;
        onGround = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}