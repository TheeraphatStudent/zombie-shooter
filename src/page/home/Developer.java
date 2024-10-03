package page.home;

import java.awt.Dimension;

import javax.swing.JFrame;

import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

public class Developer extends JFrame {
    public Developer() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Developer");
        setLocationRelativeTo(null);

        new WindowClosingFrameEvent().navigateTo(this, new GameCenter(), true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
