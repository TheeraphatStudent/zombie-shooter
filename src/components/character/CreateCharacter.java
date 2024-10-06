package components.character;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import components.objectElement.Bullet;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import page.controls.GameContent;
import page.home.GameCenter;

import utils.LoadImage;
import utils.UseText;

interface ManageCharacterElement {
    // Character
    final int CHARACTER_WIDTH = 250;
    final int CHARACTER_HEIGHT = 250;

    // Weapon
    final int WEAPON_WIDTH = 160;
    final int WEAPON_HEIGHT = 155;
    final double WEAPON_SCALE = 0.5;

}

interface CreateCharacterProps {
    // Method
    void setCharacterMoveLeft(boolean isMoveLeft);

    void setCharacterAlive(boolean isAlive);

    void setCharacterInfected(boolean isInfected);

    void updateWeaponAngle(Point mousePos);

    void onShootBullet(Point mousePos);

}

public class CreateCharacter extends JPanel implements CreateCharacterProps, ManageCharacterElement, Runnable {
    // ON Character
    private JLayeredPane base;
    private JLayeredPane compressContent;

    private CreateCharacterImage character;
    private JPanel weapon;

    // ON State
    private boolean isDead = false;
    private boolean isInfected = false;
    private int x, y;
    private int hp = 100;
    private int useCharacter;
    private boolean isMoveLeft = false;
    private boolean isSurvive = true;

    // Weapon Angle
    private double weaponAngle = 0;
    private Point mousePosition = new Point(0, 0);

    // Ref
    private GameCenter gameCenter;
    private GameContent gameContent;
    private CreateHpBar hpBar;

    private Thread drawingThread;
    private volatile boolean running = true;

    // Models

