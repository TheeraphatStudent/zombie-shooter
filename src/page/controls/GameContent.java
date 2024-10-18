package page.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import components.DrawBulletLine;
import components.DrawMouse;
import components.LevelState;
import components.Scoreboard;
import components.Sumstat;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import components.objectElement.Bullet;
import components.Cover;
import models.ClientObj;
import models.Player;
import models.Zombie;
import models.State;

import page.home.GameCenter;

import utils.LoadImage;
import utils.UseCharacter;
import utils.LoadImage.BackgroundPanel;
import utils.UseGlobal;
import utils.WindowClosingFrameEvent;
import utils.WindowResize;

interface GameContentProps {
    void mouseEvent(DrawMouse mouse);

    void addBullet(Bullet bullet);

    void updateGameState();

    // boolean getCharacterMovement();

}

public class GameContent extends JFrame implements KeyListener, GameContentProps, ManageCharacterElement, Runnable {
    ClientObj client;

    // Game State
    private State state;

    private JPanel content;
    private JLayeredPane layers;
    private Cover backgroundCover;

    // Stat
    private LevelState levelState;
    private Scoreboard scoreboard;

    private GameCenter gameCenter;
    private DrawMouse drawMouse;

    // Character Content
    private CreateCharacter character;
    private Player player;

    private ArrayList<CreateCharacter> zombies = new ArrayList<>();
    private ArrayList<ZombieMovementThread> zombieThreads = new ArrayList<>();
    private Timer spawner;
    private volatile int ZOMBIE_REMAIN;

    // Movement
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private Point mousePosition;

    // Bullet
    private CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();

    // Thread
    private Thread bulletThread;
    private Thread characterThread;

    public GameContent(GameCenter gameCenter, ClientObj client) {
        this.client = client;

        System.out.println("On Create Game Center");

        this.gameCenter = gameCenter;
        state = new State();

        createFrame();

        updateGameState();
        updatePlayerStat();

        // ปิดการปรับขนาดจอ
        // setUndecorated(true);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);

        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        layers.revalidate();
        layers.repaint();

        new Thread(this).start();

    }

    // ==================== Game State ====================

    public void updateGameState() {
        state.setStateLevel(1);

        ZOMBIE_REMAIN = state.getMaxZombie();

        updateLevelScoreboard();

    }

    private void updateLevelScoreboard() {
        levelState.setLevelState(state.getLevelState());
        levelState.setZombieOnState(state.getMaxZombie());
        levelState.setZombieRemain(ZOMBIE_REMAIN);

        state.setZombieRemain(ZOMBIE_REMAIN);

    }

    private void updatePlayerStat() {
        // Killed Stat
        scoreboard.setKilled(player.getZombieHunt());
        scoreboard.setNeededKilled(player.getStoreZombieHunt());
        scoreboard.setMaxZombie(player.getRankUpKillZombieNeeded());

        scoreboard.setRank(player.getRank());

    }

    private void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Zombie Shooter - Let's Survive");
        setLocationRelativeTo(null);

        layers = new JLayeredPane();

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

        // ==================== Create Player Character ====================

        character = new CreateCharacter(false, client);
        character.setGameContent(this);

        player = new Player(character, state);

        // ค่าเริ่มต้นเมื่อผู้เล่นเกิดมาครั้งแรก

        int bulletDamage = 10;
        int playerHealth = 100;

        player.setPlayerBulletDamage(bulletDamage);
        player.setPlayerHealth(playerHealth);

        character.setCharacterHp(100);

