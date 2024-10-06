package components.objectElement;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import java.awt.Color;

public class Bullet extends JPanel {
    private static final int BULLET_SIZE = 8;
    private static final int BULLET_SPEED = 15;

    private double x, y;
    private double dx, dy;
    private double angle;
    private boolean isActive = true;

    public Bullet(double startX, double startY, double angle) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;

        // Calculate direction based on angle
        this.dx = Math.cos(angle) * BULLET_SPEED;
        this.dy = Math.sin(angle) * BULLET_SPEED;

        setOpaque(false);
        setSize(BULLET_SIZE, BULLET_SIZE);
    }

    public void move() {
        if (!isActive)
            return;

        x += dx;
        y += dy;

        setLocation((int) x, (int) y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isActive)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.YELLOW);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.fillOval(0, 0, BULLET_SIZE, BULLET_SIZE);
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        isActive = false;
    }

    public boolean isOutOfBounds(int frameWidth, int frameHeight) {
        return x < 0 || x > frameWidth || y < 0 || y > frameHeight;
    }
}
