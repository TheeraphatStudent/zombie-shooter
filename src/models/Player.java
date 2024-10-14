package models;

import javax.swing.Timer;

import components.character.CreateCharacter;

public class Player {
    private CreateCharacter character;
    private volatile int zombieHunt = 0;

    // On Survive
    private Timer onSurvive;
    private volatile int sec = 0;
    private volatile int min = 0;
    private volatile int hour = 0;

    public Player(CreateCharacter character) {
        this.character = character;

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
        System.out.println(this.zombieHunt);

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public void onGameFinish() {
        this.onSurvive.stop();

    }

    public int getZombieHunt() {
        return this.zombieHunt;

    }

    public String getAliveTime() {
        return String.format("%d : %d : %d", this.hour, this.min, this.sec);

    }

}
