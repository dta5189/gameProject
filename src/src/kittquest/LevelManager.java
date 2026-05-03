package kittquest;

import java.awt.*;
import java.util.ArrayList;

public class LevelManager {

    private ArrayList<Rectangle> platforms = new ArrayList<>();
    private Rectangle goal;
    public static int cameraX = 0;
    private int levelWidth;
    private int levelNum;
    private int[][] clouds, hills;

    public LevelManager(int level) {
        this.levelNum = level;
        buildLevel(level);
    }

    private void buildLevel(int level) {
        platforms.clear();
        if (level == 1) {
            // ── LEVEL 1: Grassy Meadow ──────────────────────────────────────
            levelWidth = 1800;
            goal = new Rectangle(1755, 278, 30, 70);
            platforms.add(new Rectangle(0,    450, levelWidth, 50)); // ground
            platforms.add(new Rectangle(220,  380, 140, 18));
            platforms.add(new Rectangle(440,  340, 130, 18));
            platforms.add(new Rectangle(650,  370, 120, 18));
            platforms.add(new Rectangle(840,  310, 140, 18));
            platforms.add(new Rectangle(1060, 350, 130, 18));
            platforms.add(new Rectangle(1260, 300, 120, 18));
            platforms.add(new Rectangle(1460, 330, 140, 18));
            platforms.add(new Rectangle(1680, 310, 130, 18));
            clouds = new int[][]{{80,60,90,35},{300,40,110,40},{560,65,85,32},{820,48,100,38},{1100,42,90,32},{1380,60,85,30},{1620,50,95,35}};
            hills  = new int[][]{{-60,370,260,130},{220,375,240,125},{500,368,220,132},{760,372,250,128},{1040,370,230,130},{1320,374,240,126},{1580,370,240,130}};

        } else if (level == 2) {
            // ── LEVEL 2: City Rooftops (easier gaps) ────────────────────────────
            levelWidth = 2000;
            goal = new Rectangle(1920, 248, 30, 70);
            // Start rooftop — bigger so you have room
            platforms.add(new Rectangle(0,    420, 350, 30));  // start rooftop
            platforms.add(new Rectangle(420,  400, 180, 20));  // smaller gap
            platforms.add(new Rectangle(670,  375, 160, 20));
            platforms.add(new Rectangle(900,  350, 160, 20));
            platforms.add(new Rectangle(1130, 320, 160, 20));
            platforms.add(new Rectangle(1360, 350, 160, 20));
            platforms.add(new Rectangle(1580, 300, 160, 20));
            platforms.add(new Rectangle(1790, 320, 160, 20));
            platforms.add(new Rectangle(1930, 300, 160, 20));  // final rooftop
            // Lower safety platforms in the gaps
            platforms.add(new Rectangle(370,  450, 80, 20));
            platforms.add(new Rectangle(620,  440, 80, 20));
            platforms.add(new Rectangle(850,  430, 80, 20));
            platforms.add(new Rectangle(1080, 420, 80, 20));
            clouds = new int[][]{{100,50,80,28},{320,30,100,35},{600,55,80,28},{880,38,95,32},{1160,45,85,28},{1440,35,90,30},{1720,48,80,28}};
            hills  = null;

        } else {
            // ── LEVEL 3: Dark Dungeon / Boss Arena ──────────────────────────
            levelWidth = 2400;
            goal = new Rectangle(2320, 288, 30, 70);
            // Separate ground chunks with lava gaps
            platforms.add(new Rectangle(0,    450, 350, 50));  // start chunk
            platforms.add(new Rectangle(420,  450, 200, 50));  // chunk 2
            platforms.add(new Rectangle(700,  450, 200, 50));  // chunk 3
            platforms.add(new Rectangle(980,  450, 300, 50));  // chunk 4
            platforms.add(new Rectangle(1360, 450, 200, 50));  // chunk 5
            platforms.add(new Rectangle(1640, 450, 200, 50));  // chunk 6
            platforms.add(new Rectangle(1920, 450, 480, 50));  // boss arena
            // Floating platforms
            platforms.add(new Rectangle(160,  340, 110, 18));
            platforms.add(new Rectangle(360,  300, 100, 18));
            platforms.add(new Rectangle(550,  340, 110, 18));
            platforms.add(new Rectangle(740,  290, 100, 18));
            platforms.add(new Rectangle(930,  330, 110, 18));
            platforms.add(new Rectangle(1130, 270, 100, 18));
            platforms.add(new Rectangle(1310, 310, 110, 18));
            platforms.add(new Rectangle(1510, 260, 100, 18));
            platforms.add(new Rectangle(1700, 300, 110, 18));
            platforms.add(new Rectangle(1880, 250, 100, 18));
            clouds = new int[][]{{80,45,80,25},{320,30,75,22},{600,48,80,25},{900,35,75,22},{1200,42,80,25},{1500,32,75,22},{1800,45,80,25},{2100,35,75,22}};
            hills  = null;
        }
    }

