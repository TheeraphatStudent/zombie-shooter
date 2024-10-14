package page.controls;

import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import models.Player;
import page.home.GameCenter;

import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseButton;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;

public class Scoreboard extends JFrame {
    private GameCenter gameCenter;
    private Player player;

    public Scoreboard(GameCenter gameCenter, Player player) {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Register");
        setLocationRelativeTo(null);

        setIconImage(new LoadImage().getImage("resource/images/icon/main.png"));
        this.gameCenter = gameCenter;
        this.player = player;

        System.out.println(player.getAliveTime());
        System.out.println(player.getZombieHunt());

        GridBagConstraints gridConst = new GridBagConstraints();
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

        // ==================== Action ====================

        JPanel displayName = new JPanel();
        displayName.setLayout(new GridBagLayout());
        displayName.setOpaque(false);

        gridConst.anchor = GridBagConstraints.BASELINE;
        gridConst.insets = new Insets(0, 0, 30, 0);
        gridConst.weighty = 1;
        gridConst.gridheight = 1;

        JTextPane title = new UseText(28, 400, 50, false).createSimpleText("Enter display name: ", null, null, Font.BOLD);
        title.setPreferredSize(new Dimension(400, 30));
        displayName.add(title, gridConst);

        gridConst.gridx = 0;
        gridConst.gridy = 0;
        gridConst.anchor = GridBagConstraints.EAST;
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.weightx = 1.0;
        gridConst.weighty = 0;
        gridConst.insets = new Insets(0, 0, 50, 0);

        backgroundPanel.add(displayName, gridConst);
        // ==================== Layer ====================

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);


        setContentPane(layers);
        layers.revalidate();
        layers.repaint();
    }

    @Override
    public void dispose() {
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);

        super.dispose();
    }
}
