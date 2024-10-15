package models;

import javax.swing.Timer;

import components.character.CreateCharacter;

public class Player {
    private State state;
    private CreateCharacter character;
    private volatile int zombieHunt = 0;

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

    // >>>>>>>>>> Setter >>>>>>>>>>

    public void addZombieWasKilled(int number) {
        this.zombieHunt += number;
        this.storeZombieHunt += number;

        if (storeZombieHunt >= (5 * (rank + 1))) {
            System.out.println("Is Rank Up!");
            rank++;
            character.setCharacterRank(rank);

            storeZombieHunt = 0;

        }

        System.out.println(this.zombieHunt);

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public void onGameFinish() {
        this.onSurvive.stop();

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
        return (5 * (this.rank + 1));

    }

    public String getAliveTime() {
        return String.format("%d : %d : %d", this.hour, this.min, this.sec);

    }

}
