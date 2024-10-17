package page.home;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import client.Client;
import client.Server;
import components.DrawMouse;
import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

public class CreateRoom extends JFrame {

    Server server;

    private GameCenter gameCenter;
    private DrawMouse drawMouse;

    public CreateRoom(GameCenter gameCenter, Server server) {
        this.gameCenter = gameCenter;
        this.server = server;

        // new Client(server.getServerIp(), server.getServerPort());

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

        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowResize().addWindowResize(this, new Component[]{backgroundPanel, drawMouse}, new Component[]{layers});
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
    
}
