package components;

import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import page.controls.GameContent;
import page.home.GameCenter;
import utils.LoadImage;
import utils.UseText;

public class CreateCharacter extends JPanel {
    private boolean isDead = false;
    private boolean isInfected = false;
    private int x, y;
    private int hp = 100;
    private int useCharacter;
    private boolean isMoveLeft = false;

    // Ref
    private GameCenter gameCenter;
    private GameContent gameContent;
    private CreateHpBar hpBar;

    // [[[[[[[[[[ Player ]]]]]]]]]]
    public CreateCharacter(GameCenter gameCenter, GameContent gameContent, boolean isInfected) {
        this.gameCenter = gameCenter;
        this.gameContent = gameContent;
        this.isMoveLeft = isMoveLeft;

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
        JPanel character = createCharacterImage(isInfected);
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        add(character);

        // Health bar
        hpBar = new CreateHpBar(hp, Color.GREEN);
        hpBar.setBounds(0, 175, 100, 20);
        add(hpBar);
    }

    // :::::::::: Zombie ::::::::::
    public CreateCharacter(GameCenter gameCenter, GameContent gameContent) {
        this.gameCenter = gameCenter;
        this.gameContent = gameContent;

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(200, 200));

        this.useCharacter = (int) (Math.random() * 10) + 1;

        JTextPane zombieName = new UseText(14, 200, 40).createSimpleText(
                "", Color.WHITE, null, Font.PLAIN);
        zombieName.setBounds(0, 0, 200, 40);

        add(zombieName);

        // Character panel
        JPanel character = createCharacterImage(false);
        character.setBounds(0, 25, 80, 140);
        character.setOpaque(false);
        add(character);

        // Health bar
        hpBar = new CreateHpBar(hp, Color.RED);
        hpBar.setBounds(0, 175, 100, 20);
        add(hpBar);
    }

    public void setCharacterHp(int hp) {
        hpBar.setHp(hp);
    }

    private JPanel createCharacterImage(final boolean isSurvive) {
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

                Image character = new LoadImage()
                        .getImage(String.format(getImagePath, useCharacter));

                Graphics2D g2d = (Graphics2D) g;

                if (isMoveLeft) {
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

        g2d.drawRect(0, 0, getWidth(), getHeight());
    }

    // :::::::::: Character Movement Direction ::::::::::
    public void setCharacterMoveLeft(boolean isMoveLeft) {
        this.isMoveLeft = isMoveLeft;
        repaint();
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