    public void update(Player player) {
        cameraX = player.x - GamePanel.SCREEN_WIDTH / 3;
        if (cameraX < 0) cameraX = 0;
        if (cameraX > levelWidth - GamePanel.SCREEN_WIDTH)
            cameraX = levelWidth - GamePanel.SCREEN_WIDTH;
    }

    public void update() {}

    public void draw(Graphics2D g) {
        drawBackground(g);
        drawClouds(g);
        if (levelNum == 2) drawCityDetails(g);
        if (levelNum == 3) drawLava(g);
        drawPlatforms(g);
        drawGoal(g);
    }

    // ── LEVEL 1: Grassy Meadow Background ────────────────────────────────────
    private void drawBackground(Graphics2D g) {
        if (levelNum == 1) {
            g.setColor(new Color(135, 206, 250));
            g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
            if (hills != null) {
                for (int[] h : hills) {
                    int hx = h[0] - (int)(cameraX * 0.3);
                    g.setColor(new Color(110, 180, 80));
                    g.fillOval(hx, h[1], h[2], h[3]);
                    g.setColor(new Color(140, 210, 100));
                    g.fillOval(hx + h[2]/4, h[1] - 12, h[2]/2, h[3]/2);
                }
            }
        } else if (levelNum == 2) {
            // Dusk city sky gradient
            g.setColor(new Color(255, 140, 80));
            g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT / 2);
            g.setColor(new Color(180, 80, 120));
            g.fillRect(0, GamePanel.SCREEN_HEIGHT / 2, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT / 2);
            // City building silhouettes (parallax)
            drawCitySkyline(g);
        } else {
            // Dark cave
            g.setColor(new Color(12, 8, 28));
            g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
            // Stalactites
            g.setColor(new Color(40, 25, 60));
            for (int i = 0; i < 12; i++) {
                int sx = (i * 180 + 60) - (int)(cameraX * 0.2) % GamePanel.SCREEN_WIDTH;
                int sh = 30 + (i % 3) * 20;
                g.fillPolygon(new int[]{sx, sx + 20, sx + 10}, new int[]{0, 0, sh}, 3);
            }
            // Lava glow at bottom
            g.setColor(new Color(180, 50, 0, 60));
            g.fillRect(0, GamePanel.SCREEN_HEIGHT - 80, GamePanel.SCREEN_WIDTH, 80);
        }
    }

    private void drawCitySkyline(Graphics2D g) {
        // Background buildings (slow parallax)
        int[][] buildings = {{0,200,60,250},{80,160,80,290},{180,180,70,270},{280,140,90,310},{400,170,75,280},{510,150,85,300},{620,190,65,260},{720,145,90,305},{840,175,70,275},{960,155,80,295},{1080,185,65,265},{1180,148,88,302},{1300,172,72,278},{1420,158,82,292}};
        for (int[] b : buildings) {
            int bx = b[0] - (int)(cameraX * 0.25);
            g.setColor(new Color(40, 30, 60));
            g.fillRect(bx, b[1], b[2], b[3]);
            // Windows
            g.setColor(new Color(255, 220, 100, 180));
            for (int wy = b[1] + 10; wy < b[1] + b[3] - 10; wy += 20) {
                for (int wx = bx + 8; wx < bx + b[2] - 8; wx += 18) {
                    if ((wx + wy) % 40 != 0) g.fillRect(wx, wy, 8, 10);
                }
            }
        }
    }

    private void drawCityDetails(Graphics2D g) {
        // Water tower and AC units on rooftops - drawn in platform loop instead
    }

    private void drawClouds(Graphics2D g) {
        if (levelNum == 3) {
            // Eerie smoke puffs
            g.setColor(new Color(40, 15, 50, 140));
        } else if (levelNum == 2) {
            g.setColor(new Color(255, 180, 140, 160));
        } else {
            g.setColor(new Color(255, 255, 255, 220));
        }
        if (clouds == null) return;
        for (int[] c : clouds) {
            int cx = c[0] - (int)(cameraX * 0.5);
            int cy = c[1], cw = c[2], ch = c[3];
            g.fillOval(cx, cy, cw, ch);
            g.fillOval(cx + cw/4, cy - ch/3, (int)(cw*0.6), (int)(ch*0.8));
            g.fillOval(cx + cw/2, cy, (int)(cw*0.6), ch);
        }
    }

    private void drawLava(Graphics2D g) {
        // Lava in the gaps between ground chunks
        int[][] lavaGaps = {{350,450,70,50},{620,450,80,50},{900,450,80,50},{1280,450,80,50},{1560,450,80,50}};
        long t = System.currentTimeMillis();
        for (int[] gap : lavaGaps) {
            int gx = gap[0] - cameraX;
            if (gx + gap[2] < 0 || gx > GamePanel.SCREEN_WIDTH) continue;
            g.setColor(new Color(180, 40, 0));
            g.fillRect(gx, gap[1], gap[2], gap[3]);
            // Animated glow
            int alpha = (int)(Math.abs(Math.sin(t / 350.0)) * 140) + 80;
            g.setColor(new Color(255, 90, 0, alpha));
            g.fillRect(gx, gap[1], gap[2], 14);
            // Bubbles
            g.setColor(new Color(255, 140, 0));
            int bpos = (int)((t / 180) % gap[2]);
            g.fillOval(gx + bpos, gap[1] - 6, 10, 10);
        }
    }

    private void drawPlatforms(Graphics2D g) {
        for (int i = 0; i < platforms.size(); i++) {
            Rectangle p = platforms.get(i);
            int sx = p.x - cameraX;
            if (sx + p.width < 0 || sx > GamePanel.SCREEN_WIDTH) continue;

            if (levelNum == 1) drawGrassyPlatform(g, sx, p, i == 0);
            else if (levelNum == 2) drawCityPlatform(g, sx, p, i == 0);
            else drawDungeonPlatform(g, sx, p, p.width > 100);
        }
    }

    private void drawGrassyPlatform(Graphics2D g, int sx, Rectangle p, boolean isGround) {
        g.setColor(new Color(139, 90, 43));
        g.fillRect(sx, p.y + (isGround ? 12 : 8), p.width, p.height - (isGround ? 12 : 8));
        g.setColor(new Color(80, 160, 50));
        if (isGround) g.fillRect(sx, p.y, p.width, 14);
        else g.fillRoundRect(sx, p.y, p.width, 12, 6, 6);
        g.setColor(new Color(110, 200, 65));
        for (int gx = sx + 6; gx < sx + p.width - 6; gx += 14)
            g.fillOval(gx, p.y - 3, 8, 7);
        if (!isGround) {
            g.setColor(new Color(0, 0, 0, 40));
            g.fillRoundRect(sx + 4, p.y + p.height + 2, p.width - 4, 6, 4, 4);
        }
    }

    private void drawCityPlatform(Graphics2D g, int sx, Rectangle p, boolean isGround) {
        if (isGround) {
            // Concrete rooftop base
            g.setColor(new Color(80, 80, 90));
            g.fillRect(sx, p.y, p.width, p.height);
            // Ledge
            g.setColor(new Color(100, 100, 110));
            g.fillRect(sx, p.y, p.width, 6);
            // Rooftop details
            g.setColor(new Color(60, 60, 70));
            for (int tx = sx + 20; tx < sx + p.width - 20; tx += 60) {
                g.fillRect(tx, p.y - 14, 16, 14); // AC units
                g.setColor(new Color(40, 40, 50));
                g.fillRect(tx + 2, p.y - 12, 12, 10);
                g.setColor(new Color(60, 60, 70));
            }
        } else {
            // Floating concrete slab
            g.setColor(new Color(90, 90, 100));
            g.fillRect(sx, p.y, p.width, p.height);
            g.setColor(new Color(110, 110, 120));
            g.fillRect(sx, p.y, p.width, 5);
            // Metal edge
            g.setColor(new Color(150, 150, 160));
            g.setStroke(new BasicStroke(2));
            g.drawRect(sx, p.y, p.width, p.height);
            g.setStroke(new BasicStroke(1));
            // Shadow
            g.setColor(new Color(0, 0, 0, 50));
            g.fillRect(sx + 4, p.y + p.height + 2, p.width, 6);
        }
    }

    private void drawDungeonPlatform(Graphics2D g, int sx, Rectangle p, boolean isGround) {
        if (isGround) {
            g.setColor(new Color(55, 35, 70));
            g.fillRect(sx, p.y, p.width, p.height);
            g.setColor(new Color(75, 50, 95));
            g.fillRect(sx, p.y, p.width, 8);
            // Cracks
            g.setColor(new Color(35, 20, 50));
            g.setStroke(new BasicStroke(1.5f));
            for (int cx = sx + 30; cx < sx + p.width - 30; cx += 80) {
                g.drawLine(cx, p.y + 2, cx + 20, p.y + 8);
            }
            g.setStroke(new BasicStroke(1));
        } else {
            g.setColor(new Color(60, 35, 80));
            g.fillRect(sx, p.y, p.width, p.height);
            g.setColor(new Color(90, 55, 115));
            g.fillRoundRect(sx, p.y, p.width, 10, 4, 4);
            // Glowing rune
            g.setColor(new Color(150, 80, 200, 120));
            g.fillOval(sx + p.width/2 - 5, p.y + 4, 10, 6);
            g.setColor(new Color(0, 0, 0, 50));
            g.fillRect(sx + 3, p.y + p.height + 2, p.width, 5);
        }
    }

    private void drawGoal(Graphics2D g) {
        int fx = goal.x - cameraX + goal.width / 2;
        int fy = goal.y;
        if (fx < -30 || fx > GamePanel.SCREEN_WIDTH + 30) return;

        // Find the platform the goal sits on to anchor the pole base
        int poleBaseY = fy + goal.height;

        // Pole
        g.setColor(new Color(200, 200, 210));
        g.setStroke(new BasicStroke(5));
        g.drawLine(fx, fy, fx, poleBaseY);
        g.setStroke(new BasicStroke(1));

        // Flag
        int[] flagX = {fx, fx + 30, fx};
        int[] flagY = {fy, fy + 13, fy + 26};
        Color flagColor = levelNum == 3 ? new Color(200,50,200)
                : levelNum == 2 ? new Color(50,150,255)
                : new Color(220,50,50);
        g.setColor(flagColor);
        g.fillPolygon(flagX, flagY, 3);
        g.setColor(Color.YELLOW);
        g.fillOval(fx + 9, fy + 8, 9, 9);

        // Base block sitting ON TOP of platform surface
        // Base block — wider and flush on platform
        g.setColor(new Color(120, 80, 30));
        g.fillRoundRect(fx - 8, poleBaseY - 8, 16, 10, 3, 3);
        g.setColor(new Color(160, 110, 50));
        g.fillRoundRect(fx - 6, poleBaseY - 8, 12, 5, 2, 2);
    }

    public boolean playerReachedGoal(Player player) { return goal.intersects(player.getBounds()); }
    public ArrayList<Rectangle> getPlatforms() { return platforms; }
    public int getLevelWidth() { return levelWidth; }
    public int getLevelNum() { return levelNum; }
}