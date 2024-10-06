package components;

import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import page.controls.GameContent;
import page.home.GameCenter;
import utils.LoadImage;
import utils.UseText;

interface CreateCharacterProps {
    void setCharacterMoveLeft(boolean isMoveLeft);

    void setCharacterAlive(boolean isAlive);

    void setCharacterInfected(boolean isInfected);

}

public class CreateCharacter extends JPanel implements CreateCharacterProps {
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

    // [[[[[[[[[[ Player ]]]]]]]]]]
    public CreateCharacter(GameCenter gameCenter, GameContent gameContent, boolean isInfected) {
        this.gameCenter = gameCenter;
        this.gameContent = gameContent;
        this.isSurvive = !isInfected;
        this.isInfected = isInfected;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(200, 200));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        String displayName = gameCenter.getDisplayName();
        String displayText = new UseText().truncateText(displayName);
        displayText += " - rank 0";

        JTextPane playerName = new UseText(14, 200, 40).createSimpleText(
                displayText, Color.WHITE, null, Font.PLAIN);
        playerName.setBounds(0, 0, 200, 40);

        add(playerName);

        // Character panel
        JPanel character = createCharacterImage();
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        add(character);

        Color hpColor = Color.GREEN;

        if (isInfected) {
            hpColor = Color.ORANGE;

        }

        // Health bar
        hpBar = new CreateHpBar(hp, hpColor);
        hpBar.setBounds(0, 175, 100, 20);
        add(hpBar);
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
        setPreferredSize(new Dimension(200, 200));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        JTextPane zombieName = new UseText(14, 200, 40).createSimpleText(
                "", Color.WHITE, null, Font.PLAIN);
        zombieName.setBounds(0, 0, 200, 40);

        add(zombieName);

        JPanel character = createCharacterImage();
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        add(character);

        hpBar = new CreateHpBar(hp, Color.RED);
        hpBar.setBounds(0, 175, 100, 20);
        add(hpBar);
    }

    public void setCharacterHp(int hp) {
        hpBar.setHp(hp);
    }

    private JPanel createCharacterImage() {

        return new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(Color.RED);
                g.drawRect(0, 0, getWidth(), getHeight());

                String getImagePath = "resource/images/character/survive/h%d.png";

                if (!isSurvive) {
                    getImagePath = "resource/images/character/zombie/z%d.png";

                }

                // ========== Character Props ==========

                Image character = new LoadImage()
                        .getImage(String.format(getImagePath, useCharacter));

                Graphics2D g2d = (Graphics2D) g;

                if (!isMoveLeft) {
                    g2d.drawImage(character, 0, 0, getWidth(), getHeight(), this);

                } else {
                    AffineTransform old = g2d.getTransform();

                    g2d.translate(getWidth(), 0);
                    g2d.scale(-1, 1);

                    g2d.drawImage(character, 0, 0, getWidth(), getHeight(), this);

                    g2d.setTransform(old);

                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(80, 140);
            }
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawRect(0, 0, getWidth(), getHeight());

        String getGun = "resource/images/character/weapon/Gun.png";
        if (!isSurvive) {
            getGun = "";
        }
        Image weapon = new LoadImage().getImage(getGun);

        AffineTransform oldTransform = g2d.getTransform();

        int characterCenterX = 40; // width is 80
        int characterCenterY = 95; // height (140/2 + 25)

        g2d.translate(characterCenterX, characterCenterY);
        g2d.rotate(weaponAngle);

        // Weapon
        int weaponWidth = 50;
        int weaponHeight = 20;
        g2d.drawImage(weapon, 0, 0, weaponWidth, weaponHeight, this);

        g2d.setTransform(oldTransform);
    }

    public void updateWeaponAngle(Point mousePos) {
        this.mousePosition = mousePos;

        int characterCenterX = getX() + 40;
        int characterCenterY = getY() + 95;

        double deltaX = mousePos.x - characterCenterX;
        double deltaY = mousePos.y - characterCenterY;

        weaponAngle = Math.atan2(deltaY, deltaX);

        if (isMoveLeft) {
            weaponAngle = Math.PI - weaponAngle;
        }

        repaint();
    }

    // :*:*:*:*:*:*:*:*:*: Character Getter :*:*:*:*:*:*:*:*:*:

    // :v:v:v:v:v:v:v:v:v: Character Setter :v:v:v:v:v:v:v:v:v:
    public void setCharacterMoveLeft(boolean isMoveLeft) {
        this.isMoveLeft = !isMoveLeft;

        // อย่าลืม Repain
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

    public void revalidateComponent() {
        repaint();
        revalidate();

    }
}

class CreateHpBar extends JPanel {

    private int hp;
    private Color colorBar = Color.GREEN;

    public CreateHpBar(int hp, Color colorBar) {
        this.hp = hp;
        this.colorBar = colorBar;

        setPreferredSize(new Dimension(100, 20));
    }

    public void setHp(int hp) {
        this.hp = hp;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(colorBar);
        g.fillRect(0, 0, (int) (getWidth() * (hp / 100.0)), getHeight());

        g.setColor(Color.BLACK);
        g.drawString(hp + " HP", getWidth() / 2 - 15, getHeight() / 2 + 5);
    }
}
