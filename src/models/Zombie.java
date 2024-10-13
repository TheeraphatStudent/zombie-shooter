package models;

import java.util.HashMap;
import java.util.Map;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import page.controls.GameContent;
import types.ZombieType;

public class Zombie implements ManageCharacterElement {

    private CreateCharacter character;
    private CreateCharacter zombie;
    private GameContent gameContent;

    public Zombie(
        CreateCharacter character,
        CreateCharacter zombie,
        GameContent gameContent) {
            this.character = character;
            this.zombie = zombie;
            this.gameContent = gameContent;

    }

    public void updateZombiePosition() {
        // Get positions
        int playerX = character.getX() + (CHARACTER_WIDTH / 2); // Center of player
        int playerY = character.getY() + (CHARACTER_HEIGHT / 2);

        int zombieX = zombie.getX() + (CHARACTER_WIDTH / 2); // Center of zombie
        int zombieY = zombie.getY() + (CHARACTER_HEIGHT / 2);

        // Calculate direction to player
        double dx = playerX - zombieX;
        double dy = playerY - zombieY;

        // Calculate angle to player for zombie rotation
        double angle = Math.atan2(dy, dx);

        zombie.setCharacterMoveLeft(dx < 0);

        // Get zombie type and speed
        ZombieType zombieType = zombieTypes.get(zombie.getZombieType());
        double zombieSpeed = zombieType.getSpeed();

        // Move zombie
        int newX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
        int newY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);

        // Update the zombie's location
        zombie.setLocation(newX, newY);
    }

    

    ZombieType getZombieType(String zombieBehavior) {
        return zombieTypes.get(zombieBehavior);

    }

    private Map<String, ZombieType> zombieTypes = new HashMap<String, ZombieType>() {
        {
            put("normal", new ZombieType(5, 10, 100));
            put("fast", new ZombieType(8, 5, 100));
            put("slow", new ZombieType(3, 15, 100));
        }
    };
}
