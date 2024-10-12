package page.home;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import components.DrawMouse;
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

    // Ref
    private DrawMouse drawMouse;
    private GameCenter gameCenter;

    public Register() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Register");
        setLocationRelativeTo(null);

        setIconImage(new LoadImage().getImage("resource/images/icon/main.png"));

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

        gridConst.gridy = 1;

        field = new UseText(30, 400, 50, false).createTextField("", Color.white, true);
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

        // Start Button
        JButton start = new UseButton(32).createSimpleButton("Start", Color.WHITE, 400, 100, "hand");
        start.addActionListener((e -> {
            System.out.println("Navigate Work!");

            navigateTo();
        }));

        gridConst.fill = GridBagConstraints.CENTER;
        gridConst.gridy = 1;
        backgroundPanel.add(start, gridConst);

        // ==================== Layer ====================

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        // ==================== Event ====================

        new WindowClosingFrameEvent(this);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // # Key Input Event

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

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void navigateTo() {
        getDisplayName = new UseText().truncateText(field.getText().trim());
        isValidName = !getDisplayName.isEmpty();

        if (isValidName) {
            gameCenter = new GameCenter(getDisplayName, "192.168.0.0");
            UseGlobal.setName(getDisplayName);

            System.out.println(gameCenter);

            new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);
        } else {
            new useAlert().warringAlert("Please enter display name!");
        }
    }
}
