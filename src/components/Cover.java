package components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import utils.UseGlobal;

public class Cover extends JPanel {
    private float coverBackground = 0f;
    private JLayeredPane layers;

    public Cover() {
        setLayout(null);
        setPreferredSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setOpaque(false);

        layers = new JLayeredPane();
        layers.setLayout(null);
        layers.setBounds(0, 0, UseGlobal.getWidth(), UseGlobal.getHeight());

    }

    public void setCoverBackground(float getWeightCover) {
        this.coverBackground = getWeightCover;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, coverBackground));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

    }
}
