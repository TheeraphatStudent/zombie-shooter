package models;

import java.io.Serializable;

public class State implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * จำนวนของ Zombie = (10 + (players_join * 5)) * (state * 0.5)
    */

    // private ArrayList<Object> players  = new ArrayList<>();

    private int levelState = 0;
    private int maxZombie = 0;
    private int zombieRemain = 0;

    // >>>>>>>>>> Setter >>>>>>>>>>

    public void setZombieRemain(int zombieRemain) {
        this.zombieRemain = zombieRemain;

    }

    // public void setPlayersObject(ArrayList<Object> players) {
    //     this.players = players;

    // }

    public void setStateLevel(int incrementLevel) {
        this.levelState += incrementLevel;

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public int getMaxZombie() {
        maxZombie = (int) ((int) (5 + (1 * 5)) * (levelState * 0.5));
        // System.out.println("Level State: " + levelState);
        // System.out.println("Max Zombie: " + maxZombie);

        return maxZombie;

    }

    public int getZombieRemain() {
        return this.zombieRemain;

    }

    public int getLevelState() {
        return this.levelState;
        
    }

}
