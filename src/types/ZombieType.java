package types;

public class ZombieType {
    double speed;
    double damage;
    double health;

    public ZombieType(double speed, double damage, double health) {
        this.speed = speed;
        this.damage = damage;
        this.health = health;
    }

    public double getSpeed() {
        return this.speed;

    }

    public double getDamage() {
        return this.damage;

    }
}