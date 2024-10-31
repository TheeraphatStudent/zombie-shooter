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
import java.io.Serializable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import client.Client;

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
import components.Cover;

import models.Bullet;
import models.ClientObj;
import models.Player;
import models.Zombie;
import models.State;
import page.controls.multiplayer.PlayerBehaviorListener;
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

    void disposeContent();

    final static int MAX_ZOMBIES_FRAME = 4;

}

public class GameContent extends JFrame implements KeyListener, GameContentProps, ManageCharacterElement, Runnable {
    // Player Listener
    private List<PlayerBehaviorListener> playerListener = new ArrayList<>();

    // ? Multiplayer
    private boolean isHost = false;

    // private Client clientConnect;
    private boolean onMultiplayerMode = false;
    private List<ClientObj> clientObjs;
    private List<Player> players;
    private List<CreateCharacter> characters;

    public ClientObj parentClient;

    // Game State
    protected State state;

    protected JPanel content;
    protected JLayeredPane layers;
    protected Cover backgroundCover;

    // Stat
    protected LevelState levelState;
    protected Scoreboard scoreboard;

    private GameCenter gameCenter;
    protected DrawMouse drawMouse;

    // Character Content
    protected CreateCharacter character;
    protected Player player;

    // ! Zombie
    private ArrayList<CreateCharacter> zombiesCharacters;
    private List<Zombie> zombies;
    private ArrayList<ZombieMovementThread> zombieMoveThreads;
    private Timer spawner;
    private volatile int ZOMBIE_REMAIN;

    // Movement
    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    public Point mousePosition;

    // Bullet
    protected CopyOnWriteArrayList<Bullet> bullets;

    // Thread
    private Thread bulletThread;
    private Thread characterThread;

