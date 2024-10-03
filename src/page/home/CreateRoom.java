package page.home;

import java.awt.Dimension;

import javax.swing.JFrame;

import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

public class CreateRoom extends JFrame {

    private GameCenter gameCenter;

    public CreateRoom(GameCenter gameCenter) {
        this.gameCenter = gameCenter;

        createFrame();

    }

    public CreateRoom() {
        createFrame();

    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Create Room");
        setLocationRelativeTo(null);

        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
