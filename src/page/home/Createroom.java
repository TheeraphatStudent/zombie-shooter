package page.home;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;
import client.Server;
import components.CoverTitle;
import components.DrawMouse;
import components.character.CreateCharacter;

import java.awt.Color;
import java.awt.Font;
import models.ClientObj;
import utils.LoadImage;
import utils.UseButton;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

public class Createroom extends JFrame {

  Server server;
  Client client;

  // Models
  ClientObj clientObj;

  private GameCenter gameCenter;
  private DrawMouse drawMouse;
  private List<CreateCharacter> playerCharacters;

  public Createroom(Server server, Client client, ClientObj clientObj, GameCenter gameCenter) {
    this.gameCenter = gameCenter;
    this.server = server;
    this.client = client;
    this.playerCharacters = new ArrayList<>();
    this.clientObj = clientObj;

    createFrame();
  }

  private void createFrame() {
    setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
    setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setTitle("create room ");
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

    // Create Room
    JPanel createroom = createroomcontent();
    createroom.setBounds(0, 0, this.getWidth(), this.getHeight());
    layers.add(createroom, JLayeredPane.MODAL_LAYER);

    // Mouse
    drawMouse = new DrawMouse();
    drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
    layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

    setContentPane(layers);
    layers.revalidate();
    layers.repaint();

    new WindowResize().addWindowResize(this, new Component[] { backgroundPanel, createroom, drawMouse },
        new Component[] { layers });
    new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
  }

  private JPanel createroomcontent() {
    JPanel content = new JPanel(new GridBagLayout());
    content.setOpaque(false);

    JPanel formPanel = new JPanel(new GridBagLayout());

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

    JPanel buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setOpaque(false);

    JPanel headers = new JPanel(new GridLayout(1, 2, 20, 20));
    headers.setOpaque(false);

    JPanel footer = new JPanel(new GridLayout());

    UseButton useButton = new UseButton(24);
    JButton back = useButton.createSimpleButton("Back",
        Color.decode("#B0FFBC"),
        100,
        40,
        "hand");
    headers.add(back);

    return content;
  }
}
