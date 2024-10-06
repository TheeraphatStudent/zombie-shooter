package components.character;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CreateHpBar extends JPanel {

    private int hp;
    private Color colorBar = Color.GREEN;

    public CreateHpBar(int hp, Color colorBar) {
        this.hp = hp;
        this.colorBar = colorBar;

        setPreferredSize(new Dimension(100, 20));
    }

    public void setHp(int hp) {
        this.hp = hp;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(colorBar);
        g.fillRect(0, 0, (int) (getWidth() * (hp / 100.0)), getHeight());

        g.setColor(Color.BLACK);
        g.drawString(hp + " HP", getWidth() / 2 - 15, getHeight() / 2 + 5);
    }
}