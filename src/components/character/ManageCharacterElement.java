package components.character;

import java.awt.Rectangle;

public interface ManageCharacterElement {
    // Character
    final int CHARACTER_WIDTH = 250;
    final int CHARACTER_HEIGHT = 250;

    // Hitbox
    final int CHARACTER_HIT_X = 80;
    final int CHARACTER_HIT_Y = 140;

    // Content
    final int CHARACTER_CENTER_X = (int) (CHARACTER_WIDTH / 3);
    final int CHARACTER_CENTER_Y = (int) (int) (CHARACTER_HEIGHT / 4.25);
    final Rectangle CHARACTER_CENTER_XY = new Rectangle(CHARACTER_CENTER_X, CHARACTER_CENTER_Y, CHARACTER_HIT_X, CHARACTER_HIT_Y);

    // Weapon
    final int WEAPON_WIDTH = 160;
    final int WEAPON_HEIGHT = 155;
    final double WEAPON_SCALE = 0.5;

}