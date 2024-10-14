package types;

public class ZombieType {
    int speed;
    int damage;
    int health;

    public ZombieType(int speed, int damage, int health) {
        this.speed = speed;
        this.damage = damage;
        this.health = health;
    }

    public int getSpeed() {
        return this.speed;

    }

    public int getDamage() {
        return this.damage;

    }

    public int getHealth() {
            return this.health;

    }
}