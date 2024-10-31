package page.controls;

import java.awt.Rectangle;
import java.util.List;

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

    // On Multiplayer Mode
    private List<Player> players;
    private List<CreateCharacter> characters;

    private volatile boolean running = true;
    private volatile Timer biteTimer;
    private volatile boolean isBiting = false;

    private boolean isMultiplayer;

    // Single Player
    public ZombieMovementThread(CreateCharacter zombie, Zombie behavior, Player player, CreateCharacter character,
            GameContent content) {
        this.zombie = zombie;
        this.behavior = behavior;
        this.player = player;
        this.character = character;
        this.content = content;
        this.isMultiplayer = false;

        biteTimer = new Timer(1000, e -> biteInArea());
    }

    // Multiplayer
    public ZombieMovementThread(
            CreateCharacter zombie,
            Zombie behavior,
            List<Player> players,
            List<CreateCharacter> characters,
            GameContent content) {
        this.zombie = zombie;
        this.behavior = behavior;
        this.players = players;
        this.characters = characters;
        this.content = content;
        this.isMultiplayer = true;

        biteTimer = new Timer(1000, e -> biteInArea());
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(16);

                behavior.updateZombiePosition();

                if (!isBiting && checkIsPlayerInRange()) {
                    isBiting = true;
                    biteTimer.start();

                } else if (isBiting && !checkIsPlayerInRange()) {
                    isBiting = false;
                    biteTimer.stop();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }

        biteTimer.stop();
    }

    private boolean checkIsPlayerInRange() {
        if (this.isMultiplayer) {
            return isAnyPlayerInRange();

        } else {
            return isPlayerInRange(this.zombie, this.character);
        }
    }

    private boolean isAnyPlayerInRange() {
        Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
        for (CreateCharacter character : characters) {
            Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(character);
            if (zombieHitbox.intersects(playerHitbox)) {
                return true;
            }
        }
        return false;
    }


    private boolean isPlayerInRange(CreateCharacter zombie, CreateCharacter character) {
        Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
        Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(character);
        return zombieHitbox.intersects(playerHitbox);
    }

    private void biteInArea() {
        if (isMultiplayer) {
            biteInAreaMultiplayer();
        } else {
            biteInAreaSinglePlayer();
        }
    }

    private void biteInAreaSinglePlayer() {
        System.out.println("On Zombie Damage Single Mode");
        
        if (isPlayerInRange(this.zombie, this.character)) {
            player.setPlayerHealth(player.getPlayerHealth() - (int) behavior.getZombieDamage());
            character.setCharacterHp(player.getPlayerHealth());
            
            if (player.getPlayerHealth() <= 0) {
                System.out.println("Player is dead!");
                this.content.disposeContent();
            }
        }
    }
    
    private void biteInAreaMultiplayer() {
        System.out.println("On Zombie Damage Multiplayer Mode");

        Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
        for (int i = 0; i < characters.size(); i++) {
            CreateCharacter character = characters.get(i);
            Player player = players.get(i);
            Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(character);

            if (zombieHitbox.intersects(playerHitbox)) {
                player.setPlayerHealth(player.getPlayerHealth() - (int) behavior.getZombieDamage());
                character.setCharacterHp(player.getPlayerHealth());

                if (player.getPlayerHealth() <= 0) {
                    player.setInfectedPlayer(true);

                }
            }
        }
        
        // Send update player to another clients
    }

    public CreateCharacter getZombie() {
        return this.zombie;
    }

    public void stopMovement() {
        running = false;
    }
}
