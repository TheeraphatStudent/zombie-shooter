package page.controls;

import java.awt.Dimension;

import javax.swing.JFrame;

import page.home.GameCenter;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

public class GameContent extends JFrame {

    private GameCenter gameCenter;

    public GameContent(GameCenter gameCenter) {
        this.gameCenter = gameCenter;

        createFrame();

    }

    public GameContent() {
        createFrame();

    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Let's Survive");
        setLocationRelativeTo(null);

        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
