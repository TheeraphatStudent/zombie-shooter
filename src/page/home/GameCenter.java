package page.home;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseButton;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;

public class GameCenter extends JFrame {

        private String name;

        public GameCenter() {
                setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
                setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setTitle("Zombie Shooter - Welcome");
                setLocationRelativeTo(null);

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

                // ##### NAME #####
                JTextPane title_name = new UseText(24, 400, 50)
                                .createSimpleText("Name: " + name, null, null, Font.BOLD);

                // backgroundPanel.add(title_name, gridConst);

                // ##### IP #####
                JTextPane title_ip = new UseText(24, 400, 50)
                                .createSimpleText("IP: 192.168.0.0", null, null, Font.BOLD);

                // gridConst.gridy = 1;
                // backgroundPanel.add(title_ip, gridConst);

                // ==================== Actions ====================
                // ##### Game Title #####
                JButton gameTItle = new UseButton(32).createSimpleButton(
                                "Zombie Runner",
                                Color.WHITE,
                                400,
                                100,
                                "default");

                gameTItle.setEnabled(false);

                gridConst.gridx = 0;
                gridConst.gridy = 1;
                gridConst.fill = GridBagConstraints.HORIZONTAL;
                gridConst.weighty = 0;
                gridConst.insets = new Insets(0, 0, 100, 0);
                backgroundPanel.add(gameTItle, gridConst);

                JButton singlePlay = new UseButton(32).createButtonAndChangePage(
                                "",
                                "Solo Player",
                                Color.WHITE,
                                400,
                                100,
                                "hand",
                                this,
                                "content");

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
                                "create");

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
                                "dev");

                gridConst.gridy = 4;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(developer, gridConst);

                JButton exit = new UseButton(32).createButtonAndChangePage(
                                "",
                                "Exit",
                                Color.decode("#FFB0B0"),
                                400,
                                100,
                                "hand",
                                this,
                                "exit");

                gridConst.gridy = 5;
                gridConst.insets = new Insets(0, 0, 15, 0);
                backgroundPanel.add(exit, gridConst);

                // Parent Content
                setContentPane(backgroundPanel);

                new WindowClosingFrameEvent(this);
        }

        public void setDisplayName(String getName) {
                this.name = getName;

        }

}
