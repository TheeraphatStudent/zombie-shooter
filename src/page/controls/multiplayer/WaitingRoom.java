package page.controls.multiplayer;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
import models.Communication;
import page.controls.GameContent;
import page.home.GameCenter;
import utils.*;

public class WaitingRoom extends JFrame implements ManageCharacterElement {
    private static final long serialVersionUID = 1L;
    private boolean isHost = false;
    private boolean isGameStarted = false;

    private GameCenter gameCenter;
    private Server server;
    private Client client;
    private transient ClientObj clientObj;

    private JPanel content;
    private JTextPane title;
    private JTextPane subTitle;
    private JTextPane serverInfo;

    private int numOfPlayers;

    private List<CreateCharacter> playerCharacters;
    private Timer countdownTimer;
    private int countdownSeconds = 15;

    // ! Communication Contain !
    private Communication communication;

    // ? Global ?
    private String serverIp = "127.1.0.0";
    private String serverPort = "00000";

    private List<ClientObj> clientObjs;
    private List<Integer> requirePlayers;

    // Thread
    private Thread serverThread;
    private Thread clientThread;
    private Thread playersListeningThread;

    public WaitingRoom(Server server, ClientObj clientObj, GameCenter gameCenter, int numOfPlayers) {
        System.out.println("Prepare for host!");

        this.gameCenter = gameCenter;
        this.server = server;
        this.playerCharacters = new ArrayList<>();
        this.communication = new Communication();
        this.clientObj = clientObj;

        System.out.println("Waiting Room > Client : " + this.clientObj.getClientName());

        this.numOfPlayers = numOfPlayers;
        this.server.setRequiredPlayersToStart(this.numOfPlayers);

        this.client = new Client(server.getServerIp(), server.getServerPort(), clientObj);

        this.isHost = true;
        initialMoment(this.isHost);
    }

    public WaitingRoom(Server server, ClientObj clientObj, GameCenter gameCenter, String joinToIp, int onPort) {
        System.out.println("Prepare for client!");

        this.gameCenter = gameCenter;
        this.server = server;
        this.playerCharacters = new ArrayList<>();
        this.communication = new Communication();
        this.clientObj = clientObj;

        this.client = new Client(joinToIp, onPort, clientObj);

        this.isHost = false;
        initialMoment(this.isHost);

    }

