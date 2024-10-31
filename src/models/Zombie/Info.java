package models.Zombie;

import java.io.Serializable;
import java.util.UUID;

import types.ZombieType;

public class Info implements Serializable {
    private static final long serialVersionUID = 1L;

    private String initialId;
    private int x;
    private int y;
    private int profile;
    private int health;
    private ZombieType type;

    public Info() {
        this.initialId = UUID.randomUUID().toString();

    }

    // >>>>>>>>>>>>>>>> Setter

    public void setId(String id) {
        this.initialId = id;

    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setZombieType(ZombieType type) {
        this.type = type;

    }

    // <<<<<<<<<<<<<<<< Getter

    public int getHealth() {
        return this.health;
    
    }

    public String getId() {
        return this.initialId;

    }

    public int getProfile() {
        return profile;

    }

    public int getX() {
        return x;

    }

    public int getY() {
        return y;

    }

    public ZombieType getZombieType() {
        return type;

    }
}
