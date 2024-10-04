package page.controls;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import components.CreateCharacter;
import components.DrawMouse;
import page.home.GameCenter;
import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

interface GameContentProps {
    int MOVEMENT_SPEED = 5;

}

public class GameContent extends JFrame implements KeyListener, GameContentProps {

    private GameCenter gameCenter;
    private DrawMouse drawMouse;
    private CreateCharacter character;
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private Timer movementTimer;

    public GameContent(GameCenter gameCenter) {
        this.gameCenter = gameCenter;
        createFrame();
        initializeMovement();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

    }

    private void initializeMovement() {
        movementTimer = new Timer(16, (e -> {
            updateCharacterPosition();

        }));
        movementTimer.start();
    }

    private void updateCharacterPosition() {
        int currentX = character.getX();
        int currentY = character.getY();
        boolean moved = false;

        if (isUpPressed && currentY - MOVEMENT_SPEED >= 0) {
            currentY -= MOVEMENT_SPEED;
            moved = true;
        }
        if (isDownPressed && currentY + MOVEMENT_SPEED + character.getHeight() <= getHeight()) {
            currentY += MOVEMENT_SPEED;
            moved = true;
        }
        if (isLeftPressed && currentX - MOVEMENT_SPEED >= 0) {
            currentX -= MOVEMENT_SPEED;
            moved = true;
        }
        if (isRightPressed && currentX + MOVEMENT_SPEED + character.getWidth() <= getWidth()) {
            currentX += MOVEMENT_SPEED;
            moved = true;
        }

        if (moved) {
            character.setLocation(currentX, currentY);
            repaint();
        }
    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Let's Survive");
        setLocationRelativeTo(null);

        JLayeredPane layers = new JLayeredPane();

        // ==================== Background ====================

        // Background Image
        String backgroundPath = "resource/images/background/plain.png";
        BackgroundPanel backgroundPanel = new LoadImage.BackgroundPanel(
                backgroundPath,
                this.getWidth(),
                this.getHeight(),
                1,
                0,
                false);

        backgroundPanel.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setLayout(null);

        // ==================== Create Character ====================

        character = new CreateCharacter(this.gameCenter);

        // # Set Character To Center
        character.setBounds(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100, 200, 200);
        content.add(character);

        // ==================== Layer ====================

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        content.setBounds(0, 0, this.getWidth(), this.getHeight());
        content.setOpaque(false);
        layers.add(content, JLayeredPane.PALETTE_LAYER);

        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                isUpPressed = true;
                break;
            case KeyEvent.VK_S:
                isDownPressed = true;
                break;
            case KeyEvent.VK_A:
                isLeftPressed = true;
                break;
            case KeyEvent.VK_D:
                isRightPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                isUpPressed = false;
                break;
            case KeyEvent.VK_S:
                isDownPressed = false;
                break;
            case KeyEvent.VK_A:
                isLeftPressed = false;
                break;
            case KeyEvent.VK_D:
                isRightPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void dispose() {
        movementTimer.stop();
        super.dispose();
    }
}