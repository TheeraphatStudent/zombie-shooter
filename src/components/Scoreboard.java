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

    private JTextPane killedText;
    private JTextPane requireText;

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
        requireText = new UseText(20, 200, 40, true).createSimpleText(String.format("Require: %d / %d", 0, 0),
                Color.BLACK, Color.WHITE, Font.PLAIN);
        add(requireText, gridConst);
    }

    public void setKilled(int killed) {
        this.killed = killed;
        killedText.setText(String.format("Kill: %d", killed));

        revalidateContent();
    }

    private void revalidateContent() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
            }
        });
    }
}
