package models;

import java.io.Serializable;

import javax.swing.Timer;

import components.character.CreateCharacter;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private State state;
    private CreateCharacter character;

    private volatile int xDir = 0;
    private volatile int yDir = 0;

    private volatile int zombieHunt = 0;

    private volatile int damage = 10;
    private volatile int health = 100;

    // ใช้สำหรับอัพแรงค์
    private volatile int storeZombieHunt = 0;

    private volatile int rank = 0;

    // On Survive
    private Timer onSurvive;
    private volatile int sec = 0;
    private volatile int min = 0;
    private volatile int hour = 0;

    public Player(CreateCharacter character, State state) {
        this.character = character;
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

    public void addZombieWasKilled(int number) {
        this.zombieHunt += number;
        this.storeZombieHunt += number;

        if (storeZombieHunt >= ((5 * (rank + 1)) * 2)) {
            System.out.println("Is Rank Up!");
            rank++;
            character.setCharacterRank(rank);
            
            this.health = this.health + (int) ((int) (10 * (rank + 1)) * (state.getLevelState() * 0.2));
            character.setCharacterHp(this.health);
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

    public void setPlayerHealth(int newHealth) {
        this.health = newHealth;

    }

    public void setPlayerDirecter(int x, int y) {
        this.xDir = x;
        this.yDir = y;

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public int getPlayerBulletDamage() {
        return this.damage;

    }

    public int getPlayerHealth() {
        return this.health;

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

    public int geDirectionX() {
        return this.xDir;

    }

    public int geDirectionY() {
        return this.yDir;

    }

}
