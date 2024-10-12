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
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import components.DrawBulletLine;
import components.DrawMouse;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import components.objectElement.Bullet;
import page.home.GameCenter;

import utils.LoadImage;
import utils.UseCharacter;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;

interface GameContentProps {
    int MOVEMENT_SPEED = 10;

    int CREATE_ZOMBIES = 50;

    void mouseEvent(DrawMouse mouse);

    // Character control movement
    // boolean getCharacterMovement();

}

public class GameContent extends JFrame implements KeyListener, GameContentProps, ManageCharacterElement, Runnable {

    private JPanel content;

    private GameCenter gameCenter;
    private DrawMouse drawMouse;

    private CreateCharacter character;
    private ArrayList<CreateCharacter> zombies = new ArrayList<>();

    // Movement
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private Timer movementTimer;
    private Point mousePosition;

    // Bullet
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Timer bulletTimer;

    public GameContent(GameCenter gameCenter) {
        System.out.println("On Create Game Center");

        this.gameCenter = gameCenter;

        createFrame();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        initializeBulletTimer();

        new Thread(this).start();

    }

    // private void initializeMovement() {
    // System.out.println("Initialize Movement Work!");

    // movementTimer = new Timer(16, (e -> {
    // updateCharacterPosition();

    // }));
    // movementTimer.start();
    // }

    // private void movementzom() {
    // Timer zombieTimer = new Timer(16, (e -> {
    // updateZombies();
    // }));
    // zombieTimer.start();
    // }

