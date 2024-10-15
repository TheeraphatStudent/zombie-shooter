package components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JTextPane;

import utils.UseText;

public class Scoreboard extends JPanel {

    private int killed = 0;

    private int maxZombie = 0;
    private int neededZombie = 0;

    private JTextPane killedText;
    private JTextPane rankUp;

    public Scoreboard() {
        createScoreboard();
    }

    public void createScoreboard() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.anchor = GridBagConstraints.BASELINE;
        gridConst.insets = new Insets(0, 0, -10, 0);
        gridConst.weightx = 1;
        gridConst.weighty = 1;

        add(new UseText(20, 200, 40, false).createSimpleText("Scoreboard", Color.BLACK, null, Font.PLAIN),
                gridConst);

        gridConst.insets = new Insets(0, 0, 0, 0);
        gridConst.gridy = 1;
        killedText = new UseText(20, 200, 40, true).createSimpleText(String.format("Kill: %d", killed), Color.BLACK,
                Color.WHITE, Font.PLAIN);
        add(killedText, gridConst);

        gridConst.insets = new Insets(10, 0, -10, 0);
        gridConst.gridy = 2;
        add(new UseText(20, 200, 40, false).createSimpleText("Rank up", Color.BLACK, null, Font.PLAIN),
                gridConst);

        gridConst.insets = new Insets(0, 0, 0, 0);
        gridConst.gridy = 3;
        rankUp = new UseText(20, 200, 40, true).createSimpleText(String.format("Require: %d / %d", neededZombie, maxZombie),
                Color.BLACK, Color.WHITE, Font.PLAIN);
        add(rankUp, gridConst);
    }

    public void setKilled(int killed) {
        this.killed = killed;
        killedText.setText(String.format("Kill: %d", killed));
        
        revalidateContent();
    }
    
    public void setMaxZombie(int max) {
        this.maxZombie = max;
        rankUp.setText(String.format("Require: %d / %d", neededZombie, maxZombie));

        revalidateContent();

    }

    public void setNeededKilled(int needed) {
        this.neededZombie = needed;

        revalidateContent();
    }

    private void revalidateContent() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                killedText.revalidate();
                killedText.repaint(); 

                rankUp.revalidate();
                rankUp.repaint(); 

                revalidate();
                repaint();
            }
        });
    }
}
