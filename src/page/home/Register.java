package page.home;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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

public class Register extends JFrame implements KeyListener {

    private boolean isValidName = false;
    private JTextField field;
    private String getDisplayName;

    public Register() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Register");
        setLocationRelativeTo(null);

        GridBagConstraints gridConst = new GridBagConstraints();
        JLayeredPane layers = new JLayeredPane();

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

        // Add a KeyListener to field to update isValidName
        field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigateTo();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                getDisplayName = field.getText().trim();
                isValidName = !getDisplayName.isEmpty();
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        gridConst.gridx = 0;
        gridConst.gridy = 0;
        gridConst.anchor = GridBagConstraints.CENTER;
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.weightx = 1.0;
        gridConst.weighty = 0;
        gridConst.insets = new Insets(0, 0, 50, 0);

        backgroundPanel.add(displayName, gridConst);

        // ==================== Start Button ====================

        JButton start = new UseButton(32).createSimpleButton(
                "Start",
                Color.WHITE,
                400,
                100,
                "hand");

        start.addActionListener((e -> {
            navigateTo();
        }));

        gridConst.fill = GridBagConstraints.CENTER;
        gridConst.gridy = 1;
        backgroundPanel.add(start, gridConst);

        // Added Content On Layer
        
        backgroundPanel.setBounds(0, 0, UseGlobal.getWidth(), UseGlobal.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Parent Content
        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowClosingFrameEvent(this);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new WindowClosingFrameEvent().closePage(this);
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            navigateTo();
        }
    }

    private void navigateTo() {
        getDisplayName = field.getText().trim();
        isValidName = !getDisplayName.isEmpty();

        if (isValidName) {
            GameCenter gameCenter = new GameCenter();

            gameCenter.setDisplayName(getDisplayName);
            UseGlobal.setName(getDisplayName);
            // UseGlobal.printState();

            new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);

        } else {
            new useAlert().warringAlert("Please enter display name!");

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
