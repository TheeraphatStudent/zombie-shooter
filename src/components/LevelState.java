package components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utils.UseText;

public class LevelState extends JPanel {
    private int zombieOnState = 0;
    private int zombieRemain = 0;
    private int state = 0;

    public LevelState() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.anchor = GridBagConstraints.BASELINE;
        gridConst.insets = new Insets(0, 0, 0, 0);
        gridConst.weightx = 1;
        gridConst.weighty = 1;

        add(new UseText(28, 200, 40, true).createSimpleText(String.format("State %d", state), Color.BLACK, null, Font.PLAIN),
                gridConst);

        gridConst.gridy = 1;
        add(new UseText(18, 200, 30, true).createSimpleText(
                String.format("Zombie %d / %d", zombieRemain, zombieOnState), Color.BLACK, null, Font.PLAIN),
                gridConst);
    }

    public void setLevelState(int levelState) {
        this.state = levelState;
        revalidateContent();

    }

    public void setZombieOnState(int zombie) {
        this.zombieOnState = zombie;
        revalidateContent();

    }

    public void setZombieRemain(int remain) {
        this.zombieRemain = remain;
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
