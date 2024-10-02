package page.home;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import utils.LoadImage;
import utils.UseButton;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.UseText;
import utils.WindowClosingFrameEvent;
import utils.useAlert;

public class Register extends JFrame {
    public Register() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Register");
        setLocationRelativeTo(null);

        GridBagConstraints gridConst = new GridBagConstraints();

        JTextField field;

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

        // Display name panel
        JPanel displayName = new JPanel();
        displayName.setLayout(new GridBagLayout());
        displayName.setOpaque(false);

        gridConst.anchor = GridBagConstraints.BASELINE;
        gridConst.insets = new Insets(0, 0, 30, 0);
        gridConst.weighty = 1;
        gridConst.gridheight = 1;

        // ==================== Display Name ====================

        JTextPane title = new UseText(28, 400, 50).createSimpleText("Enter display name: ", null, null, Font.BOLD);
        title.setPreferredSize(new Dimension(400, 30));

        displayName.add(title, gridConst);

        gridConst.gridy = 1;

        field = new UseText(30, 400, 50).createTextField("", Color.white, true);
        field.setPreferredSize(new Dimension(400, 50));

        displayName.add(field, gridConst);

        gridConst.gridx = 0;
        gridConst.gridy = 0;
        gridConst.anchor = GridBagConstraints.CENTER;

        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.weightx = 1.0;
        gridConst.weighty = 0;
        gridConst.insets = new Insets(0, 0, 50, 0);

        backgroundPanel.add(displayName, gridConst);

        // ==================== Start Button ====================

        JButton start = new UseButton(20).createSimpleButton(
                "Start",
                Color.WHITE,
                400,
                100,
                "hand");

        start.addActionListener((e -> {
            String getDisplayName = field.getText().trim();
            boolean isValidName = getDisplayName != null && !getDisplayName.isEmpty();

            if (isValidName) {
                GameCenter gameCenter = new GameCenter();
                this.dispose();
                gameCenter.setVisible(true);

            } else {
                new useAlert().warringAlert("Please enter display name!");
            }
        }));

        gridConst.fill = GridBagConstraints.CENTER;
        gridConst.gridy = 1;
        backgroundPanel.add(start, gridConst);

        // Parent Content
        setContentPane(backgroundPanel);

        new WindowClosingFrameEvent(this);
    }
}