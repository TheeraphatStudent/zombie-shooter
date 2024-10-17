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
import models.ClientObj;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import page.controls.GameContent;

import utils.LoadImage;
import utils.UseGlobal;
import utils.UseText;

interface CreateCharacterProps {
    // Method
    void setCharacterMoveLeft(boolean isMoveLeft);

    void setCharacterAlive(boolean isAlive);

    void setCharacterInfected(boolean isInfected);

    void updateWeaponAngle(Point mousePos);

    void onShootBullet(Point mousePos);

}

public class CreateCharacter extends JPanel implements CreateCharacterProps, ManageCharacterElement {

    ClientObj client;
    
    // ON Character
    private JLayeredPane base;
    private JLayeredPane compressContent;
    private String zombieType;

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

    private JTextPane playerName;
    private String displayName = "";
    private String displayText = "";
    private int currentRank = 0;

    // Weapon Angle
    private double weaponAngle = 0;

    // Ref
    private GameContent gameContent;
    private CreateHpBar hpBar;

    // Models

    // [[[[[[[[[[[[[[[[[[[[ Player ]]]]]]]]]]]]]]]]]]]]
    public CreateCharacter(GameContent gameContent, boolean isInfected, ClientObj clientObj) {
        this.gameContent = gameContent;
        this.isSurvive = !isInfected;

        this.client = clientObj;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        // [document/images/enemy.png]

        // >>>>>>>>>> Create 📃
        base = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(Color.ORANGE);
                g2d.drawRect(0, 0, getWidth(), CHARACTER_HEIGHT);

                super.paintComponent(g);
            }

        };
        compressContent = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(Color.PINK);
                g2d.drawRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
            }

        };

        // Compress Content Here!
        compressContent.setOpaque(false);
        compressContent.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        compressContent.setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        base.setOpaque(false);
        base.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        base.setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        // >>>>>>>>>> Player name 👋
        displayName = client.getClientName();
        displayText = new UseText().truncateText(displayName) + " - rank " + currentRank;

        playerName = new UseText(16, CHARACTER_WIDTH, 40, true).createSimpleText(
                displayText,
                Color.WHITE,
                null,
                Font.PLAIN);
        playerName.setBounds(0, 25, CHARACTER_WIDTH, 40);

        base.add(playerName);

        // >>>>>>>>>> Character 🗣️
        character = new CreateCharacterImage(useCharacter, !isInfected, this.isMoveLeft);

        // ! Character set content size
        character.setBounds(CHARACTER_CENTER_XY);
        character.setOpaque(false);
        base.add(character);

        // >>>>>>>>>> Health bar ❤️‍🩹
        Color onSurvive = !isInfected ? Color.GREEN : Color.ORANGE;

        hpBar = new CreateHpBar(hp, onSurvive);
        hpBar.setBounds((CHARACTER_CENTER_X - 8), (CHARACTER_CENTER_Y + CHARACTER_HIT_Y) + 8, 100, 20);
        base.add(hpBar);

        // >>>>>>>>>> Weapon 🗡️
        weapon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldTransform = g2d.getTransform();

                g2d.setColor(Color.MAGENTA);
                g2d.drawRect(0, 0, getWidth(), getHeight());

                // Draw weapon
                drawWeapon(g2d, oldTransform);

            }
        };

        weapon.setOpaque(false);
        weapon.setBounds(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        weapon.setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        compressContent.add(base, JLayeredPane.DEFAULT_LAYER);
        compressContent.add(weapon, JLayeredPane.DRAG_LAYER);

        add(compressContent);


    }

    // :::::::::::::::::::: Zombie ::::::::::::::::::::
    public CreateCharacter(GameContent gameContent) {
        this.gameContent = gameContent;

        // Zombie State -> false, false
        this.isSurvive = false;
        this.isInfected = false;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(CHARACTER_WIDTH, CHARACTER_HEIGHT));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        // JTextPane zombieName = new UseText(14, CHARACTER_WIDTH, 40).createSimpleText(
        // "", Color.WHITE, null, Font.PLAIN);
        // zombieName.setBounds(0, 0, CHARACTER_WIDTH, 40);

        // add(zombieName);

        character = new CreateCharacterImage(useCharacter, false, this.isMoveLeft);

        // ! Character set content size
        character.setBounds(CHARACTER_CENTER_XY);
        character.setOpaque(false);
        add(character);

        hpBar = new CreateHpBar(hp, Color.RED);
        hpBar.setBounds((CHARACTER_CENTER_X - 8), (CHARACTER_CENTER_Y + CHARACTER_HIT_Y) + 8, 100, 20);
        add(hpBar);

    }

    // :|:|:|:|:|:|:|:|:|:|:|:|:|: Pain Component :|:|:|:|:|:|:|:|:|:|:|:|:|:

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.green);
        g2d.drawRect(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        super.paintComponent(g2d);
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
        int weaponSpinX = character.getX() + 40;
        int weaponSpinY = character.getY() + 70;

        g2d.translate(weaponSpinX, weaponSpinY);
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
        Point componentPos = SwingUtilities.convertPoint(getParent(), mousePos, this);

        int weaponSpinX = character.getX() + 40;
        int weaponSpinY = character.getY() + 70;

        double deltaX = componentPos.x - weaponSpinX;
        double deltaY = componentPos.y - weaponSpinY;

        weaponAngle = Math.atan2(deltaY, deltaX);

        // if (isMoveLeft) {
        // weaponAngle = Math.PI - weaponAngle;

        // }

        weapon.repaint();
    }

    public void onShootBullet(Point mousePos) {
        if (!isSurvive) {
            return;

        }

        // ตำแหน่งปืน
        int weaponSpinX = (getX() + CHARACTER_CENTER_X) + 40;
        int weaponSpinY = (getY() + CHARACTER_CENTER_Y) + 70;

        // เพิ่มจำนวนกระสุนที่ยิงออกไป
        gameContent.addBullet(new Bullet(weaponSpinX, weaponSpinY, weaponAngle));
    }

    // ::::::::::::::::: Control :::::::::::::::::

    // >>>>>>>>>> Setter >>>>>>>>>>

    public void setZombieType(String zombieType) {
        this.zombieType = zombieType;
    }

    public void setCharacterMoveLeft(boolean isMoveLeft) {
        this.isMoveLeft = isMoveLeft;
        this.character.setCharacterMoveLeft(isMoveLeft);

    }

    public void setCharacterAlive(boolean isAlive) {
        this.isSurvive = isAlive;
    }

    public void setCharacterInfected(boolean isInfected) {
        this.isInfected = isInfected;
    }

    public void setCharacterHp(int hp) {
        System.out.println("Set Character Hp Work!");
        System.out.println(hp);

        this.hp = hp;
        hpBar.setHp(this.hp);

    }

    public void setCharacterRank(int rank) {
        System.out.println("Set Character Rank Work!");
        System.out.println(rank);
    
        this.currentRank = rank;
        this.displayText = displayName + " - rank " + this.currentRank;  // Update display text
    
        playerName.setText(displayText); 
    
        revalidateContent();
    }
    

    // <<<<<<<<<< Getter <<<<<<<<<<

    public boolean getCharacterIsAlive() {
        return this.isSurvive;

    }

    public int getCharacterHp() {
        return this.hp;

    }

    public String getZombieType() {
        return this.zombieType;
    }

    private void revalidateContent() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                revalidate();
                repaint();

            }
        });

    }

}
