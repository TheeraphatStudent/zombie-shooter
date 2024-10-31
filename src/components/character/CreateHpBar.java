package components.character;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CreateHpBar extends JPanel {
    private volatile int currentHp;
    private volatile int maxHp;
    private Color colorBar = Color.GREEN;

    public CreateHpBar(int maxHp, Color colorBar) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.colorBar = colorBar;

        setPreferredSize(new Dimension(100, 20));
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;

    }

    public void setHp(int hp) {
        this.currentHp = Math.min(hp, maxHp);
        repaint();
    }

    public void setColorBar(Color colorBar) {
        this.colorBar = colorBar;
        repaint();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(colorBar);
        int barWidth = (int) (getWidth() * ((double) currentHp / maxHp));
        g.fillRect(0, 0, barWidth, getHeight());

        g.setColor(Color.BLACK);
        String hpText = currentHp + " HP";
        g.drawString(hpText, getWidth() / 2 - 15, getHeight() / 2 + 5);
    }
}