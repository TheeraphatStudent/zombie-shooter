package page.controls;

import java.awt.Dimension;

import javax.swing.JFrame;

import page.home.GameCenter;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

public class GameContent extends JFrame {
    public GameContent() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Let's Survive");
        setLocationRelativeTo(null);

        new WindowClosingFrameEvent().navigateTo(this, new GameCenter(), true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
