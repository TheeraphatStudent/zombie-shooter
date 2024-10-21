package page.controls.multiplayer;

import java.awt.Dimension;

import javax.swing.JFrame;

import utils.UseGlobal;

public class GameContent extends JFrame {
    public GameContent() {
        createFrame();

    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Let's Survive ");
        setLocationRelativeTo(null);

    }

}
