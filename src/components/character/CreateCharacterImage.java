package components.character;

import javax.swing.JPanel;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import utils.LoadImage;

public class CreateCharacterImage extends JPanel implements ManageCharacterElement {

    private boolean isSurvive;
    private boolean isMoveLeft;
    private int useCharacter;

    public CreateCharacterImage(int useCharacter, boolean isSurvive, boolean isMoveLeft) {
        this.isSurvive = isSurvive;
        this.useCharacter = useCharacter;
        this.isMoveLeft = isMoveLeft;

    }

    public void setCharacterMoveLeft(boolean isMoveLeft) {
        this.isMoveLeft = isMoveLeft;

        repaint();
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLUE);
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
        return new Dimension(CHARACTER_HIT_X, CHARACTER_HIT_Y);
    }
}