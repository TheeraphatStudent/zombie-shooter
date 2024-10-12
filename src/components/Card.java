package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import utils.LoadImage;
import utils.UseText;

public class Card extends JPanel {
    public Card(String name, String id, String imgName) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.WHITE);
        setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();

        JPanel head = new RoundedPanel(20, imgName);
        head.setPreferredSize(new Dimension(350, 600));

        JTextPane displayName = new UseText(20, 400, 50, false).createSimpleText("Name: " + name, null, null, Font.PLAIN);
        JTextPane displayId = new UseText(20, 400, 50, false).createSimpleText("Id: " + id, null, null, Font.PLAIN);

        JPanel compressPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        compressPanel.setBackground(Color.WHITE);
        compressPanel.add(displayName);
        compressPanel.add(displayId);

        gridConst.gridy = 0;
        gridConst.weightx = 1.0;
        gridConst.weighty = 1.0;
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.insets = new Insets(25, 25, 25, 25);
        add(head, gridConst);

        gridConst.gridy = 1;
        gridConst.weightx = 1.0;
        gridConst.weighty = 0.1;
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.insets = new Insets(0, 25, 0, 25);
        add(compressPanel, gridConst);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
    }
}

class RoundedPanel extends JPanel {
    private int borderRadius;
    private String imgName;

    public RoundedPanel(int borderRadius, String imgName) {
        this.borderRadius = borderRadius;
        this.imgName = imgName;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D roundedRect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), borderRadius,
                borderRadius);

        g2d.setClip(roundedRect);

        Image image = new LoadImage().getImage(imgName); 
        if (image != null) {
            int imgWidth = image.getWidth(this);
            int imgHeight = image.getHeight(this);

            int x = (getWidth() - imgWidth) / 2;
            int y = (int) ((getHeight() - imgHeight) / 2.25);

            g2d.drawImage(image, x, y, this);
        } else {
            System.err.println("Image not found: " + imgName); 
        }

        g2d.setClip(null); 

        g2d.setColor(Color.GRAY);
        g2d.draw(roundedRect);
    }

}