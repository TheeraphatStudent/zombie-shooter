package models;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.UUID;

import javax.swing.JPanel;

interface ManageBulletElement {
    final int BULLET_SIZE = 10;
    final int BULLET_SPEED = 8;

}

interface BulletProps {
    void drawContent(Graphics2D g2d);

    void isOutOfBounds(int width, int height);

    void setIsBulletActive(Boolean isActive);

}

public class Bullet extends JPanel implements ManageBulletElement{
    private String bulletId;

    private Player player;

    private double x, y;
    private double dx, dy;
    private boolean isActive = true;

    public Bullet(Player player, double startX, double startY, double angle) {
        this.bulletId = UUID.randomUUID().toString();
        this.player = player;

        this.x = startX;
        this.y = startY;

        this.dx = Math.cos(angle) * BULLET_SPEED;
        this.dy = Math.sin(angle) * BULLET_SPEED;
    }

    public String getBulletId() {
        return this.bulletId;
    }

    public Player getPlayer() {
        return this.player;

    }

    public void move() {
        if (!isActive) {
            return;

        }

        x += dx;
        y += dy;

        // System.out.printf("Dx: %f | Dy: %f\nX: %f | Y: %f\n", dx, dy, x, y);
        // System.out.println();
    }

    // วาดลูกกระสุน
    public void drawContent(Graphics2D g2d) {
        if (!isActive)
            return;

        // g2d.setColor(Color.RED);
        // g2d.drawRect((int) x, (int) y, BULLET_SIZE, BULLET_SIZE);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(
                (int) x,
                (int) y,
                BULLET_SIZE, BULLET_SIZE);
    }

    // Check out of frame
    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
        // return false;

    }

    // เมื่อผู้เล่นติดเชื้อ หรือ ตาย จะยิงไม่ได้
    public void setIsBulletActive(Boolean isActive) {
        this.isActive = isActive;

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, BULLET_SIZE, BULLET_SIZE);
    }
}
