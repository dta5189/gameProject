package kittquest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Enemy {

    // ── Regular bald old man patroller ───────────────────────────────────────
    static class EnemyUnit {
        float x, y;
        int width = 36, height = 44;
        float velocityX;
        int leftBound, rightBound;

        EnemyUnit(int sx, int sy, int left, int right, float spd) {
            x=sx; y=sy; leftBound=left; rightBound=right; velocityX=spd;
        }

        void update() {
            x += velocityX;
            if (x <= leftBound || x + width >= rightBound) velocityX = -velocityX;
        }

        void draw(Graphics2D g, int camX) {
            int sx = (int)x - camX;
            if (sx + width < 0 || sx > GamePanel.SCREEN_WIDTH) return;
            drawBaldOldMan(g, sx, (int)y, velocityX > 0, 1.0f, false);
        }

        Rectangle getBounds() { return new Rectangle((int)x, (int)y, width, height); }
    }

    // ── Projectile (energy book) ──────────────────────────────────────────────
    static class Projectile {
        float x, y, vx, vy;
        boolean active = true;
        int width = 20, height = 14;
        long spawnTime;

        Projectile(float sx, float sy, float tvx, float tvy) {
            x=sx; y=sy; vx=tvx; vy=tvy; spawnTime=System.currentTimeMillis();
        }

        void update() {
            x += vx; y += vy;
            if (System.currentTimeMillis() - spawnTime > 3000) active = false;
            if (x < -50 || x > 3000 || y > GamePanel.SCREEN_HEIGHT + 50) active = false;
        }

        void draw(Graphics2D g, int camX) {
            int sx = (int)x - camX;
            if (sx + width < 0 || sx > GamePanel.SCREEN_WIDTH) return;
            g.setColor(new Color(180, 50, 255, 80));
            g.fillRoundRect(sx-4, (int)y-4, width+8, height+8, 8, 8);
            g.setColor(new Color(80, 20, 140));
            g.fillRoundRect(sx, (int)y, width, height, 4, 4);
            g.setColor(new Color(60, 10, 100));
            g.fillRect(sx, (int)y, 4, height);
            g.setColor(new Color(200, 150, 255));
            g.fillRect(sx+5, (int)y+2, width-7, height-4);
            g.setColor(new Color(255, 100, 255, 180));
            g.drawLine(sx+6, (int)y+4, sx+width-4, (int)y+4);
            g.drawLine(sx+6, (int)y+7, sx+width-4, (int)y+7);
            g.setColor(new Color(200, 100, 255, 120));
            g.fillOval(sx-(int)(vx*2), (int)y+3, 8, 8);
        }

        boolean hits(Player p) {
            return active && new Rectangle((int)x, (int)y, width, height).intersects(p.getBounds());
        }
    }

    // ── Final Boss ────────────────────────────────────────────────────────────
    static class Boss {
        float x, y;
        int width = 100, height = 150;
        float velocityX = 0;
        int maxHp = 15, hp = 15;
        int chargeTimer = -180, chargeCooldown = 200, chargeFrames = 0;
        int shootTimer = 0, shootCooldown = 80;
        boolean charging = false;
        float chargeSpeed = 6f, walkSpeed = 1.2f;
        Player target;
        ArrayList<Projectile> projectiles = new ArrayList<>();

        // Sprite
        BufferedImage spriteSheet = null;
        int animFrame = 0, animTimer = 0;
        static final int FRAME_COUNT = 5;
        static final int FRAME_W     = 172;
        static final int FRAME_H     = 273;

        Boss(int sx, int sy, Player p) {
            x=sx; y=sy; target=p;
            try {
                java.io.InputStream is = getClass().getClassLoader()
                        .getResourceAsStream("sprites/joeBossSprite.png");
                if (is != null) {
                    spriteSheet = ImageIO.read(is);
                    System.out.println("[Boss] Sprite loaded successfully");
                } else {
                    System.out.println("[Boss] Sprite not found");
                }
            } catch (Exception e) {
                System.out.println("[Boss] Error: " + e.getMessage());
            }
        }

        void update() {
            if (chargeTimer < 0) { chargeTimer++; return; }

            chargeTimer++;
            shootTimer++;

            if (!charging) {
                velocityX = target.x > x ? walkSpeed : -walkSpeed;
                if (chargeTimer >= chargeCooldown) {
                    charging=true; chargeFrames=0; chargeTimer=0;
                    velocityX = target.x > x ? chargeSpeed : -chargeSpeed;
                }
            } else {
                chargeFrames++;
                if (chargeFrames > 55) { charging=false; velocityX=0; }
            }

            if (shootTimer >= shootCooldown) {
                shootTimer = 0;
                shootAtPlayer();
            }

            x += velocityX;
            if (x < 1920) x = 1920;
            if (x + width > 2400) x = 2400 - width;

            // Animate
            animTimer++;
            int frameSpeed = charging ? 3 : 6;
            if (animTimer >= frameSpeed) {
                animFrame = (animFrame + 1) % FRAME_COUNT;
                animTimer = 0;
            }

            projectiles.removeIf(proj -> !proj.active);
            for (Projectile proj : projectiles) proj.update();
        }

        void shootAtPlayer() {
            float cx = x + width/2f, cy = y + height/3f;
            float tx = target.x + 20f, ty = target.y + 20f;
            float dist = (float)Math.sqrt((tx-cx)*(tx-cx)+(ty-cy)*(ty-cy));
            float speed = 4.5f;
            float vx = (tx-cx)/dist*speed, vy = (ty-cy)/dist*speed;
            projectiles.add(new Projectile(cx, cy, vx, vy));
            if (hp < maxHp/2) {
                projectiles.add(new Projectile(cx, cy, vx*1.1f, vy-1.5f));
                projectiles.add(new Projectile(cx, cy, vx*0.9f, vy+1.5f));
            }
        }

        void draw(Graphics2D g, int camX) {
            int sx = (int)x - camX;
            if (sx + width < -20 || sx > GamePanel.SCREEN_WIDTH + 20) return;

            for (Projectile proj : projectiles) proj.draw(g, camX);

            // Aura glow
            long t = System.currentTimeMillis();
            int auraAlpha = (int)(Math.abs(Math.sin(t/300.0))*80)+40;
            g.setColor(new Color(150, 50, 200, auraAlpha));
            g.fillOval(sx-15, (int)y-15, width+30, height+30);

            if (spriteSheet != null) {
                int frameX = animFrame * FRAME_W;
                if (velocityX < 0) {
                    // Flip left
                    g.drawImage(spriteSheet,
                            sx+width, (int)y, sx, (int)y+height,
                            frameX, 0, frameX+FRAME_W, FRAME_H, null);
                } else {
                    g.drawImage(spriteSheet,
                            sx, (int)y, sx+width, (int)y+height,
                            frameX, 0, frameX+FRAME_W, FRAME_H, null);
                }
            } else {
                drawBaldOldMan(g, sx, (int)y, velocityX >= 0, 2.5f, charging);
            }

            // Name tag
            g.setColor(new Color(0,0,0,180));
            g.fillRoundRect(sx-5, (int)y-42, 110, 24, 8, 8);
            g.setColor(new Color(255,80,80));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("PROF. OAKES", sx-2, (int)y-24);

            // HP bar
            int barW=110, barX=sx-5, barY=(int)y-20;
            g.setColor(new Color(40,0,0));
            g.fillRoundRect(barX, barY, barW, 14, 6, 6);
            int fill=(int)((hp/(float)maxHp)*barW);
            Color barColor = hp > maxHp*0.6 ? new Color(50,200,50)
                    : hp > maxHp*0.3 ? new Color(255,180,0)
                    :                  new Color(255,40,40);
            g.setColor(barColor);
            g.fillRoundRect(barX, barY, fill, 14, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 9));
            g.drawString("BOSS "+hp+"/"+maxHp, barX+4, barY+11);

            if (charging) {
                g.setColor(new Color(255,80,0));
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("CHARGING!", sx-5, (int)y-50);
            }
        }

        boolean hits(Player p) {
            return new Rectangle((int)x,(int)y,width,height).intersects(p.getBounds());
        }

        boolean projectileHits(Player p) {
            for (Projectile proj : projectiles) {
                if (proj.hits(p)) { proj.active=false; return true; }
            }
            return false;
        }
    } // ← end Boss class

    // ── Enemy outer class fields ──────────────────────────────────────────────
    private ArrayList<EnemyUnit> units = new ArrayList<>();
    private Boss boss = null;
    private int levelNum;

    public Enemy(int level) {
        this.levelNum = level;
        spawnForLevel(level);
    }

    private void spawnForLevel(int level) {
        units.clear(); boss=null;
        if (level==1) {
            units.add(new EnemyUnit(380,  406, 280,  500,  1.6f));
            units.add(new EnemyUnit(700,  406, 580,  900,  1.8f));
            units.add(new EnemyUnit(1100, 406, 980,  1280, 1.6f));
            units.add(new EnemyUnit(1520, 406, 1380, 1720, 1.8f));
        } else if (level==2) {
            units.add(new EnemyUnit(450,  356, 420,  600,  1.6f));
            units.add(new EnemyUnit(720,  326, 670,  860,  1.6f));
            units.add(new EnemyUnit(1300, 296, 1280, 1490, 1.8f));
            units.add(new EnemyUnit(1840, 256, 1790, 1950, 1.6f));
        } else {
            units.add(new EnemyUnit(200,  406, 100,  350,  3.5f));
            units.add(new EnemyUnit(500,  406, 420,  620,  4.0f));
            units.add(new EnemyUnit(800,  406, 700,  900,  4.0f));
            units.add(new EnemyUnit(1100, 406, 980,  1280, 3.8f));
            units.add(new EnemyUnit(1450, 406, 1360, 1560, 4.0f));
        }
    }

    public void initBoss(Player p) {
        if (levelNum==3 && boss==null) {
            boss = new Boss(2300, 300, p);
        }
    }

    public void update() {
        for (EnemyUnit u : units) u.update();
        if (boss != null) boss.update();
    }

    public void draw(Graphics2D g) {
        for (EnemyUnit u : units) u.draw(g, LevelManager.cameraX);
        if (boss != null) boss.draw(g, LevelManager.cameraX);
    }

    public boolean isCatchingPlayer(Player player) {
        Rectangle pb = player.getBounds();
        for (EnemyUnit u : units) if (u.getBounds().intersects(pb)) return true;
        if (boss != null && (boss.hits(player) || boss.projectileHits(player))) return true;
        return false;
    }

    public Rectangle getBounds() {
        return units.isEmpty() ? new Rectangle() : units.get(0).getBounds();
    }

    // ── Shared drawing: bald white old man ───────────────────────────────────
    static void drawBaldOldMan(Graphics2D g, int x, int y, boolean fr, float scale, boolean angry) {
        int W=(int)(36*scale);
        int headW=(int)(28*scale), headH=(int)(26*scale);
        int headX=x+(W-headW)/2, headY=y;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(160,160,165));
        g.fillRoundRect(x+(int)(4*scale),y+(int)(16*scale),(int)(28*scale),(int)(28*scale),8,8);
        g.setColor(new Color(130,130,135));
        g.fillRect(x+(int)(13*scale),y+(int)(18*scale),(int)(10*scale),(int)(24*scale));

        g.setColor(new Color(235,210,190));
        g.fillRect(x+(int)(11*scale),y+(int)(12*scale),(int)(14*scale),(int)(8*scale));

        g.setColor(new Color(238,208,182));
        g.fillOval(headX,headY,headW,headH);
        g.setColor(new Color(255,235,215));
        g.fillOval(headX+(int)(5*scale),headY+(int)(1*scale),(int)(12*scale),(int)(9*scale));
        g.setColor(new Color(195,162,130));
        g.fillOval(headX+(int)(2*scale),headY+(int)(7*scale),(int)(5*scale),(int)(3*scale));
        g.fillOval(headX+(int)(19*scale),headY+(int)(5*scale),(int)(4*scale),(int)(3*scale));

        g.setColor(Color.WHITE);
        g.fillOval(headX+(int)(4*scale),headY+(int)(10*scale),(int)(9*scale),(int)(8*scale));
        g.fillOval(headX+(int)(15*scale),headY+(int)(10*scale),(int)(9*scale),(int)(8*scale));
        g.setColor(new Color(35,35,35));
        int po=fr?(int)(2*scale):0;
        g.fillOval(headX+(int)(5*scale)+po,headY+(int)(12*scale),(int)(6*scale),(int)(6*scale));
        g.fillOval(headX+(int)(16*scale)+po,headY+(int)(12*scale),(int)(6*scale),(int)(6*scale));

        g.setColor(new Color(240,240,240));
        g.setStroke(new BasicStroke(3.0f*scale,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        if (angry) {
            g.drawLine(headX+(int)(3*scale),headY+(int)(10*scale),headX+(int)(13*scale),headY+(int)(7*scale));
            g.drawLine(headX+(int)(15*scale),headY+(int)(7*scale),headX+(int)(25*scale),headY+(int)(10*scale));
        } else {
            g.drawLine(headX+(int)(3*scale),headY+(int)(9*scale),headX+(int)(13*scale),headY+(int)(9*scale));
            g.drawLine(headX+(int)(15*scale),headY+(int)(9*scale),headX+(int)(25*scale),headY+(int)(9*scale));
        }
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(215,165,140));
        g.fillOval(headX+(int)(9*scale),headY+(int)(15*scale),(int)(10*scale),(int)(8*scale));
        g.setColor(new Color(140,90,80));
        g.drawArc(headX+(int)(7*scale),headY+(int)(20*scale),(int)(14*scale),(int)(7*scale),0,-180);

        g.setColor(new Color(195,162,130));
        g.setStroke(new BasicStroke(1.3f));
        g.drawLine(headX+(int)(3*scale),headY+(int)(5*scale),headX+(int)(7*scale),headY+(int)(9*scale));
        g.drawLine(headX+(int)(21*scale),headY+(int)(5*scale),headX+(int)(25*scale),headY+(int)(9*scale));
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(160,160,165));
        if (fr) g.fillRoundRect(x+(int)(30*scale),y+(int)(18*scale),(int)(10*scale),(int)(7*scale),4,4);
        else    g.fillRoundRect(x-(int)(4*scale), y+(int)(18*scale),(int)(10*scale),(int)(7*scale),4,4);

        g.setColor(new Color(55,55,75));
        g.fillRoundRect(x+(int)(6*scale), y+(int)(40*scale),(int)(10*scale),(int)(10*scale),4,4);
        g.fillRoundRect(x+(int)(20*scale),y+(int)(40*scale),(int)(10*scale),(int)(10*scale),4,4);

        g.setColor(new Color(35,25,15));
        g.fillRoundRect(x+(int)(3*scale), y+(int)(48*scale),(int)(14*scale),(int)(7*scale),4,4);
        g.fillRoundRect(x+(int)(18*scale),y+(int)(48*scale),(int)(14*scale),(int)(7*scale),4,4);
    }
}