package page.home;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
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

        JLayeredPane layers = new JLayeredPane();

        // ==================== Background ====================

        // Background Image
        String backgroundPath = "resource/images/background/plain.png";
        BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                backgroundPath,
                this.getWidth(),
                this.getHeight(),
                1,
                .5,
                true);

        backgroundPanel.setLayout(new GridBagLayout());

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
}
