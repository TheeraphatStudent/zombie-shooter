package models.Zombie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import models.State;
import page.controls.GameContent;
import types.ZombieType;

public class Behavior implements ManageCharacterElement {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private String id;

    private Position position;
    private CreateCharacter character;
    private CreateCharacter zombie;
    private GameContent gameContent;
    private State state;

    // ความเร็วที่ Zombie เดิน
    private double dx = 0f;
    private double dy = 0f;

    private int movedX = 0;
    private int movedY = 0;

    private String type = "normal";

    public Behavior(
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

        this.id = UUID.randomUUID().toString();

        updateZombieBehavior();

    }

    private void updateZombieBehavior() {
        Map<String, ZombieType> newZombieTypes = new HashMap<>();

        for (String key : zombieTypes.keySet()) {
            ZombieType currentType = zombieTypes.get(key);

            int newDamage = currentType.getDamage() + (int) (currentType.getDamage() * .25);
            int newHealth = currentType.getHealth()
                    + (int) (currentType.getHealth() * this.state.getLevelState() * .25);

            // System.out.println(String.format("New Damage: %d\nNew Health: %d\n",
            // newDamage, newHealth));

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
            this.dx = playerX - zombieX;
            this.dy = playerY - zombieY;

            // System.out.printf("Diagonal Player Position: dx=%f | dy=%f\n", this.dx, this.dy);

            // หามุมที่ ผู้เล่นอยู่ เพื่อให้ zombie เดินไปหา ผู้เล่น
            double angle = Math.atan2(this.dy, this.dx);

            zombie.setCharacterMoveLeft(this.dx < 0);

            // ระบุประเภทของซอมบี้
            ZombieType zombieType = zombieTypes.get(this.type);
            double zombieSpeed = zombieType.getSpeed();

            this.movedX = zombie.getX() + (int) (Math.cos(angle) * zombieSpeed);
            this.movedY = zombie.getY() + (int) (Math.sin(angle) * zombieSpeed);

            // เปลี่ยน ตำแหน่งของ Zombie
            zombie.setLocation(this.movedX, this.movedY);
            position.setLocation(this.movedX, this.movedY);

        });
    }

    // ========== Setter ==========

    public void setPositionObject(Position position) {
        this.position = position;

    }

    // ========== Getter ==========

    public Position getPositionObject() {
        return this.position;

    }

    public String getId() {
        return this.id;

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

    public double getDiagonalX() {
        return this.dx;

    }

    public double getDiagonalY() {
        return this.dy;

    }

    public int getMovedX() {
        return this.movedX;
    }

    public int getMovedY() {
        return this.movedY;
    }

    public String getType() {
        return this.type;

    }

    private Map<String, ZombieType> zombieTypes = new HashMap<String, ZombieType>() {
        {
            put("normal", new ZombieType(7, 15, 80));
            put("fast", new ZombieType(15, 10, 60));
            put("slow", new ZombieType(5, 20, 100));
        }
    };
}
