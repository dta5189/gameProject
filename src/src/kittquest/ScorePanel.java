package kittquest;

import java.awt.*;

public class ScorePanel {

    private int score=0, lives=3;

    public ScorePanel() {}

    public void draw(Graphics2D g, int level) {
        g.setColor(new Color(0,0,0,140));
        g.fillRoundRect(10,8,160,30,8,8);
        g.fillRoundRect(GamePanel.SCREEN_WIDTH-170,8,160,30,8,8);
        g.fillRoundRect(GamePanel.SCREEN_WIDTH/2-60,8,120,30,8,8);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,18));
        g.drawString("Score: "+score,20,29);

        g.setColor(new Color(255,200,0));
        g.setFont(new Font("Arial",Font.BOLD,16));
        String lvl=level==3?"BOSS LEVEL":"Level "+level;
        FontMetrics fm=g.getFontMetrics();
        g.drawString(lvl,GamePanel.SCREEN_WIDTH/2-fm.stringWidth(lvl)/2,28);

        for (int i=0;i<3;i++) {
            int hx=GamePanel.SCREEN_WIDTH-155+(i*38);
            g.setColor(i<lives?new Color(255,60,80):new Color(80,40,50));
            drawHeart(g,hx,13);
        }
    }

    private void drawHeart(Graphics2D g,int x,int y) {
        g.fillOval(x,y,12,12); g.fillOval(x+9,y,12,12);
        g.fillPolygon(new int[]{x,x+10,x+21},new int[]{y+8,y+22,y+8},3);
    }

    public void draw(Graphics2D g) { draw(g,1); }
    public void addScore(int pts) { score+=pts; }
    public void loseLife() { lives--; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
}