package utils;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;

import java.awt.Rectangle;

public class UseCharacter implements ManageCharacterElement {
    public Rectangle getCharacterHitbox(CreateCharacter character) {
        int hitboxX = character.getX() + CHARACTER_CENTER_X;
        int hitboxY = character.getY() + CHARACTER_CENTER_Y;
        
        int hitboxWidth = CHARACTER_WIDTH - 2 * CHARACTER_CENTER_X;
        int hitboxHeight = CHARACTER_HEIGHT - 2 * CHARACTER_CENTER_Y;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }
}
