package types;

import java.io.Serializable;

public class ZombieType implements Serializable {
    private static final long serialVersionUID = 1L;

    int speed;
    int damage;
    int health;

    public ZombieType(int speed, int damage, int health) {
        this.speed = speed;
        this.damage = damage;
        this.health = health;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHealth(int health) {
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