        // # Set Character To Center
        character.setBounds(this.getWidth() / 2 - 100, this.getHeight() / 2 - 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        content.add(character);

        // ! Add Zombie
        initializeZombieSpawner();

        // ==================== Layer ====================

        backgroundPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        content.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(content, JLayeredPane.PALETTE_LAYER);

        // Scoreboard
        JPanel score = new JPanel();
        score.setLayout(new GridBagLayout());
        score.setOpaque(false);

        GridBagConstraints gridConst = new GridBagConstraints();

        gridConst.gridx = 0;
        gridConst.gridy = 0;
        gridConst.weightx = 1;
        gridConst.weighty = 1;
        gridConst.insets = new Insets(15, 15, 0, 0);

        gridConst.anchor = GridBagConstraints.NORTHWEST;

        scoreboard = new Scoreboard();
        score.add(scoreboard, gridConst);

        gridConst.insets = new Insets(15, 0, 0, 0);
        gridConst.anchor = GridBagConstraints.NORTH;

        levelState = new LevelState();
        levelState.setBackground(Color.GREEN);

        score.add(levelState, gridConst);

        score.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(score, JLayeredPane.MODAL_LAYER);

        backgroundCover = new Cover();
        backgroundCover.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(backgroundCover, JLayeredPane.POPUP_LAYER);

        drawMouse = new DrawMouse();
        drawMouse.setBounds(0, 0, this.getWidth(), this.getHeight());
        layers.add(drawMouse, JLayeredPane.DRAG_LAYER);

        this.mouseEvent(drawMouse);

        setContentPane(layers);

        new WindowResize().addWindowResize(this,
                new Component[] { backgroundPanel, backgroundCover, content, score, drawMouse },
                new Component[] { layers });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    // ----*----*----*---- Bullet Management ----*----*----*----

    private void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.move();

            boolean bulletRemoved = false;

            Iterator<CreateCharacter> zombieContain = zombies.iterator();
            while (zombieContain.hasNext() && !bulletRemoved) {
                CreateCharacter zombie = zombieContain.next();
                Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);

                if (bullet.getBounds().intersects(zombieHitbox)) {
                    // Remove bullet
                    bullets.remove(bullet);

                    zombie.setCharacterHp(zombie.getCharacterHp() - player.getPlayerBulletDamage());

                    if (zombie.getCharacterHp() <= 0) {
                        for (ZombieMovementThread thread : zombieThreads) {
                            if (thread.zombie == zombie) {
                                thread.stopMovement();
                                break;
                            }
                        }

                        player.addZombieWasKilled(1);
                        scoreboard.setKilled(player.getZombieHunt());

                        zombieContain.remove();
                        content.remove(zombie);

                        this.ZOMBIE_REMAIN = ZOMBIE_REMAIN - 1;
                        if (ZOMBIE_REMAIN <= 0) {
                            updateGameState();
                        }

                        updateLevelScoreboard();
                        updatePlayerStat();
                        revalidateContent();
                    }

                    bulletRemoved = true;
                }
            }

            if (!bulletRemoved && bullet.isOutOfBounds(getWidth(), getHeight())) {
                bullets.remove(bullet);
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

    public void disposeContent() {
        bullets.clear();
        zombies.clear();
        character.removeAll();
        stopAllZombies();

        spawner.stop();
        player.onGameFinish();

    }

    @Override
    public void dispose() {
        disposeContent();

        this.removeAll();
        new WindowClosingFrameEvent().navigateTo(this, gameCenter, false);

        super.dispose();
    }

    // ----*----*----*---- Player Control - Key Input ----*----*----*----

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
            if (zombies.size() >= 4) {
                return;

            }

            if (zombies.size() < state.getZombieRemain()) {
                String[] types = { "normal", "fast", "slow" };
                String randomType = types[(int) (Math.random() * types.length)];

                addZombie(randomType);
            }
        });
        spawner.start();
    }

    // ! ซอมบี้เดิน
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

                    // behavior = new Zombie(character, zombie, GameContent.this,nul);
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

                    backgroundCover.setCoverBackground(0.5f);
                    disposeContent();
                    // dispose();

                }
            }
        }

        public void stopMovement() {
            running = false;
        }
    }

    // ! เพิ่ม ซอมบี้เข้า Frame
    private void addZombie(String type) {
        System.out.println("Level State: " + state.getLevelState());

        CreateCharacter zombie = new CreateCharacter(this);
        Zombie zombieBehavior = new Zombie(character, zombie, this, state, type);

        zombie.setMaxCharacterHp((int) zombieBehavior.getZombieHealth());

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
        zombie.setCharacterHp((int) zombieBehavior.getZombieHealth());

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
        while (character.getCharacterHp() > 0) {

            // ใช้ Thread A เพื่อควบคุมกระสุน
            bulletThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateBullets();

                }

            });

            bulletThread.start();

            // ใช้ Thread B เพื่อควบคุมการเดินของผู้เล่น
            characterThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateCharacterPosition();

                }

            });

            characterThread.start();

            revalidateContent();

            try {
                Thread.sleep(16);

            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        drawMouse.add(new Sumstat(this, this.gameCenter, true, player, client));
        drawMouse.revalidate();
        drawMouse.repaint();

    }
}