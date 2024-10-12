package components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;

public class DrawBulletLine implements ManageCharacterElement {
    public DrawBulletLine(Graphics2D g2d, Point mousePosition, CreateCharacter character) {
        final int BULLET_LINE_LENGTH = 200;

        if (mousePosition == null || !character.getCharacterIsAlive()) {
            return;

        }

        // Start Position
        int weaponSpinX = (character.getX() + CHARACTER_CENTER_X) + 40;
        int weaponSpinY = (character.getY() + CHARACTER_CENTER_Y) + 70;

        double deltaX = mousePosition.x - weaponSpinX;
        double deltaY = mousePosition.y - weaponSpinY;
        double angle = Math.atan2(deltaY, deltaX);

        int endX = weaponSpinX + (int) (Math.cos(angle) * BULLET_LINE_LENGTH);
        int endY = weaponSpinY + (int) (Math.sin(angle) * BULLET_LINE_LENGTH);

        // วาดเส้นประ
        float[] dashPattern = { 10f, 10f };
        Stroke dashedStroke = new BasicStroke(4f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10f, dashPattern, 0f);

        g2d.setStroke(dashedStroke);
        g2d.setColor(Color.RED);
        g2d.drawLine(weaponSpinX, weaponSpinY, endX, endY);
    }
}