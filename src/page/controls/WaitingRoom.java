package page.controls;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;

import client.Client;
import client.Server;
import components.CoverTitle;
import components.DrawBulletLine;
import components.DrawMouse;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import models.ClientObj;
import page.home.GameCenter;
import utils.*;

public class WaitingRoom extends JFrame implements ManageCharacterElement {
    private GameCenter gameCenter;
    private Server server;
    private Client client;
    private ClientObj clientObj;

    private JPanel content;
    private JTextPane title;
    private JTextPane subTitle;

    private List<CreateCharacter> playerCharacters;
    private Timer countdownTimer;
    private int countdownSeconds = 15;

    public WaitingRoom(Server server, Client client, ClientObj clientObj, GameCenter gameCenter) {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Waiting Room");
        setLocationRelativeTo(null);

        this.gameCenter = gameCenter;
        this.server = server;
        this.client = client;
        this.playerCharacters = new ArrayList<>();
        this.clientObj = clientObj;

        setupLayout();
        startListeningForPlayers();
    }


    private void setupLayout() {
        JLayeredPane layers = new JLayeredPane();
    
        // Background
        String backgroundPath = "resource/images/background/plain.png";
        LoadImage.BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                backgroundPath, getWidth(), getHeight(), 1, .5, false);
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
    
        DrawMouse drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, getWidth(), getHeight());

        JPanel coverContent = new JPanel();
        coverContent.setLayout(new GridBagLayout());
        coverContent.setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();

        gridConst.gridx = 0;
        gridConst.gridy = 0;
        gridConst.weightx = 1;
        gridConst.weighty = 1;
        gridConst.insets = new Insets(15, 15, 0, 0);

        gridConst.anchor = GridBagConstraints.NORTHWEST;
        coverContent.add(createDisconnectButton(), gridConst);

        gridConst.insets = new Insets(15, 0, 0, 0);
        gridConst.anchor = GridBagConstraints.NORTH;

        title = new UseText(24, 250, 50, true).createSimpleText("Waiting for player...", Color.BLACK, Color.WHITE, Font.BOLD);
        coverContent.add(title, gridConst);

        gridConst.insets = new Insets(75, 0, 0, 0);
        gridConst.anchor = GridBagConstraints.NORTH;

        subTitle = new UseText(16, 250, 50, true).createSimpleText("Waiting for player...", Color.BLACK, null, Font.PLAIN);
        coverContent.add(subTitle, gridConst);

        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                // RenderingHints.VALUE_ANTIALIAS_ON);

                // new DrawBulletLine(g2d, mousePosition, character);

                // Reset Stroke
                g2d.setStroke(new BasicStroke(4f));
            }
        };

        content.setLayout(null);
        content.setOpaque(false);

        content.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(content, JLayeredPane.PALETTE_LAYER);

        coverContent.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(coverContent, JLayeredPane.POPUP_LAYER);
    
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);
        setContentPane(layers);
    
        new WindowResize().addWindowResize(
                this,
                new Component[]{backgroundPanel, drawMouse},
                new Component[]{layers});
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
    }

    private JButton createDisconnectButton() {
        JButton btn = new UseButton(24).createSimpleButton("Disconnect", Color.decode("#FFB0B0"), 250, 50, "hand");
        btn.addActionListener(e -> {
            // client.disconnect();
            new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);
        });
        return btn;
    }

    private void addPlayer() {
        CreateCharacter playerCharacter = new CreateCharacter(false, clientObj); 

        playerCharacter.setBounds(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        content.add(playerCharacter);
        content.revalidate();
        content.repaint();

        playerCharacters.add(playerCharacter);

        updatePlayerCount();
    }

    private void startListeningForPlayers() {
        Timer joinTimer = new Timer(2000, e -> {
            if (playerCharacters.size() < 3) {
                addPlayer();
            }
            

            if (playerCharacters.size() >= 2) {
                ((Timer)e.getSource()).stop();
            }
        });
        joinTimer.start();
    }
    
    private void updatePlayerCount() {
        System.out.println("Update PLayer Count!");

        title.setText("Player " + playerCharacters.size() + " / 3");
        if (playerCharacters.size() >= 2) {
            startCountdown();
        }
    }
    

    private void startCountdown() {
        subTitle.setText("Game starting in " + countdownSeconds + "...");
        countdownTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds > 0) {
                subTitle.setText("Game starting in " + countdownSeconds + "...");

            } else {
                ((Timer)e.getSource()).stop();
                startGame();

            }
        });
        countdownTimer.start();
    }

    private void startGame() {
        new WindowClosingFrameEvent().navigateTo(this, new GameContent(gameCenter, clientObj), false);
        // dispose();

    }
}