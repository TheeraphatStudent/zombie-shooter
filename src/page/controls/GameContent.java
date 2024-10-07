package page.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import components.DrawMouse;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import components.objectElement.Bullet;
import page.home.GameCenter;

import utils.LoadImage;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

interface GameContentProps {
    int MOVEMENT_SPEED = 10;

    void mouseEvent(DrawMouse mouse);

    // Character control movement
    // boolean getCharacterMovement();

}

public class GameContent extends JFrame implements KeyListener, GameContentProps, ManageCharacterElement {

    private GameCenter gameCenter;
    private DrawMouse drawMouse;
    private CreateCharacter character;
    private CreateCharacter zombie;

    // Movement
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private Timer movementTimer;
    private Point mousePosition;

    // Bullet
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Timer bulletTimer;

    public GameContent(GameCenter gameCenter) {
        this.gameCenter = gameCenter;

        createFrame();
        initializeMovement();
        initializeBulletTimer();
        movementzom();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

    }

    private void initializeMovement() {
        System.out.println("Initialize Movement Work!");

        movementTimer = new Timer(16, (e -> {
            updateCharacterPosition();

        }));
        movementTimer.start();
    }

    private void movementzom() {
        System.out.println("Initialize Movement Work!");

        movementTimer = new Timer(16, (e -> {
            updatezombie();

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

    // !ซอมบี้เดิน
    private void updatezombie() {
        int currentXzom = zombie.getX();
        int currentYzom = zombie.getY();

        zombie.setLocation(currentXzom + 5, currentYzom + 5);
        repaint();
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

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                // RenderingHints.VALUE_ANTIALIAS_ON);

                drawBulletLine(g2d);
                drawBullets(g2d);
            }
        };

        content.setLayout(null);
        content.setOpaque(false);

        // ==================== Create Character ====================

        character = new CreateCharacter(this.gameCenter, this, false);

        // # Set Character To Center
        character.setBounds(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        content.add(character);

        // ! Add Zombie
        zombie = new CreateCharacter(this.gameCenter, this);
        zombie.setBounds(100, 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        repaint();
        content.add(zombie);

        // ==================== Layer ====================

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        content.setBounds(0, 0, this.getWidth(), this.getHeight());
        content.setOpaque(false);
        layers.add(content, JLayeredPane.PALETTE_LAYER);

        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        this.mouseEvent(drawMouse);

        setContentPane(layers);
        layers.revalidate();
        layers.repaint();

        new WindowClosingFrameEvent().navigateTo(this, gameCenter, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    // ----*----*----*---- Bullet Management ----*----*----*----

    private void initializeBulletTimer() {
        bulletTimer = new Timer(16, e -> {
            updateBullets();
            repaint();

        });
        bulletTimer.start();
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();

            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }
        }
    }

    private void drawBullets(Graphics2D g2d) {
        for (Bullet bullet : bullets) {
            bullet.drawContent(g2d);

        }
    }

    private void drawBulletLine(Graphics2D g2d) {
        final int BULLET_LINE_LENGTH = 200;

        if (mousePosition == null) {
            return;

        }

        // Start Position
        int weaponSpinX = character.getX() + 25;
        int weaponSpinY = character.getY() + 100;

        double deltaX = mousePosition.x - weaponSpinX;
        double deltaY = mousePosition.y - weaponSpinY;
        double angle = Math.atan2(deltaY, deltaX);

        int endX = weaponSpinX + (int) (Math.cos(angle) * BULLET_LINE_LENGTH);
        int endY = weaponSpinY + (int) (Math.sin(angle) * BULLET_LINE_LENGTH);

        // วาดเส้นประ
        float[] dashPattern = { 10f, 10f };
        Stroke dashedStroke = new BasicStroke(4f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10f, dashPattern, 0f);

        g2d.setStroke(dashedStroke);
        g2d.setColor(Color.RED);
        g2d.drawLine(weaponSpinX, weaponSpinY, endX, endY);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);

    }

    // ----*----*----*---- Dispose Content ----*----*----*----

    @Override
    public void dispose() {
        bulletTimer.stop();
        movementTimer.stop();
        bullets.clear();

        super.dispose();
    }

    // ----*----*----*---- Player Control - Key Input ----*----*----*----

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
                character.setCharacterMoveLeft(isLeftPressed);

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
                character.setCharacterMoveLeft(isLeftPressed);

                break;
            case KeyEvent.VK_D:
                isRightPressed = false;

                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // ! ----*----*----*---- Zombie Control ----*----*----*----

    // ----*----*----*---- Mouse ----*----*----*----

    public void mouseEvent(DrawMouse mouse) {

        /*
         * ==========================================
         * Mouse Moved
         * ==========================================
         */

        mouse.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                character.updateWeaponAngle(e.getPoint());

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // System.out.println("============================");
                // System.out.println("Is Moved");

                // System.out.println(e.getX());
                // System.out.println(e.getY());

                // System.out.println("============================");

                character.updateWeaponAngle(e.getPoint());
                updateMousePosition(e.getPoint());

            }

        });

        /*
         * ==========================================
         * Mouse Clicked
         * ==========================================
         */

        mouse.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                // System.out.println("+++++++++++++++++++++++++++++");
                // System.out.println("Is Clicked");

                // System.out.println(e.getX());
                // System.out.println(e.getY());

                // System.out.println("+++++++++++++++++++++++++++++");

            };

            public void mousePressed(MouseEvent e) {
                // System.out.println("+++++++++++++++++++++++++++++");
                // System.out.println("Is Pressed");

                // System.out.println(e.getX());
                // System.out.println(e.getY());

                // System.out.println("+++++++++++++++++++++++++++++");

                // System.out.println("On Shoot");
                character.updateWeaponAngle(e.getPoint());
                character.onShootBullet(e.getPoint());

            };

            public void mouseReleased(MouseEvent e) {

            };

            public void mouseEntered(MouseEvent e) {

            };

            public void mouseExited(MouseEvent e) {

            };

        });

    }

    private void updateMousePosition(Point point) {
        mousePosition = point;
        character.updateWeaponAngle(point);
        repaint();
    }
}