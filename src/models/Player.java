package models;

import java.awt.Point;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import components.character.CreateCharacter;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isMoveLeft = false;
    private boolean isInfected = false;

    private State state;
    private int characterNo = 1;

    private volatile int xDir = 0;
    private volatile int yDir = 0;

    private volatile int zombieHunt = 0;
    private volatile Point weaponPoint;

    private CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
    private volatile int damage = 10;
    private volatile int health = 100;
    private int maxHealth = 100;

    // ใช้สำหรับอัพแรงค์
    private volatile int storeZombieHunt = 0;
    private volatile int rank = 0;

    // On Survive
    private Timer onSurvive;
    private volatile int sec = 0;
    private volatile int min = 0;
    private volatile int hour = 0;

    public Player(int characterNo, State state) {
        this.characterNo = characterNo;
        this.state = state;

        this.onSurvive = new Timer(1000, e -> {
            sec++;

            if (sec >= 60) {
                min++;
                sec = 0;

            }

            if (min >= 60) {
                hour++;
                min=0;

            }

        });

        this.onSurvive.start();

    }

    public void onGameFinish() {
        this.onSurvive.stop();

    }

    // >>>>>>>>>> Setter >>>>>>>>>>

    public void setState(State state) {
        this.state = state;

    }

    public void setBullets(CopyOnWriteArrayList<Bullet> bullets) {
        this.bullets = bullets;

    }

    public void addZombieWasKilled(int number) {
        this.zombieHunt += number;
        this.storeZombieHunt += number;

        if (storeZombieHunt >= ((5 * (rank + 1)) * 2)) {
            System.out.println("Is Rank Up!");
            this.rank++;
            // character.setCharacterRank(rank);
            
            this.health = this.health + (int) ((int) (10 * (rank + 1)) * (state.getLevelState() * 0.2));
            // character.setCharacterHp(this.health);
            System.out.println("Current Health: " + this.health);

            this.damage += (5 * state.getLevelState()) + rank+1;
            System.out.println("Current Damage: " + this.damage);

            storeZombieHunt = 0;

        }

        System.out.println(this.zombieHunt);

    }

    public void setPlayerBulletDamage(int newDamage) {
        this.damage = newDamage;

    }

    // -----* Player Health *-----

    public void setPlayerHealth(int newHealth) {
        this.health = newHealth;

    }

    public void setMaxPlayerHealth(int maxHealth) {
        this.maxHealth = maxHealth;

    }

    // -----* Player Position *-----

    public void setPlayerLocation(int x, int y) {
        System.out.printf("Set Player Location: x=%d | y=%d\n", x, y);

        this.xDir = x;
        this.yDir = y;

    }

    // -----* Player Movement *-----

    public void setWeaponPoint(Point point) {
        this.weaponPoint = point;

    }

    public void setIsPlayerMoveLeft(boolean isMovedLeft) {
        this.isMoveLeft = isMovedLeft;

    }
    
    public void setInfectedPlayer(boolean isInfected) {
        this.isInfected = isInfected;

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public int getCharacterNo() {
        return this.characterNo;

    }

    public int getPlayerBulletDamage() {
        return this.damage;

    }

    public int getPlayerHealth() {
        return this.health;

    }

    public int getMaxPlayerHealth() {
        return this.maxHealth;

    }

    public int getZombieHunt() {
        return this.zombieHunt;

    }

    public int getStoreZombieHunt() {
        return this.storeZombieHunt;

    }

    public int getRank() {
        return this.rank;

    }

    public int getRankUpKillZombieNeeded() {
        return (5 * (this.rank + 1)) * 2;

    }

    public String getAliveTime() {
        return String.format("%d : %d : %d", this.hour, this.min, this.sec);

    }

    public int getDirectionX() {
        return this.xDir;

    }

    public int getDirectionY() {
        return this.yDir;

    }

    public State getState() {
        return this.state;

    }

    public CopyOnWriteArrayList<Bullet> getBullets() {
        return this.bullets;

    }

    public Point getWeaponPoint() {
        return this.weaponPoint;

    }

    public boolean getPlayerIsMovedLeft() {
        return this.isMoveLeft;

    }

    public boolean getInfectedStatus() {
        return this.isInfected;

    }

}