    private void initializeZombieSpawner() {
        Timer spawner = new Timer(1500, e -> {
            if (zombies.size() < CREATE_ZOMBIES && zombies.size() < 3) {
                String[] types = { "normal", "fast", "slow" };
                String randomType = types[(int) (Math.random() * types.length)];

                addZombie(randomType);
            }
        });
        spawner.start();
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
    private void updateZombies() {
        for (CreateCharacter zombie : zombies) {
            // Get positions
            int playerX = character.getX() + (CHARACTER_WIDTH / 2);
            int playerY = character.getY() + (CHARACTER_HEIGHT / 2);
            int zombieX = zombie.getX() + (CHARACTER_WIDTH / 2);
            int zombieY = zombie.getY() + (CHARACTER_HEIGHT / 2);

            // Calculate direction to player
            double dx = playerX - zombieX;
            double dy = playerY - zombieY;

            // Calculate angle to player for zombie rotation
            double angle = Math.atan2(dy, dx);

            // Determine if zombie should face left or right
            boolean shouldFaceLeft = dx < 0;
            zombie.setCharacterMoveLeft(shouldFaceLeft);

            // Get zombie type and speed
            ZombieType zombieType = zombieTypes.get(zombie.getZombieType());
            double zombieSpeed = zombieType.speed; // Get speed based on type

            // Move zombie
            int newX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
            int newY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);

            // Optional: Add boundary checking
            newX = Math.max(0, Math.min(newX, getWidth() - CHARACTER_WIDTH));
            newY = Math.max(0, Math.min(newY, getHeight() - CHARACTER_HEIGHT));

            zombie.setLocation(newX, newY);
        }
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

        content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                // RenderingHints.VALUE_ANTIALIAS_ON);

                new DrawBulletLine(g2d, mousePosition, character);

                // Reset Stroke
                g2d.setStroke(new BasicStroke(4f));

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
        initializeZombieSpawner();

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
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();

            boolean bulletRemoved = false;

            Iterator<CreateCharacter> zombieIterator = zombies.iterator();
            while (zombieIterator.hasNext() && !bulletRemoved) {
                CreateCharacter zombie = zombieIterator.next();
                
                // Get the hitbox (blue border area) of the zombie
                Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
                
                // Check if the bullet intersects with the zombie's hitbox
                if (bullet.getBounds().intersects(zombieHitbox)) {
                    // Remove both bullet and zombie
                    bulletIterator.remove();
                    zombieIterator.remove();
                    content.remove(zombie);
            
                    bulletRemoved = true;
                    repaint();
                }
            }

            if (!bulletRemoved && bullet.isOutOfBounds(getWidth(), getHeight())) {
                bulletIterator.remove();
            }
        }
    }

    private void drawBullets(Graphics2D g2d) {
        for (Bullet bullet : bullets) {
            bullet.drawContent(g2d);

        }
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);

    }

    // ----*----*----*---- Dispose Content ----*----*----*----

    @Override
    public void dispose() {
        this.removeAll();

        bulletTimer.stop();
        movementTimer.stop();
        bullets.clear();

        new WindowClosingFrameEvent().navigateTo(this, new GameCenter(), false);

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

    public class ZombieType {
        double speed;
        double detectionRange;

        public ZombieType(double speed, double detectionRange) {
            this.speed = speed;
            this.detectionRange = detectionRange;
        }
    }

    private Map<String, ZombieType> zombieTypes = new HashMap<String, ZombieType>() {
        {
            put("normal", new ZombieType(5, 300));
            put("fast", new ZombieType(8, 250));
            put("slow", new ZombieType(3, 400));
        }
    };

    private class ZombieMovementThread extends Thread {
        private CreateCharacter zombie;
        private volatile boolean running = true; // Control the thread's execution

        public ZombieMovementThread(CreateCharacter zombie) {
            this.zombie = zombie;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    // Sleep to control zombie movement speed
                    Thread.sleep(16); // Adjust this value to control the refresh rate (60 FPS = 1000ms / 60 ≈ 16ms)

                    // Update the zombie's position towards the player
                    updateZombiePosition(zombie);

                    // Ensure the UI updates (invoke on the Swing Event Dispatch Thread)
                    SwingUtilities.invokeLater(() -> repaint());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void updateZombiePosition(CreateCharacter zombie) {
        // Get positions
        int playerX = character.getX() + (CHARACTER_WIDTH / 2); // Center of player
        int playerY = character.getY() + (CHARACTER_HEIGHT / 2);
        int zombieX = zombie.getX() + (CHARACTER_WIDTH / 2); // Center of zombie
        int zombieY = zombie.getY() + (CHARACTER_HEIGHT / 2);

        // Calculate direction to player
        double dx = playerX - zombieX;
        double dy = playerY - zombieY;

        // Calculate angle to player for zombie rotation
        double angle = Math.atan2(dy, dx);

        // Determine if zombie should face left or right
        boolean shouldFaceLeft = dx < 0;
        zombie.setCharacterMoveLeft(shouldFaceLeft);

        // Get zombie type and speed
        ZombieType zombieType = zombieTypes.get(zombie.getZombieType());
        double zombieSpeed = zombieType.speed; // Get speed based on type

        // Move zombie
        int newX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
        int newY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);

        // Optional: Add boundary checking
        newX = Math.max(0, Math.min(newX, getWidth() - CHARACTER_WIDTH));
        newY = Math.max(0, Math.min(newY, getHeight() - CHARACTER_HEIGHT));

        // Update the zombie's location
        zombie.setLocation(newX, newY);
    }

    // Method to add a zombie and start its movement
    private void addZombie(String type) {
        CreateCharacter zombie = new CreateCharacter(this.gameCenter, this, true);
        zombie.setZombieType(type);

        // Determine spawn position based on a random side
        int spawnSide = (int) (Math.random() * 4);
        int x = 0, y = 0;

        switch (spawnSide) {
            case 0: // Left
                x = -CHARACTER_WIDTH; // Place it just outside the left edge
                y = (int) (Math.random() * getHeight()); // Random vertical position
                break;
            case 1: // Right
                x = getWidth(); // Place it just outside the right edge
                y = (int) (Math.random() * getHeight()); // Random vertical position
                break;
            case 2: // Top
                x = (int) (Math.random() * getWidth()); // Random horizontal position
                y = -CHARACTER_HEIGHT; // Place it just outside the top edge
                break;
            case 3: // Bottom
                x = (int) (Math.random() * getWidth()); // Random horizontal position
                y = getHeight(); // Place it just outside the bottom edge
                break;
        }

        // Set bounds for the zombie
        zombie.setBounds(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        content.add(zombie);
        zombies.add(zombie);

        // Update the UI
        SwingUtilities.invokeLater(() -> {
            content.revalidate();
            content.repaint();
        });

        // Start the movement thread for this zombie
        new Thread(new ZombieMovementThread(zombie)).start(); // If you have zombie movement logic
    }

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

    @Override
    public void run() {
        while (true) {
            updateCharacterPosition();
            updateZombies();
            updateBullets();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}