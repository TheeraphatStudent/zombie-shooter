package components;

import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import page.home.GameCenter;
import utils.LoadImage;
import utils.UseText;

public class CreateCharacter extends JPanel {
    private boolean isDead = false;
    private boolean isInfected = false;
    private int x, y;
    private int hp = 100;
    private int useCharacter;

    // Ref
    private GameCenter gameCenter;
    CreateHpBar hpBar;

    public CreateCharacter(GameCenter gameCenter) {
        this.gameCenter = gameCenter;
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

        // Health bar
        hpBar = new CreateHpBar(hp);
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

                Image character = new LoadImage()
                        .getImage(String.format("resource/images/character/survive/h%d.png", useCharacter));
                g.drawImage(character, 0, 0, getWidth(), getHeight(), this);
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

}

class CreateHpBar extends JPanel {

    private int hp;

    public CreateHpBar(int hp) {
        this.hp = hp;
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

        g.setColor(Color.GREEN);
        g.fillRect(0, 0, (int) (getWidth() * (hp / 100.0)), getHeight());

        g.setColor(Color.BLACK);
        g.drawString(hp + " HP", getWidth() / 2 - 15, getHeight() / 2 + 5);
    }
}
