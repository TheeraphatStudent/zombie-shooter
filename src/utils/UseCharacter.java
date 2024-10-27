package utils;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;

import java.awt.Rectangle;
import java.util.Random;

public class UseCharacter implements ManageCharacterElement {
    public Rectangle getCharacterHitbox(CreateCharacter character) {
        int hitboxX = character.getX() + CHARACTER_CENTER_X;
        int hitboxY = character.getY() + CHARACTER_CENTER_Y;
        
        int hitboxWidth = CHARACTER_WIDTH - 2 * CHARACTER_CENTER_X;
        int hitboxHeight = CHARACTER_HEIGHT - 2 * CHARACTER_CENTER_Y;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public int getCharacterRandSpawnX() {
        int frameWidth = UseGlobal.getWidth();
        return Math.max(0, Math.min( new Random().nextInt(frameWidth - CHARACTER_WIDTH), frameWidth - CHARACTER_WIDTH));

    }

    public int getCharacterRandSpawnY() {
        int frameHeight = UseGlobal.getHeight();
        return Math.max(0, Math.min(new Random().nextInt(frameHeight - CHARACTER_HEIGHT), frameHeight - CHARACTER_HEIGHT));

    }
}
