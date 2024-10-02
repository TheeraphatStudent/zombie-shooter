package page.home;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

public class GameCenter extends JFrame {
    public GameCenter() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Welcome");
        setLocationRelativeTo(null);

        String backgroundPath = "resource/images/background/plain.png";
        BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                backgroundPath,
                this.getWidth(),
                this.getHeight(),
                1,
                .5,
                true);

        backgroundPanel.setLayout(new GridBagLayout());

        // Parent Content
        setContentPane(backgroundPanel);

        new WindowClosingFrameEvent(this);

    }

}