    private void initialMoment(boolean isHost) {

        if (isHost) {
            this.serverThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    WaitingRoom.this.server.start();

                }

            });

            this.serverThread.start();

        }

        this.clientThread = new Thread(new Runnable() {

            @Override
            public void run() {
                WaitingRoom.this.client.start();

            }

        });

        this.clientThread.start();

        setupLayout();
        startListeningForPlayers();

    }

    private void setupLayout() {
        System.out.println("On Waiting...");

        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Waiting Room");
        setLocationRelativeTo(null);

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

        gridConst.insets = new Insets(75, 0, 0, 0);

        serverInfo = new UseText(16, 270, 50, true).createSimpleText(
                String.format("Server IP: %s | Port: %s", serverIp, serverPort), Color.BLACK, null,
                Font.PLAIN);
        coverContent.add(serverInfo, gridConst);

        gridConst.insets = new Insets(15, 0, 0, 0);
        gridConst.anchor = GridBagConstraints.NORTH;

        title = new UseText(24, 250, 50, true).createSimpleText("Waiting for player...", Color.BLACK, Color.WHITE,
                Font.BOLD);
        coverContent.add(title, gridConst);

        gridConst.insets = new Insets(75, 0, 0, 0);

        subTitle = new UseText(16, 250, 50, true).createSimpleText("Waiting for player...", Color.BLACK, null,
                Font.PLAIN);
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
                new Component[] { backgroundPanel, drawMouse },
                new Component[] { layers });
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
    }

    private JButton createDisconnectButton() {
        JButton btn = new UseButton(24).createSimpleButton("Disconnect", Color.decode("#FFB0B0"), 250, 50, "hand");
        btn.addActionListener(e -> {
            client.disconnect();
            new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);
        });
        return btn;
    }

    private void startListeningForPlayers() {
        System.out.println("$$$$$ Preparing Start Listening For PLayers! $$$$$");
        System.out.println("---------------------------------------------------");

        playersListeningThread = new Thread(() -> {
            System.out.println("Thread Start Work!");

            while (playerCharacters.size() <= numOfPlayers) {
                if (client != null && client.isConnected()) {
                    try {
                        System.out.println();
                        System.out.println("Waiting Room > On Client Connect!");

                        // Get client > message
                        String message = client.getMessage();

                        // Get client > communication
                        this.communication = client.getCommunication();
                        Map<String, List> contents = communication.getContent();
                        System.out.println(contents.entrySet());

                        this.serverIp = (String) contents.get("SERVER_INFO").get(0);
                        this.serverPort = (String) contents.get("SERVER_INFO").get(1);

                        serverInfo.setText(String.format("IP: %s | Port: %s", serverIp, serverPort));
                        serverInfo.revalidate();
                        serverInfo.repaint();

                        this.clientObjs = contents.get("NEW_PLAYER");
                        this.requirePlayers = contents.get("REQUIRE_PLAYERS");

                        if (requirePlayers != null && !requirePlayers.isEmpty()) {
                            this.numOfPlayers = requirePlayers.get(0);
                        } else {
                            System.out.println("requirePlayers list is null or empty.");
                        }

                        updatePlayerCount();

                        if (clientObjs != null && clientObjs.size() > playerCharacters.size()) {
                            System.out.println("Key: NEW_PLAYER");

                            for (int i = playerCharacters.size(); i < clientObjs.size(); i++) {
                                ClientObj newClient = (ClientObj) clientObjs.get(i);
                                System.out.println("Adding New Client Object: " + newClient);
                                System.out.println("New Client Name: " + newClient.getClientName());

                                SwingUtilities.invokeLater(() -> addPlayer(newClient));
                            }

                            if (message.equals("START_COUNTDOWN")) {
                                startCountdown();
                                client.resetMessage();

                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Something went wrong! -> " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

            if (playerCharacters.size() >= numOfPlayers) {
                SwingUtilities.invokeLater(this::startCountdown);

            }

        });

        playersListeningThread.setDaemon(true);
        playersListeningThread.start();
    }

    private void addPlayer(ClientObj requireClientObj) {
        System.out.println("Added Player: " + requireClientObj);

        // CreateCharacter playerCharacter = new CreateCharacter(false,
        // requireClientObj);
        CreateCharacter playerCharacter = requireClientObj.getPlayer().getCharacter();

        content.add(playerCharacter);
        content.revalidate();
        content.repaint();

        playerCharacters.add(playerCharacter);
        updatePlayerCount();
    }

    private void updatePlayerCount() {
        this.title.setText(String.format("Player %d / %d", playerCharacters.size(), numOfPlayers));

    }

    private void startCountdown() {
        subTitle.setText("Game starting in " + countdownSeconds + "...");
        countdownTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds > 0) {
                subTitle.setText("Game starting in " + countdownSeconds + "...");

            } else {
                ((Timer) e.getSource()).stop();
                if (!this.isGameStarted) {
                    startGame();
                    this.isGameStarted = true;

                }

            }
        });
        countdownTimer.start();
    }

    private void stopWorker() {
        if (this.isHost) {
            this.serverThread.interrupt();
        }
        this.playersListeningThread.interrupt();
        this.clientThread.interrupt();

    }

    @Override
    public void dispose() {
        stopWorker();

        super.dispose();
    }

    private void startGame() {
        stopWorker();
        client.clientSideSendObject("READY_TO_START");

        MultiplayerGameContent gameContent;
        if (this.isHost) {
            gameContent = new MultiplayerGameContent(
                    this.gameCenter,
                    this.clientObj,
                    this.client,
                    this.server,
                    this.clientObjs,
                    this.communication);
        } else {
            gameContent = new MultiplayerGameContent(
                    this.gameCenter,
                    this.clientObj,
                    this.client,
                    this.clientObjs,
                    this.communication);
        }

        SwingUtilities.invokeLater(() -> {
            subTitle.setText("Game starting...");
            content.removeAll();
            revalidate();
            repaint();

            new WindowClosingFrameEvent().navigateTo(this, gameContent, false);

            this.dispose();
        });
    }

}