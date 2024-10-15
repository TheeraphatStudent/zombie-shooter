package components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import utils.UseText;

public class LevelState extends JPanel {
    private int zombieOnState = 0;
    private int zombieRemain = 0;
    private int state = 0;

    private JTextPane stateLevel;
    private JTextPane zombies;

    public LevelState() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.anchor = GridBagConstraints.BASELINE;
        gridConst.insets = new Insets(0, 0, 0, 0);
        gridConst.weightx = 1;
        gridConst.weighty = 1;

        stateLevel = new UseText(28, 200, 40, true).createSimpleText(String.format("State %d", state), Color.BLACK, null, Font.PLAIN);
        add(stateLevel, gridConst);

        gridConst.gridy = 1;

        zombies = new UseText(18, 200, 30, true).createSimpleText(String.format("Zombie %d / %d", zombieRemain, zombieOnState), Color.BLACK, null, Font.PLAIN);
        add(zombies, gridConst);
    }

    public void setLevelState(int levelState) {
        this.state = levelState;
        stateLevel.setText(String.format("State %d", this.state));

        revalidateContent();

    }

    public void setZombieOnState(int zombie) {
        this.zombieOnState = zombie;
        zombies.setText(String.format("Zombie %d / %d", zombieRemain, zombieOnState));

        revalidateContent();

    }

    public void setZombieRemain(int remain) {
        this.zombieRemain = remain;
        zombies.setText(String.format("Zombie %d / %d", zombieRemain, zombieOnState));

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