    // [[[[[[[[[[ Player ]]]]]]]]]]
    public CreateCharacter(GameCenter gameCenter, GameContent gameContent, boolean isInfected) {
        this.gameCenter = gameCenter;
        this.gameContent = gameContent;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        // [document/images/enemy.png]

        // >>>>>>>>>> Create 📃
        base = new JLayeredPane();
        compressContent = new JLayeredPane();

        base.setOpaque(false);
        base.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        compressContent.setOpaque(false);
        compressContent.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        // >>>>>>>>>> Player name 👋
        String displayName = gameCenter.getDisplayName();
        String displayText = new UseText().truncateText(displayName);
        displayText += " - rank 0";

        JTextPane playerName = new UseText(14, CHARACTER_WIDTH, 40).createSimpleText(
                displayText,
                Color.WHITE,
                null,
                Font.PLAIN);
        playerName.setBounds(0, 0, CHARACTER_WIDTH, 40);
        base.add(playerName);

        // >>>>>>>>>> Character 🗣️
        character = new CreateCharacterImage(useCharacter, !isInfected, this.isMoveLeft);

        // ! Character set content size
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        base.add(character);

        // >>>>>>>>>> Health bar ❤️‍🩹
        Color onSurvive = !isInfected ? Color.GREEN : Color.ORANGE;

        hpBar = new CreateHpBar(hp, onSurvive);
        hpBar.setBounds(0, 175, 100, 20);
        base.add(hpBar);

        // >>>>>>>>>> Weapon 🗡️
        weapon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldTransform = g2d.getTransform();

                // Draw weapon
                drawWeapon(g2d, oldTransform);

            }
        };

        weapon.setOpaque(false);
        weapon.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        compressContent.add(base, JLayeredPane.DEFAULT_LAYER);
        compressContent.add(weapon, JLayeredPane.DRAG_LAYER);

        add(compressContent);

        // :::::::::: Thread ::::::::::

        drawingThread = new Thread(this);
        drawingThread.start();

    }

    // :::::::::: Zombie ::::::::::
    public CreateCharacter(GameCenter gameCenter, GameContent gameContent) {
        this.gameCenter = gameCenter;
        this.gameContent = gameContent;

        // Zombie State -> false, false
        this.isSurvive = false;
        this.isInfected = false;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        JTextPane zombieName = new UseText(14, CHARACTER_WIDTH, 40).createSimpleText(
                "", Color.WHITE, null, Font.PLAIN);
        zombieName.setBounds(0, 0, CHARACTER_WIDTH, 40);

        add(zombieName);

        character = new CreateCharacterImage(useCharacter, false, this.isMoveLeft);

        // ! Character set content size
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        add(character);

        hpBar = new CreateHpBar(hp, Color.RED);
        hpBar.setBounds(0, 175, 100, 20);
        add(hpBar);

        // Start drawing thread
        drawingThread = new Thread(this);
        drawingThread.start();

    }

    public void setCharacterHp(int hp) {
        hpBar.setHp(hp);
    }

    // ::::::::::::::::: Draw Weapon :::::::::::::::::::

    private void drawWeapon(Graphics2D g2d, AffineTransform originTransform) {

        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isSurvive)
            return;

        String getGun = "resource/images/character/weapon/Gun.png";
        Image weapon = new LoadImage().getImage(getGun);

        // ตำแหน่งในการหมุนปืน
        int weaponPivotX = character.getX() + 40;
        int weaponPivotY = character.getY() + 70;

        g2d.translate(weaponPivotX, weaponPivotY);
        g2d.rotate(weaponAngle);

        g2d.scale(WEAPON_SCALE, WEAPON_SCALE);

        int scaledHeight = (int) (WEAPON_HEIGHT * WEAPON_SCALE);

        g2d.drawImage(weapon,
                -20,
                -scaledHeight / 2,
                WEAPON_WIDTH,
                WEAPON_HEIGHT,
                this);

        g2d.setTransform(originTransform);
    }

    // ::::::::::::::::: Weapon Control :::::::::::::::::::

    public void updateWeaponAngle(Point mousePos) {
        this.mousePosition = mousePos;

        Point componentPos = SwingUtilities.convertPoint(
                getParent(), mousePos, this);

        int weaponPivotX = character.getX() + 40;
        int weaponPivotY = character.getY() + 70;

        double deltaX = componentPos.x - weaponPivotX;
        double deltaY = componentPos.y - weaponPivotY;

        weaponAngle = Math.atan2(deltaY, deltaX);
        System.out.println("Weapon Angle: " + weaponAngle);

        // if (isMoveLeft) {
        // weaponAngle = Math.PI - weaponAngle;

        // }

        weapon.repaint();
    }

    public void onShootBullet(Point mousePos) {
        System.out.println("On Shoot");
        System.out.println(mousePos.getLocation());

    }

    // ::::::::::::::::: Start Drawing Thread :::::::::::::::::::

    @Override
    public void run() {
        while (running) {
            try {
                revalidateComponent();
                Thread.sleep(64);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                break;
            }
        }
    }

    public void stop() {
        running = false;
        drawingThread.interrupt();
    }

    // ::::::::::::::::: Revalidate Component :::::::::::::::::::

    public void revalidateComponent() {
        repaint();
        revalidate();

        weapon.repaint();
        weapon.revalidate();

        character.repaint();
        character.revalidate();
    }

    // ::::::::::::::::: Control :::::::::::::::::
    // >>>>>>>>>> Setter >>>>>>>>>>

    public void setCharacterMoveLeft(boolean isMoveLeft) {
        this.isMoveLeft = isMoveLeft;
        this.character.setCharacterMoveLeft(isMoveLeft);

        revalidateComponent();
    }

    public void setCharacterAlive(boolean isAlive) {
        this.isSurvive = isAlive;
        revalidateComponent();
    }

    public void setCharacterInfected(boolean isInfected) {
        this.isInfected = isInfected;
        revalidateComponent();
    }

    // <<<<<<<<<< Getter <<<<<<<<<<
}
