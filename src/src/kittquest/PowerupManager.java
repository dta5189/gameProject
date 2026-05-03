package kittquest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * PowerupManager.java - Chest Powerups
 * Handles spawning, drawing, collection and effects
 * for 3 chest types: Shield (blue), Double Jump (green), Speed (pink)
 */
public class PowerupManager {

    public enum PowerupType { SHIELD, DOUBLE_JUMP, SPEED }

    // ── Individual chest ──────────────────────────────────────────────────────
    static class Chest {
        int x, y;
        int width = 32, height = 32;
        PowerupType type;
        boolean collected = false;

        // Sprite frame index per type
        // Frame 0 = blue (shield), Frame 1 = green (double jump), Frame 2 = pink (speed)
        int frame;

        Chest(int x, int y, PowerupType type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.frame = type == PowerupType.SHIELD ? 0
                    : type == PowerupType.DOUBLE_JUMP ? 1
                    : 2;
        }

        void draw(Graphics2D g, int camX, BufferedImage sheet) {
            if (collected) return;
            int sx = x - camX;
            if (sx + width < 0 || sx > GamePanel.SCREEN_WIDTH) return;

            if (sheet != null) {
                // Crop correct frame from sprite sheet
                int frameX = frame * 32;
                g.drawImage(sheet, sx, y, sx + width, y + height,
                        frameX, 0, frameX + 32, 32, null);
            } else {
                // Fallback colored rect if sprite not loaded
                Color[] colors = {new Color(80,120,255), new Color(80,200,80), new Color(255,100,180)};
                g.setColor(colors[frame]);
                g.fillRoundRect(sx, y, width, height, 6, 6);
                g.setColor(Color.YELLOW);
                g.fillRect(sx + 12, y + 10, 8, 12);
            }

            // Glow effect based on type
            Color[] glows = {new Color(80,120,255,60), new Color(80,200,80,60), new Color(255,100,180,60)};
            g.setColor(glows[frame]);
            long t = System.currentTimeMillis();
            int pulse = (int)(Math.abs(Math.sin(t / 400.0)) * 6);
            g.fillRoundRect(sx - pulse, y - pulse, width + pulse*2, height + pulse*2, 10, 10);
        }

        Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    }

    // ── Active effect on player ───────────────────────────────────────────────
    public static PowerupType activeEffect = null;
    public static int effectTimer = 0;
    private static final int EFFECT_DURATION = 300; // 5 seconds at 60fps

    private ArrayList<Chest> chests = new ArrayList<>();
    private BufferedImage spriteSheet;
    private Random rand = new Random();
    private int levelNum;

    public PowerupManager(int level) {
        this.levelNum = level;
        loadSprite();
        if (level >= 2) spawnChests(level);
    }

    private void loadSprite() {
        try {
            // Try resources root path first
            java.io.InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("sprites/piskelchest.png");
            if (is != null) {
                spriteSheet = ImageIO.read(is);
                System.out.println("[PowerupManager] Sprite loaded successfully");
            } else {
                // Fallback — try loading directly from file system
                java.io.File f = new java.io.File("assets/sprites/piskelchest.png");
                if (f.exists()) {
                    spriteSheet = ImageIO.read(f);
                    System.out.println("[PowerupManager] Sprite loaded from filesystem");
                } else {
                    System.out.println("[PowerupManager] Sprite not found at either path");
                }
            }
        } catch (Exception e) {
            System.out.println("[PowerupManager] Error: " + e.getMessage());
        }
    }

    private void spawnChests(int level) {
        chests.clear();
        PowerupType[] types = PowerupType.values();

        int[][] positions;
        if (level == 2) {
            // Placed on top of platforms (y = platform.y - chest height)
            positions = new int[][]{
                    {420,  368},  // on platform at y=390
                    {670,  343},  // on platform at y=375
                    {900,  318},  // on platform at y=350
                    {1580, 268},  // on platform at y=300
            };
        } else {
            // Level 3 — on floating platforms
            positions = new int[][]{
                    {160,  308},  // platform y=340
                    {550,  308},  // platform y=340
                    {930,  298},  // platform y=330
                    {1510, 228},  // platform y=260
                    {1880, 218},  // platform y=250
                    {2100, 370},  // boss arena ground
            };
        }

        for (int[] pos : positions) {
            PowerupType t = types[rand.nextInt(types.length)];
            chests.add(new Chest(pos[0], pos[1], t));
        }
    }

    public void update(Player player) {
        // Tick active effect down
        if (activeEffect != null) {
            effectTimer--;
            if (effectTimer <= 0) {
                deactivateEffect(player);
            }
        }

        // Check chest collection
        Rectangle pb = player.getBounds();
        for (Chest c : chests) {
            if (!c.collected && c.getBounds().intersects(pb)) {
                c.collected = true;
                activateEffect(c.type, player);
            }
        }
    }

    private void activateEffect(PowerupType type, Player player) {
        // Cancel previous effect first
        if (activeEffect != null) deactivateEffect(player);

        activeEffect  = type;
        effectTimer   = EFFECT_DURATION;

        switch (type) {
            case SPEED:
                player.applySpeedBoost(true);
                break;
            case SHIELD:
                player.applyShield(true);
                break;
            case DOUBLE_JUMP:
                player.applyDoubleJump(true);
                break;
        }
        System.out.println("[Powerup] Activated: " + type);
    }

    private void deactivateEffect(Player player) {
        if (activeEffect == null) return;
        switch (activeEffect) {
            case SPEED:       player.applySpeedBoost(false); break;
            case SHIELD:      player.applyShield(false);     break;
            case DOUBLE_JUMP: player.applyDoubleJump(false); break;
        }
        activeEffect = null;
        effectTimer  = 0;
    }

    public void draw(Graphics2D g) {
        for (Chest c : chests) c.draw(g, LevelManager.cameraX, spriteSheet);
    }

    // Draw HUD indicator showing active powerup
    public void drawHUD(Graphics2D g) {
        if (activeEffect == null) return;

        int hudX = GamePanel.SCREEN_WIDTH / 2 - 60;
        int hudY = GamePanel.SCREEN_HEIGHT - 45;

        // Background pill
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(hudX, hudY, 120, 30, 12, 12);

        // Color and label per type
        Color[] colors = {new Color(80,120,255), new Color(80,200,80), new Color(255,100,180)};
        String[] labels = {"SHIELD", "DBL JUMP", "SPEED"};
        int idx = activeEffect.ordinal();

        // Timer bar
        float pct = effectTimer / (float)EFFECT_DURATION;
        g.setColor(colors[idx]);
        g.fillRoundRect(hudX + 2, hudY + 2, (int)((116) * pct), 26, 10, 10);

        // Label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(labels[idx], hudX + 60 - fm.stringWidth(labels[idx])/2, hudY + 20);
    }

    public static void reset() {
        activeEffect = null;
        effectTimer  = 0;
    }
}
