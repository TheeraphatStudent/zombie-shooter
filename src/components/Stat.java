package components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import utils.UseText;

public class Stat extends JPanel {
    private volatile GridBagConstraints gridConst = new GridBagConstraints();
    private JTextPane scoreText;

    public Stat() {
        createPersonaStat();
    }

    public JPanel createPersonaStat() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(250, 100));
        panel.setOpaque(false);
        panel.setBackground(Color.RED);

        gridConst.anchor = GridBagConstraints.NORTHWEST;
        gridConst.insets = new Insets(10, 10, 10, 10);
        gridConst.weightx = 1;
        gridConst.weighty = 1;
        gridConst.gridheight = 1;

        JTextPane title = new UseText(28, 200, 50, false).createSimpleText("Scoreboard", Color.BLACK, Color.WHITE, Font.BOLD);
        title.setOpaque(false);
        title.setPreferredSize(new Dimension(200, 50));
        
        panel.add(title, gridConst);

        panel.revalidate();
        panel.repaint();

        return panel;
    }

    public void updateScore(int score) {
        scoreText.setText("Score: " + score);
    }
}

