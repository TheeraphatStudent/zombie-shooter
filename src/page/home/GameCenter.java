package page.home;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import components.DrawMouse;
import java.awt.Image;
import page.controls.GameContent;
import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseButton;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;

public class GameCenter extends JFrame {

        private String name = "???";
        private String ip = "???";

        // Ref
        private DrawMouse drawMouse;
        private Developer developerPage;
        private CreateRoom createRoomPage;

        public GameCenter() {
                createFrame();

        }

        public GameCenter(String name, String ip) {
                this.name = name;
                this.ip = ip;

                developerPage = new Developer(this);
                createRoomPage = new CreateRoom(this);

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

                // ==================== Title Content ====================
                JPanel titleContent = new JPanel();
                titleContent.setLayout(null);

                // ---------- Name ----------
                JTextPane title_name = new UseText(24, 400, 50)
                                .createSimpleText("Name: " + name, null, null, Font.BOLD);
                title_name.setOpaque(false);

                // ---------- IP ----------
                JTextPane title_ip = new UseText(24, 400, 50)
                                .createSimpleText("IP: " + ip, null, null, Font.BOLD);
                title_ip.setOpaque(false);

                title_name.setBounds(10, 10, 400, 50);
                titleContent.add(title_name);

                title_ip.setBounds(10, 45, 400, 50);
                titleContent.add(title_ip);

                // ==================== Actions ====================
                // ##### Game Title #####
                // JButton gameTItle = new UseButton(32).createSimpleButton(
                // "Zombie Runner",
                // Color.WHITE,
                // 400,
                // 100,
                // "default");

                // gameTItle.setEnabled(false);

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
                                new GameContent(this));

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
                                createRoomPage);

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
                                developerPage);

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
                        new WindowClosingFrameEvent(this);

                }));

                gridConst.gridy = 5;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(exit, gridConst);

                // ==================== Layer ====================

                backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
                backgroundPanel.setOpaque(false);
                layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

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

                new WindowClosingFrameEvent(this);

        }

        public String getDisplayName() {
                return this.name;

        }

        public String getIp() {
                return this.ip;

        }

}
