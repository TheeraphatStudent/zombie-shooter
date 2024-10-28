package page.controls;

import java.awt.Rectangle;

import javax.swing.Timer;

import components.character.CreateCharacter;
import models.Player;
import models.Zombie;
import utils.UseCharacter;

public class ZombieMovementThread extends Thread {
    private GameContent content;

    private Zombie behavior;
    private CreateCharacter zombie;

    private Player player;
    private CreateCharacter character;

    private volatile boolean running = true;
    private volatile Timer biteTimer;
    private volatile boolean isBiting = false;

    public ZombieMovementThread(CreateCharacter zombie, Zombie behavior, Player player, GameContent content) {
        this.zombie = zombie;
        this.behavior = behavior;
        this.player = player;
        this.character = this.player.getCharacter();
        this.content = content;

        biteTimer = new Timer(1000, e -> biteInArea());
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(16);

                // behavior = new Zombie(character, zombie, GameContent.this,nul);
                behavior.updateZombiePosition();

                if (!isBiting && isPlayerInRange()) {
                    isBiting = true;
                    biteInArea();

                    biteTimer.start();

                    if (isBiting && !isPlayerInRange()) {
                        isBiting = false;
                        biteTimer.stop();

                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;

            }
        }
        biteTimer.stop();
    }

    private boolean isPlayerInRange() {
        Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
        Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(character);
        return zombieHitbox.intersects(playerHitbox);

    }

    private void biteInArea() {
        if (isPlayerInRange()) {
            // System.out.println("Bite!");

            // character.setCharacterHp(character.getCharacterHp() - (int) behavior.getZombieDamage());
            player.setPlayerHealth(player.getPlayerHealth() - (int) behavior.getZombieDamage());
            character.setCharacterHp(player.getPlayerHealth());

            if (player.getPlayerHealth() <= 0) {
                System.out.println("Player is dead!");

                this.content.disposeContent();
                // dispose();

            }
        }
    }

    public CreateCharacter getZombie() {
        return this.zombie;

    }

    public void stopMovement() {
        running = false;
    }
}