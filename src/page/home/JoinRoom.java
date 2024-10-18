package page.home;

import java.awt.*;
import java.util.function.Supplier;

import javax.swing.*;

import client.Client;
import client.Server;
import components.CoverTitle;
import components.DrawMouse;
import models.ClientObj;
import page.controls.WaitingRoom;
import utils.LoadImage;
import utils.UseAlert;
import utils.UseButton;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

public class JoinRoom extends JFrame {

    Server server;
    Client client;

    // Models
    ClientObj clientObj;

    private GameCenter gameCenter;
    private DrawMouse drawMouse;

    public JoinRoom(GameCenter gameCenter, ClientObj clientObj) {
        this.gameCenter = gameCenter;
        this.clientObj = clientObj;
        this.server = this.clientObj.getClientServer();

        createFrame();
    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Join Room");
        setLocationRelativeTo(null);

        JLayeredPane layers = new JLayeredPane();

        // Background
        String backgroundPath = "resource/images/background/plain.png";
        LoadImage.BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                backgroundPath, this.getWidth(), this.getHeight(), 1, .5, true);
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Title Content
        JPanel titleContent = new CoverTitle(clientObj.getClientName(), clientObj.getClientIp());
        
        titleContent.setLayout(null);
        titleContent.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(titleContent, JLayeredPane.PALETTE_LAYER);

        // Join Room Content
        JPanel joinRoomContent = createJoinRoomContent();
        joinRoomContent.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(joinRoomContent, JLayeredPane.MODAL_LAYER);

        // Mouse
        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowResize().addWindowResize(this, new Component[] { backgroundPanel, joinRoomContent, drawMouse },
                new Component[] { layers });
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
    }

    private JPanel createJoinRoomContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.decode("#858585"));

        GridBagConstraints gridConst = new GridBagConstraints();
        gridConst.weightx = 1;
        gridConst.weighty = 1;
        gridConst.gridx = 1;
        gridConst.fill = GridBagConstraints.HORIZONTAL;

        UseText useText = new UseText(24, 100, 40, false);
        JTextField serverIpField = useText.createTextField("", Color.WHITE, true);
        serverIpField.setPreferredSize(new Dimension(100, 50));

        // Server IP
        gridConst.gridy = 0;
        gridConst.insets = new Insets(10, 20, 0, 20);
        formPanel.add(useText.createSimpleText("Server IP: ", Color.BLACK, null, Font.BOLD), gridConst);

        gridConst.gridy = 1;
        gridConst.insets = new Insets(0, 20, 10, 20);
        formPanel.add(serverIpField, gridConst);

        // Port
        gridConst.gridy = 2;
        gridConst.insets = new Insets(0, 20, 0, 20);
        formPanel.add(useText.createSimpleText("Port: ", Color.BLACK, null, Font.BOLD), gridConst);

        JTextField serverPortField = useText.createTextField("", Color.WHITE, true);
        serverPortField.setPreferredSize(new Dimension(100, 50));

        gridConst.gridy = 3;
        gridConst.insets = new Insets(0, 20, 10, 20);
        formPanel.add(serverPortField, gridConst);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JPanel headers = new JPanel(new GridLayout(1, 2, 20, 20));
        headers.setOpaque(false);

        JPanel footer = new JPanel(new GridLayout());

        UseButton useButton = new UseButton(24);

        // Headers
        JButton joinRoomBtn = useButton.createSimpleButton("Join", Color.decode("#B0FFBC"), 100, 40, "hand");

        joinRoomBtn.addActionListener(e -> {

            String serverIp = serverIpField.getText().trim();
            String serverPort = serverPortField.getText().trim();

            if (serverIp.isEmpty() || serverPort.isEmpty()) {
                new UseAlert().warringAlert("Server IP or Server Port is empty! Please try again.");
                return;
            }

            try {
                int port = Integer.parseInt(serverPort);
                if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException("Port number out of valid range (0-65535)");
                }

                System.out.println("Connecting to IP: " + serverIp);
                System.out.println("Port: " + port);
                System.out.println("Join Work!");

                client = new Client(serverIp, port);
                client.start();

            } catch (NumberFormatException numExc) {
                new UseAlert().warringAlert("Port should be a valid number between 0 and 65535!");

            } catch (IllegalArgumentException IllExc) {
                new UseAlert().warringAlert("Something went wrong! -> " + IllExc.getMessage());

            } catch (Exception exc) {
                new UseAlert().warringAlert("Trying to connect fail! -> " + exc.getMessage());

            }

        });

        headers.add(joinRoomBtn);

        JButton createRoomBtn = useButton.createButtonAndChangePage(
                "",
                "Create Room",
                Color.decode("#FEFFA7"),
                250,
                40,
                "hand",
                this,
                () -> new WaitingRoom(this.server, this.client, this.clientObj, this.gameCenter));
        headers.add(createRoomBtn);

        // Footer
        JButton back = useButton.createButtonAndChangePage(
                "",
                "Back",
                Color.decode("#FFB0B0"),
                250, 40,
                "hand",
                this,
                () -> this.gameCenter);
        footer.add(back);

        gridConst.weightx = 1;
        gridConst.weighty = 1;
        gridConst.gridx = 1;
        gridConst.fill = GridBagConstraints.BOTH;

        gridConst.gridy = 0;
        gridConst.insets = new Insets(0, 0, 20, 0);
        buttonPanel.add(headers, gridConst);

        gridConst.gridy = 1;
        buttonPanel.add(footer, gridConst);

        gridConst.gridy = 4;
        gridConst.insets = new Insets(30, 20, 10, 20);
        formPanel.add(buttonPanel, gridConst);

        content.add(formPanel);
        return content;
    }
}