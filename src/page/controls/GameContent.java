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
import models.Player;
import models.Zombie;
import page.home.GameCenter;
import types.ZombieType;
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
    private Player player;

    private ArrayList<CreateCharacter> zombies = new ArrayList<>();
    private ArrayList<ZombieMovementThread> zombieThreads = new ArrayList<>();
    private Timer spawner;

    // Movement
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private Point mousePosition;

    // Bullet
    private ArrayList<Bullet> bullets = new ArrayList<>();

    public GameContent(GameCenter gameCenter) {
        System.out.println("On Create Game Center");

        this.gameCenter = gameCenter;

        createFrame();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        new Thread(this).start();

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
        player = new Player(character);

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

    private void updateBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();

            boolean bulletRemoved = false;

            Iterator<CreateCharacter> zombieContain = zombies.iterator();
            while (zombieContain.hasNext() && !bulletRemoved) {
                CreateCharacter zombie = zombieContain.next();
                Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);

                if (bullet.getBounds().intersects(zombieHitbox)) {
                    // Remove both bullet and zombie
                    bulletIterator.remove();

                    zombie.setCharacterHp(zombie.getCharacterHp() - 20);

                    if (zombie.getCharacterHp() <= 0) {
                        for (ZombieMovementThread thread : zombieThreads) {
                            if (thread.zombie == zombie) {
                                thread.stopMovement();
                                break;
                            }
                        }

                        player.addZombieWasKilled(1);

                        zombieContain.remove();
                        content.remove(zombie);
                        revalidateContent();

                    }

                    bulletRemoved = true;
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
        bullets.clear();
        zombies.clear();
        character.removeAll();
        stopAllZombies();

        spawner.stop();
        player.onGameFinish();

        new WindowClosingFrameEvent().navigateTo(this, new Scoreboard(gameCenter, player), false);

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

    private void initializeZombieSpawner() {
        spawner = new Timer(1500, e -> {
            if (zombies.size() < CREATE_ZOMBIES && zombies.size() <= 5) {
                String[] types = { "normal", "fast", "slow" };
                String randomType = types[(int) (Math.random() * types.length)];

                addZombie(randomType);
            }
        });
        spawner.start();
    }

    //! ซอมบี้เดิน
    private class ZombieMovementThread extends Thread {
        private Zombie behavior;
        private CreateCharacter zombie;

        private CreateCharacter player;

        private volatile boolean running = true;
        private volatile Timer biteTimer;
        private volatile boolean isBiting = false;

        public ZombieMovementThread(CreateCharacter zombie, Zombie behavior, CreateCharacter player) {
            this.zombie = zombie;
            this.behavior = behavior;
            this.player = player;

            biteTimer = new Timer(1000, e -> biteInArea());
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(16);

                    behavior = new Zombie(character, zombie, GameContent.this);
                    behavior.updateZombiePosition();

                    if (!isBiting && isPlayerInRange()) {
                        isBiting = true;
                        biteInArea();

                        biteTimer.start();

                        if (isBiting && !isPlayerInRange()) {
                            isBiting = false;
                            biteTimer.stop();

                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    running = false;

                }
            }
            biteTimer.stop();
        }

        private boolean isPlayerInRange() {
            Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
            Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(player);
            return zombieHitbox.intersects(playerHitbox);
        }

        private void biteInArea() {
            if (isPlayerInRange()) {
                System.out.println("Bite!");

                player.setCharacterHp(player.getCharacterHp() - (int) behavior.getZombieDamage());
                if (player.getCharacterHp() <= 0) {
                    System.out.println("Player is dead!");

                    dispose();

                }
            }
        }

        public void stopMovement() {
            running = false;
        }
    }

    //! เพิ่ม ซอมบี้เข้า Frame
    private void addZombie(String type) {
        CreateCharacter zombie = new CreateCharacter(this.gameCenter, this);
        zombie.setZombieType(type);

        Zombie zombieBehavior = new Zombie(character, zombie, this);
        zombie.setCharacterHp((int) zombieBehavior.getZombieHealth());

        int spawnSide = (int) (Math.random() * 4);
        int x = 0, y = 0;

        switch (spawnSide) {
            case 0: // Left
                x = -CHARACTER_WIDTH;
                y = (int) (Math.random() * getHeight());
                break;

            case 1: // Right
                x = getWidth();
                y = (int) (Math.random() * getHeight());
                break;

            case 2: // Top
                x = (int) (Math.random() * getWidth());
                y = -CHARACTER_HEIGHT;
                break;

            case 3: // Bottom
                x = (int) (Math.random() * getWidth());
                y = getHeight();
                break;
        }

        zombie.setBounds(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        content.add(zombie);
        zombies.add(zombie);

        ZombieMovementThread zombieThread = new ZombieMovementThread(zombie, zombieBehavior, character);
        zombieThread.start();

        zombieThreads.add(zombieThread);
    }

    private void stopAllZombies() {
        for (ZombieMovementThread thread : zombieThreads) {
            thread.stopMovement();
        }
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

    }

    private void revalidateContent() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                content.revalidate();
                content.repaint();

            }
        });

    }

    @Override
    public void run() {
        while (true) {

            // ใช้ Thread A เพื่อควบคุมกระสุน
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateBullets();

                }

            }).start();

            // ใช้ Thread B เพื่อควบคุมการเดินของผู้เล่น
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateCharacterPosition();

                }

            }).start();

            revalidateContent();

            try {
                Thread.sleep(12);

            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }
}