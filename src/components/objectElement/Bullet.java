package components.objectElement;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JPanel;

interface ManageBulletElement {
    final int BULLET_SIZE = 10;
    final int BULLET_SPEED = 8;

}

interface BulletProps {
    void drawContent(Graphics2D g2d);

    void isOutOfBounds(int width, int height);

    void setIsAlive(Boolean isActive);

}

public class Bullet extends JPanel implements ManageBulletElement {
    private double x, y;
    private double dx, dy;
    private boolean isActive = true;

    public Bullet(double startX, double startY, double angle) {
        this.x = startX;
        this.y = startY;

        this.dx = Math.cos(angle) * BULLET_SPEED;
        this.dy = Math.sin(angle) * BULLET_SPEED;
    }

    public void move() {
        if (!isActive)
            return;
        x += dx;
        y += dy;
    }

    // วาดลูกกระสุน
    public void drawContent(Graphics2D g2d) {
        if (!isActive)
            return;

        g2d.setColor(Color.RED);
        g2d.drawRect((int) x, (int) y, BULLET_SIZE, BULLET_SIZE);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(
                (int) x,
                (int) y,
                BULLET_SIZE, BULLET_SIZE);
    }

    // Check out of frame
    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    // เมื่อผู้เล่นติดเชื้อ หรือ ตาย จะยิงไม่ได้
    public void setIsAlive(Boolean isActive) {
        this.isActive = isActive;

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, BULLET_SIZE, BULLET_SIZE);
    }
}
