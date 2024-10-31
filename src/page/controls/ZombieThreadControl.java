package page.controls;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import components.character.CreateCharacter;
import models.ClientObj;
import models.Player;
import models.Zombie.Behavior;
import models.Zombie.Info;
import utils.UseCharacter;

public class ZombieThreadControl extends Thread {
    private GameContent content;

    private Behavior behavior;
    private CreateCharacter zombie;

    private Player player;
    private CreateCharacter character;

    // On Multiplayer Mode
    private List<Player> players;
    private List<CreateCharacter> characters;
    private Info zombieInfo;

    private volatile boolean running = true;
    private volatile Timer biteTimer;
    private volatile boolean isBiting = false;

    private boolean isMultiplayer;

    // Single Player
    public ZombieThreadControl(
            CreateCharacter zombie,
            Behavior behavior,
            Info info,
            Player player,
            CreateCharacter character,
            GameContent content) {
        this.zombie = zombie;
        this.behavior = behavior;
        this.zombieInfo = info;

        this.player = player;
        this.character = character;
        this.content = content;
        this.isMultiplayer = false;

        biteTimer = new Timer(0, e -> biteInArea());
    }

    // Multiplayer
    public ZombieThreadControl(
            CreateCharacter zombie,
            Behavior behavior,
            List<Player> players,
            List<CreateCharacter> characters,
            GameContent content) {
        this.zombie = zombie;
        this.behavior = behavior;

        this.players = players;
        this.characters = characters;
        this.content = content;
        this.isMultiplayer = true;

        biteTimer = new Timer(0, e -> biteInArea());
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(16);

                // อัพเดทตำแหน่งการเดินของ Zombie
                behavior.updateZombiePosition();
                zombieInfo.setLocation(behavior.getMovedX(), behavior.getMovedY());

                content.onZombieUpdate(zombieInfo);

                biteTimer.start();

                if (!isBiting && checkIsPlayerInRange()) {
                    biteTimer.setDelay(1000);
                    isBiting = true;

                } else if (isBiting && !checkIsPlayerInRange()) {
                    isBiting = false;

                    biteTimer.stop();
                    // biteTimer.setDelay(0);

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
        if (this.isMultiplayer) {
            biteInAreaMultiplayer();

        } else {
            biteInAreaSinglePlayer();

        }
    }

    private void biteInAreaSinglePlayer() {
        if (isPlayerInRange(this.zombie, this.character)) {
            System.out.println("On Zombie Damage: Single Mode");
            player.setPlayerHealth(player.getPlayerHealth() - (int) behavior.getZombieDamage());
            character.setCharacterHp(player.getPlayerHealth());

            if (player.getPlayerHealth() <= 0) {
                System.out.println("Player is dead!");
                this.content.disposeContent();

            }
        }
    }

    private void biteInAreaMultiplayer() {

        Rectangle zombieHitbox = new UseCharacter().getCharacterHitbox(zombie);
        List<ClientObj> updateClientObjects = new ArrayList<>();

        for (int i = 0; i < characters.size(); i++) {
            CreateCharacter character = characters.get(i);
            Player player = players.get(i);

            Rectangle playerHitbox = new UseCharacter().getCharacterHitbox(character);

            if (zombieHitbox.intersects(playerHitbox)) {
                System.out.println("On Zombie Damage: Multiplayer Mode");
                player.setPlayerHealth(player.getPlayerHealth() - (int) behavior.getZombieDamage());
                character.setCharacterHp(player.getPlayerHealth());

                if (player.getPlayerHealth() <= 0) {
                    player.setInfectedPlayer(true);

                }
            }

            updateClientObjects.get(i).setPlayer(player);
        }

        this.content.onPlayerTakeDamages(updateClientObjects);
    }

    public CreateCharacter getZombie() {
        return this.zombie;
    }

    public void stopMovement() {
        running = false;
    }
}
