package page.home;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import components.Card;
import components.CoverTitle;
import components.DrawMouse;

import java.awt.Font;
import java.awt.GridBagConstraints;

import utils.LoadImage;
import utils.UseButton;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

public class Developer extends JFrame {

        // Ref
        private DrawMouse drawMouse;
        private GameCenter gameCenter;

        public Developer(GameCenter gameCenter) {
                this.gameCenter = gameCenter;

                createFrame();
        }

        private void createFrame() {
                setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
                setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("Zombie Shooter - Developer");
                setLocationRelativeTo(null);

                JLayeredPane layers = new JLayeredPane();
                GridBagConstraints gridConst = new GridBagConstraints();

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

                // ==================== Content ====================

                gridConst.insets = new Insets(0, 50, 0, 0);

                JPanel content = new JPanel();
                content.setLayout(new GridBagLayout());

                content.add(new Card(
                                "Theeraphat Chueanokkhum",
                                "66011212103",
                                "resource/images/author/theeraphat.png"), gridConst);

                content.add(new Card(
                                "Boonisa Pitchawrong",
                                "66011212184",
                                "resource/images/author/boonisa.png"), gridConst);

                // ==================== Absolute Content ===================

                JPanel titleContent = new CoverTitle("", "");
                titleContent.setLayout(null);

                // ---------- Back ----------
                JButton back = new UseButton(32).createButtonAndChangePage(
                                "-",
                                "Back",
                                Color.WHITE,
                                200,
                                100,
                                "hand",
                                this,
                                () -> gameCenter);

                back.setBounds(10, (this.getHeight() - 100), 200, 50);
                titleContent.add(back);

                // ==================== Layers ====================

                backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
                layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

                titleContent.setBounds(0, 0, this.getWidth(), this.getHeight());
                titleContent.setOpaque(false);
                layers.add(titleContent, JLayeredPane.PALETTE_LAYER);

                content.setBounds(0, 0, this.getWidth(), this.getHeight());
                content.setOpaque(false);
                layers.add(content, JLayeredPane.MODAL_LAYER);

                drawMouse = new DrawMouse();
                drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
                layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

                setContentPane(layers);
                layers.revalidate();
                layers.repaint();

                new WindowResize().addWindowResize(this, new Component[] { backgroundPanel, drawMouse },
                                new Component[] { layers });
                new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        }
}
