package models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import page.controls.GameContent;
import types.ZombieType;

public class Zombie implements ManageCharacterElement {

    private CreateCharacter character;
    private CreateCharacter zombie;
    private GameContent gameContent;
    private State state;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

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

            // System.out.println(String.format("New Damage: %d\nNew Health: %d\n", newDamage, newHealth));

            ZombieType updatedType = new ZombieType(
                    currentType.getSpeed(),
                    newDamage,
                    newHealth);
            newZombieTypes.put(key, updatedType);
        }

        this.zombieTypes = newZombieTypes;

    }

    public void updateZombiePosition() {
        executor.submit(() -> {
            int playerX = character.getX() + (CHARACTER_WIDTH / 2); 
            int playerY = character.getY() + (CHARACTER_HEIGHT / 2);
    
            int zombieX = zombie.getX() + (CHARACTER_WIDTH / 2); 
            int zombieY = zombie.getY() + (CHARACTER_HEIGHT / 2);
    
            // ระบุตำแหน่งที่ผู้เล่นอยู่
            double dx = playerX - zombieX;
            double dy = playerY - zombieY;
    
            // หามุมที่ ผู้เล่นอยู่ เพื่อให้ zombie เดินไปหา ผู้เล่น
            double angle = Math.atan2(dy, dx);
    
            zombie.setCharacterMoveLeft(dx < 0);
    
            // ระบุประเภทของซอมบี้
            ZombieType zombieType = zombieTypes.get(this.type);
            double zombieSpeed = zombieType.getSpeed();
    
            int newX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
            int newY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);
    
            // เปลี่ยน ตำแหน่งของ Zombie
            zombie.setLocation(newX, newY);

        });
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
