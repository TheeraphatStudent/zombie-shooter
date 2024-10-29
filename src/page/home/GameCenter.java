package page.home;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import client.Client;
import client.Server;
import components.CoverTitle;
import components.DrawMouse;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import models.ClientObj;
import models.Player;

import java.awt.Image;
import page.controls.GameContent;
import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseButton;
import utils.UseCharacter;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

public class GameCenter extends JFrame implements ManageCharacterElement {

        // Server server;
        private ClientObj client;

        // Ref
        private DrawMouse drawMouse;
        private Developer developerPage;

        public GameCenter(ClientObj client) {
                this.client = client;
                System.out.println("Game Center > Client: " + this.client.getClientName());

                developerPage = new Developer(this, client);
                // createRoomPage = new CreateRoom(this, server);

                createFrame();
        }

        private void createFrame() {
                setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
                setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("Zombie Shooter - Welcome");
                setLocationRelativeTo(null);

                setIconImage(new LoadImage().getImage("resource/images/icon/main.png"));

                JLayeredPane layers = new JLayeredPane();

                String backgroundPath = "resource/images/background/plain.png";
                BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                                backgroundPath,
                                this.getWidth(),
                                this.getHeight(),
                                1,
                                .5,
                                true);

                // Change the layout to GridBagLayout
                backgroundPanel.setLayout(new GridBagLayout());
                GridBagConstraints gridConst = new GridBagConstraints();

                JPanel gameIcon = new JPanel() {
                        @Override
                        public void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Image img = new LoadImage().getImage("resource/images/icon/main.png");

                                if (img != null) {
                                        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                                }
                        }

                        @Override
                        public Dimension getPreferredSize() {
                                return new Dimension(100, 250);
                        }
                };

                gameIcon.setPreferredSize(new Dimension(100, 250));
                gameIcon.setOpaque(false);

                gridConst.gridx = 0;
                gridConst.gridy = 1;
                gridConst.fill = GridBagConstraints.HORIZONTAL;
                gridConst.weighty = 0;
                gridConst.insets = new Insets(0, 0, 100, 0);

                backgroundPanel.add(gameIcon, gridConst);

                JButton singlePlay = new UseButton(32).createButtonAndChangePage(
                                "",
                                "Solo Player",
                                Color.WHITE,
                                400,
                                100,
                                "hand",
                                this,
                                () -> startGameSinglePlay());

                gridConst.gridy = 2;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(singlePlay, gridConst);

                JButton multiplayer = new UseButton(32).createButtonAndChangePage(
                                "",
                                "Multi Player",
                                Color.WHITE,
                                400,
                                100,
                                "hand",
                                this,
                                () -> new JoinRoom(this, client));

                gridConst.gridy = 3;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(multiplayer, gridConst);

                JButton developer = new UseButton(32).createButtonAndChangePage(
                                "",
                                "Developer",
                                Color.WHITE,
                                400,
                                100,
                                "hand",
                                this,
                                () -> developerPage);

                gridConst.gridy = 4;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(developer, gridConst);

                JButton exit = new UseButton(32).createSimpleButton(
                                "Exit",
                                Color.decode("#FFB0B0"),
                                400,
                                100,
                                "hand");

                exit.addActionListener((e -> {
                        new WindowClosingFrameEvent().closePage(GameCenter.this);

                }));

                gridConst.gridy = 5;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(exit, gridConst);

                // ==================== Layer ====================

                backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
                backgroundPanel.setOpaque(false);
                layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

                JPanel titleContent = new CoverTitle(client.getClientName(), client.getClientIp());
                titleContent.setLayout(null);

                titleContent.setBounds(0, 0, this.getWidth(), this.getHeight());
                titleContent.setOpaque(false);
                layers.add(titleContent, JLayeredPane.PALETTE_LAYER);

                drawMouse = new DrawMouse();
                drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
                layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

                // Parent Content
                setContentPane(layers);
                layers.revalidate();
                layers.repaint();

                new WindowResize().addWindowResize(this, new Component[] { backgroundPanel, drawMouse },
                                new Component[] { layers });
                new WindowClosingFrameEvent(this);

        }

        private GameContent startGameSinglePlay() {
                // CreateCharacter character = new CreateCharacter(false, client);
                // character.setBounds(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);

                Player player = new Player(new UseCharacter().getRandomCharacterNo(), null);
                player.setPlayerLocation(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100);

                client.setPlayer(player);

                return new GameContent(this, client);

        }

}
