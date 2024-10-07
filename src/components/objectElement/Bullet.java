package components.objectElement;

import java.awt.Graphics2D;

import java.awt.Color;

interface ManageBulletElement {
    final int BULLET_SIZE = 10;
    final int BULLET_SPEED = 15;

}

interface BulletProps {
    void drawContent(Graphics2D g2d);

    void isOutOfBounds(int width, int height);

    void setIsAlive(Boolean isActive);

}

public class Bullet implements ManageBulletElement {
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

    // วาดลูกกระสุนขึ้นมา
    public void drawContent(Graphics2D g2d) {
        if (!isActive)
            return;

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(
                (int) x,
                (int) y,
                BULLET_SIZE, BULLET_SIZE);
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    // เมื่อผู้เล่นติดเชื้อ หรือ ตาย จะไม่ยิง
    public void setIsAlive(Boolean isActive) {
        this.isActive = isActive;

    }
}