    public GameContent(GameCenter gameCenter, ClientObj client) {
        this.parentClient = client;

        System.out.println("On Create Game Center");

        this.gameCenter = gameCenter;
        this.state = new State();

        this.clientObjs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.characters = new ArrayList<>();

        this.zombiesCharacters = new ArrayList<>();
        this.zombies = new ArrayList<>();
        this.zombieMoveThreads = new ArrayList<>();
        this.bullets = new CopyOnWriteArrayList<>();

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

    public void createFrame() {
        setSize(new Dimension(UseGlobal.getWidth(), UseGlobal.getHeight()));
        setMinimumSize(new Dimension(UseGlobal.getMinWidth(), UseGlobal.getHeight()));

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

        player = this.parentClient.getPlayer();

        // character = new CreateCharacter(false, this.parentClient);
        // character = this.parentClient.getPlayer().getCharacter();

        character = new CreateCharacter(player.getCharacterNo(), false, parentClient);
        character.setBounds(player.getDirectionX(), player.getDirectionY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);

        character.setGameContent(this);

        player.setState(state);

        // ค่าเริ่มต้นเมื่อผู้เล่นเกิดมาครั้งแรก
        int bulletDamage = 10;
        int playerHealth = 100;

        player.setPlayerBulletDamage(bulletDamage);
        player.setPlayerHealth(playerHealth);

        character.setCharacterHp(player.getPlayerHealth());
        content.add(character);

        // CreateCharacter anotherPlayer = new CreateCharacter(false, client);
        // anotherPlayer.setBounds(100, 100, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        // content.add(anotherPlayer);

        // ! Add Zombie
        initializeZombieSpawner();
        // SwingUtilities.invokeLater(this::initializeZombieSpawner);

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

    // Multiplayer Mode Content
    protected void activeMultiplayerMode(List<ClientObj> clientObjs) {
        this.clientObjs.addAll(clientObjs);
        System.out.println(clientObjs);

        for (ClientObj getObj : this.clientObjs) {
            Player player = getObj.getPlayer();
            this.players.add(player);

        }

        for (int i = 0; i < players.size(); i++) {
            ClientObj useClientObj = this.clientObjs.get(i);
            Player usePlayer = this.players.get(i);

            System.out.printf("Player: x=%d | y=%d", player.getDirectionX(), player.getDirectionY());

            CreateCharacter character = new CreateCharacter(
                    usePlayer.getCharacterNo(),
                    usePlayer.getInfectedStatus(),
                    useClientObj);
            this.characters.add(character);

        }

        this.onMultiplayerMode = true;

    }

    // ----*----*----*---- Bullet Management ----*----*----*----

    protected void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.move();

            boolean bulletRemoved = false;

            Iterator<CreateCharacter> zombieContain = zombiesCharacters.iterator();
            while (zombieContain.hasNext() && !bulletRemoved) {
                CreateCharacter zombie = zombieContain.next();
                Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);

                if (bullet.getBounds().intersects(zombieHitbox)) {

                    Player shottedPlayer = bullet.getPlayer();

                    // Remove bullet
                    bullets.remove(bullet);

                    zombie.setCharacterHp(zombie.getCharacterHp() - shottedPlayer.getPlayerBulletDamage());

                    if (zombie.getCharacterHp() <= 0) {
                        for (ZombieMovementThread thread : zombieMoveThreads) {
                            if (thread.getZombie() == zombie) {
                                thread.stopMovement();
                                break;
                            }
                        }

                        shottedPlayer.addZombieWasKilled(1);
                        scoreboard.setKilled(shottedPlayer.getZombieHunt());

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

    public void drawBullets(Graphics2D g2d) {
        for (Bullet bullet : bullets) {
            bullet.drawContent(g2d);

        }
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);

        // onPlayerShootBullets(bullets);

    }

    // ----*----*----*---- Dispose Content ----*----*----*----

    public void disposeContent() {
        backgroundCover.setCoverBackground(0.5f);

        bullets.clear();
        zombiesCharacters.clear();
        character.removeAll();

        spawner.stop();
        zombieMoveThreads.forEach(ZombieMovementThread::stopMovement);
        player.onGameFinish();

    }

    @Override
    public void dispose() {
        System.out.println("Dispose Work!");
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
        if (isDownPressed && currentY + MOVEMENT_SPEED + CHARACTER_HEIGHT <= getHeight()) {
            currentY += MOVEMENT_SPEED;
            moved = true;
        }
        if (isLeftPressed && currentX - MOVEMENT_SPEED >= 0) {
            currentX -= MOVEMENT_SPEED;
            moved = true;
        }
        if (isRightPressed && currentX + MOVEMENT_SPEED + CHARACTER_WIDTH <= getWidth()) {
            currentX += MOVEMENT_SPEED;
            moved = true;
        }

        final int moveX = currentX;
        final int moveY = currentY;

        if (moved) {
            SwingUtilities.invokeLater(() -> {
                player.setPlayerLocation(moveX, moveY);
                character.setLocation(player.getDirectionX(), player.getDirectionY());

                onPlayerActions(player);
            });
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
                player.setIsPlayerMoveLeft(isLeftPressed);
                character.setCharacterMoveLeft(player.getPlayerIsMovedLeft());
                onPlayerActions(this.player);

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
                player.setIsPlayerMoveLeft(isLeftPressed);
                character.setCharacterMoveLeft(player.getPlayerIsMovedLeft());
                onPlayerActions(this.player);

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

    protected void initializeZombieSpawner() {
        spawner = new Timer(1500, e -> {
            if (zombiesCharacters.size() >= 4) {
                return;

            }

            if (zombiesCharacters.size() < state.getZombieRemain()) {
                String[] types = { "normal", "fast", "slow" };
                String randomType = types[(int) (Math.random() * types.length)];

                addZombie(randomType);
            }
        });

        spawner.start();
    }

    // ! เพิ่ม ซอมบี้เข้า Frame
    private void addZombie(String type) {
        // System.out.println("Level State: " + state.getLevelState());

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
        zombiesCharacters.add(zombie);

        ZombieMovementThread zombieThread = new ZombieMovementThread(zombie, zombieBehavior, player, character, this);
        zombieThread.start();

        zombieMoveThreads.add(zombieThread);

        // if (!this.onMultiplayerMode) {
        // ZombieMovementThread zombieThread = new ZombieMovementThread(zombie,
        // zombieBehavior, player, character, this);
        // zombieThread.start();

        // zombieMoveThreads.add(zombieThread);

        // } else {
        // for (int i = 0; i < players.size(); i++) {
        // ClientObj useClientObj = this.clientObjs.get(i);
        // Player usePlayer = this.players.get(i);

        // System.out.printf("Player: x=%d | y=%d", player.getDirectionX(),
        // player.getDirectionY());

        // CreateCharacter character = new CreateCharacter(usePlayer.getCharacterNo(),
        // usePlayer.getInfectedStatus(), useClientObj);
        // this.characters.add(character);

        // }

        // ZombieMovementThread zombieThread = new ZombieMovementThread(zombie,
        // zombieBehavior, players, characters, this);
        // zombieThread.start();

        // zombieMoveThreads.add(zombieThread);

        // }
    }

    public List<Zombie> getZombies() {
        return this.zombies;

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
                // character.updateWeaponAngle(e.getPoint());
                player.setWeaponPoint(e.getPoint());
                updateMousePosition();

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                player.setWeaponPoint(e.getPoint());
                updateMousePosition();

            }

        });

        /*
         * ==========================================
         * =========== Mouse Clicked ===========
         * ==========================================
         */

        mouse.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            };

            public void mousePressed(MouseEvent e) {
                // character.updateWeaponAngle(e.getPoint());
                player.setWeaponPoint(e.getPoint());
                updateMousePosition();

                character.onShootBullet(e.getPoint());
                onPlayerShootBullets(GameContent.this.bullets);

            };

            public void mouseReleased(MouseEvent e) {
            };

            public void mouseEntered(MouseEvent e) {
            };

            public void mouseExited(MouseEvent e) {
            };

        });

    }

    private void updateMousePosition() {
        mousePosition = player.getWeaponPoint();
        character.updateWeaponAngle(player.getWeaponPoint());

    }

    public void revalidateContent() {
        SwingUtilities.invokeLater(this::repaint);

    }

    public ClientObj getClientObjParent() {
        return this.parentClient;

    }

    public CopyOnWriteArrayList<Bullet> getBullets() {
        return this.bullets;

    }

    @Override
    public void run() {
        System.out.println("*/*/*/*/ ! On Game Thread Start ! /*/*/*/*");

        while (this.player.getPlayerHealth() > 0) {

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

        drawMouse.add(new Sumstat(this, this.gameCenter, true, player, this.parentClient));
        drawMouse.revalidate();
        drawMouse.repaint();

    }

    // 0-0-0-0- Player Event Listener -0-0-0-0
    public void addMovementListener(PlayerBehaviorListener listener) {
        playerListener.add(listener);
    }

    public void removeMovementListener(PlayerBehaviorListener listener) {
        playerListener.remove(listener);
    }

    protected void onPlayerActions(Player actionPlayer) {
        for (PlayerBehaviorListener listener : playerListener) {
            listener.onPlayerAction(actionPlayer);

        }
    }

    protected void onPlayerShootBullets(CopyOnWriteArrayList<Bullet> actionBullets) {
        for (PlayerBehaviorListener listener : playerListener) {
            listener.onShootBullet(actionBullets);

        }

    }
}