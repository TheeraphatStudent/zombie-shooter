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
    private State state;

    private String type = "normal";

    public Zombie(
            CreateCharacter character,
            CreateCharacter zombie,
            GameContent gameContent,
            State state,
            String type) {
        this.character = character;
        this.zombie = zombie;
        this.gameContent = gameContent;

        this.state = state;
        this.type = type;

        updateZombieBehavior();

    }

    private void updateZombieBehavior() {
        Map<String, ZombieType> newZombieTypes = new HashMap<>();

        for (String key : zombieTypes.keySet()) {
            ZombieType currentType = zombieTypes.get(key);

            int newDamage = currentType.getDamage() + (int) (currentType.getDamage() * .25);
            int newHealth = currentType.getHealth() + (int) (currentType.getHealth() * this.state.getLevelState() * .25);

            System.out.println(String.format("New Damage: %d\nNew Health: %d\n", newDamage, newHealth));

            ZombieType updatedType = new ZombieType(
                    currentType.getSpeed(),
                    newDamage,
                    newHealth);
            newZombieTypes.put(key, updatedType);
        }

        this.zombieTypes = newZombieTypes;

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
        ZombieType zombieType = zombieTypes.get(this.type);
        double zombieSpeed = zombieType.getSpeed();

        // Move zombie
        int newX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
        int newY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);

        // Update the zombie's location
        zombie.setLocation(newX, newY);
    }

    public ZombieType getZombieType(String zombieBehavior) {
        return zombieTypes.get(zombieBehavior);

    }

    public double getZombieSpeed() {
        return zombieTypes.get(this.type).getSpeed();

    }

    public double getZombieDamage() {
        return zombieTypes.get(this.type).getDamage();

    }

    public double getZombieHealth() {
        return zombieTypes.get(this.type).getHealth();

    }

    private Map<String, ZombieType> zombieTypes = new HashMap<String, ZombieType>() {
        {
            put("normal", new ZombieType(7, 15, 80));
            put("fast", new ZombieType(15, 10, 60));
            put("slow", new ZombieType(5, 20, 100));
        }
    };
}
