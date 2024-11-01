package page.controls.multiplayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import client.Client;
import client.Server;
import components.character.CreateCharacter;
import models.Bullet;
import models.ClientObj;
import models.Communication;
import models.Player;
import models.Zombie.Behavior;
import models.Zombie.Info;
import page.controls.GameContent;
import page.home.GameCenter;

public class MultiplayerGameContent extends GameContent implements GameContentListener {

    private boolean isHost;

    private Client clientConnect;
    private Server serverConnect;
    private volatile Communication communication;
    private volatile Map<String, List> contents;

    private volatile CopyOnWriteArrayList<ClientObj> clientObjs;
    private volatile CopyOnWriteArrayList<Player> players;
    private volatile CopyOnWriteArrayList<CreateCharacter> characters;
    private Map<String, CreateCharacter> zombieMap = new ConcurrentHashMap<>();
    private volatile CopyOnWriteArrayList<Info> updatedZombies;
    private volatile CopyOnWriteArrayList<Bullet> updatedBullets;

    // private ScheduledExecutorService executorService;
    private ScheduledExecutorService updateExecutor;
    private ScheduledExecutorService sendUpdateExecutor;
    private ExecutorService threadPool;

    // ! ----- Host ----- !
    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            Server serverConnect,
            List<ClientObj> clientObjs) {
        this.isHost = true;

        super(gameCenter, clientObjSide);
        super.activeMultiplayerMode(clientObjs, this.isHost);

        this.clientObjs = new CopyOnWriteArrayList<>(clientObjs);

        this.clientConnect = clientConnect;
        this.serverConnect = serverConnect;

        System.out.println("-=-=-=-=-=| On Game Start - HOST |=-=-=-=-=-\n");

        initializeEvent();
        startUpdateLoop();
    }

    // ! ----- Client ----- !
    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            List<ClientObj> clientObjs) {
        this.isHost = false;

        super(gameCenter, clientObjSide);
        super.activeMultiplayerMode(clientObjs, this.isHost);

        this.clientObjs = new CopyOnWriteArrayList<>(clientObjs);

        this.clientConnect = clientConnect;

        System.out.println("-=-=-=-=-=| On Game Start - Client |=-=-=-=-=-\n");

        initializeEvent();
        startUpdateLoop();
    }

    private void initializeEvent() {
        this.updatedBullets = new CopyOnWriteArrayList<>(super.bullets);
        this.players = new CopyOnWriteArrayList<>();
        this.characters = new CopyOnWriteArrayList<>();
        this.updatedZombies = new CopyOnWriteArrayList<>();

        this.communication = this.clientConnect.getCommunication();
        this.contents = this.communication.getContent();
        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
        this.communication.setContent("BULLETS_INFO", this.updatedBullets);
        this.communication.setContent("ZOMBIES_INFO", this.updatedZombies);

        this.clientConnect.clientSideSendObject(this.communication);
        addMovementListener(this);

        System.out.println(contents.entrySet());
        System.out.println(this.clientObjs);
    }

    private void startUpdateLoop() {
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // executorService = Executors.newSingleThreadScheduledExecutor();
        updateExecutor = Executors.newSingleThreadScheduledExecutor();
        sendUpdateExecutor = Executors.newSingleThreadScheduledExecutor();

        updateExecutor.scheduleAtFixedRate(this::update, 1, 16, TimeUnit.MILLISECONDS);
        sendUpdateExecutor.scheduleAtFixedRate(this::sendUpdateToServer, 1, 100, TimeUnit.MILLISECONDS);

        // ? ON TEST
        // updateExecutor.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.SECONDS);
        // sendUpdateExecutor.scheduleAtFixedRate(this::sendUpdateToServer, 0, 1100,
        // TimeUnit.MILLISECONDS);
    }

    private void update() {
        if (clientConnect != null && clientConnect.isConnected() && this.communication != null) {
            this.communication = clientConnect.getCommunication();
            this.contents = this.communication.getContent();
            System.out.println(contents.entrySet());

            initializeMoment();
            // onUpdateBulletsFromServer();
            // onUpdateZombieFromServer();

            CompletableFuture.allOf(
                    CompletableFuture.runAsync(this::onUpdateClientObjFromServer, threadPool),
                    CompletableFuture.runAsync(this::onUpdateBulletsFromServer, threadPool),
                    CompletableFuture.runAsync(this::onUpdateZombieFromServer, threadPool)

            ).thenRun(() -> {
                SwingUtilities.invokeLater(this::revalidateContent);
                // this.updatedBullets.clear();
            });

            // onUpdateClientObjFromServer();
        }
    }

    private void sendUpdateToServer() {
        try {
            clientConnect.clientSideSendObject(this.communication);
        } catch (Exception e) {
            System.err.println("Error sending update to server: " + e.getMessage());
        }
    }

    private void updateCharacter(CreateCharacter character, int targetX, int targetY, boolean isMovedLeft,
            boolean isInfected) {
        int currentX = character.getX();
        int currentY = character.getY();

        int deltaX = targetX - currentX;
        int deltaY = targetY - currentY;

        double smoothingFactor = 0.1;
        int smoothX = (int) (currentX + deltaX * smoothingFactor);
        int smoothY = (int) (currentY + deltaY * smoothingFactor);

        // System.out.printf("SmoothX: %d, SmoothY: %d\n", smoothX, smoothY);

        character.setLocation(smoothX, smoothY);
        character.setCharacterMoveLeft(isMovedLeft);
        character.setCharacterInfected(isInfected);

    }

    private void initializeMoment() {
        if (this.clientObjs == null || this.clientObjs.isEmpty()) {
            return;
        }

        for (ClientObj clientObj : this.clientObjs) {
            if (this.parentClient.getId().equals(clientObj.getId())) {
                continue;
            }

            System.out.println("Processing Client: " + clientObj.getClientName());
            Player player = clientObj.getPlayer();

            CreateCharacter existingCharacter = null;
            for (CreateCharacter character : characters) {
                if (character.getInitClientObj().getId().equals(clientObj.getId())) {
                    existingCharacter = character;
                    break;
                }
            }

            if (existingCharacter == null) {
                CreateCharacter character = new CreateCharacter(player.getCharacterNo(), false, clientObj);
                players.add(player);
                characters.add(character);
                character.setBounds(player.getDirectionX(), player.getDirectionY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);
                content.add(character);

                System.out.println("Created new character for client: " + clientObj.getClientName());
            } else {
                System.out.printf("Updating character position for client: %s, x: %d, y: %d\n",
                        clientObj.getClientName(), player.getDirectionX(), player.getDirectionY());

                updateCharacter(existingCharacter, player.getDirectionX(), player.getDirectionY(),
                        player.getPlayerIsMovedLeft(), player.getInfectedStatus());

                System.out.println("Updated another character position: " + clientObj.getClientName());
                System.out.printf("Player position x: %d, y: %d\n", player.getDirectionX(), player.getDirectionY());
            }

            // revalidateContent();
        }
    }

    @Override
    public void disposeContent() {
        // if (executorService != null) {
        // executorService.shutdown();
        // }

        if (updateExecutor != null) {
            updateExecutor.shutdown();
        }

        if (sendUpdateExecutor != null) {
            sendUpdateExecutor.shutdown();
        }

        if (threadPool != null) {
            threadPool.shutdown();
        }

        if (serverConnect != null) {
            serverConnect.closeServer();
        }

        clientConnect.disconnect();

        super.disposeContent();
    }

    // */*/*/*/*/* Receive And Update Content From Server */ */ */ */ */

    private void onUpdateClientObjFromServer() {
        // System.out.println("OnUpdateClientObjFromServer...");

        // ! Update Players
        List<ClientObj> updatedClientObjs = (CopyOnWriteArrayList<ClientObj>) this.contents.get("PLAYERS_INFO");

        this.clientObjs.clear();
        this.clientObjs.addAll(updatedClientObjs);

        // System.out.println("=============== Update Client Obj ===============\n");
        // System.out.println(updatedClientObjs);

    }

    private void onUpdateBulletsFromServer() {
        this.updatedBullets = (CopyOnWriteArrayList<Bullet>) contents.get("BULLETS_INFO");
        System.out.println("Update bullets...");
        System.out.println(this.updatedBullets);

        if (this.updatedBullets != null) {
            for (Bullet shootBullet : this.updatedBullets) {
                SwingUtilities.invokeLater(() -> super.addBullet(shootBullet));
                ;

            }

            this.updatedBullets.clear();
        }

        // this.communication.setContent("BULLETS_INFO", this.updatedBullets);

    }

    private void onUpdateZombieFromServer() {
        List<Info> receivedZombies = (CopyOnWriteArrayList<Info>) contents.get("ZOMBIES_INFO");
        System.out.println("Updating zombies...");
        System.out.println(receivedZombies);

        if (receivedZombies != null) {
            // this.updatedZombies.clear();
            // this.updatedZombies.addAll(receivedZombies);

            for (Info zombieInfo : receivedZombies) {
                SwingUtilities.invokeLater(() -> updateZombieOnFrame(zombieInfo));
                ;

            }

            zombieMap.entrySet()
                    .removeIf(entry -> receivedZombies.stream().noneMatch(info -> info.getId().equals(entry.getKey())));

            revalidateContent();
        }
    }

    private void updateZombieOnFrame(Info zombieInfo) {
        if (!zombieInfo.isAlive()) {
            CreateCharacter zombieCharacter = zombieMap.remove(zombieInfo.getId());
            if (zombieCharacter != null) {
                super.content.remove(zombieCharacter);

                super.zombiesCharacters.remove(zombieCharacter);
                zombieCharacter.setVisible(false);
                zombieCharacter.removeAll();

                this.updatedZombies.removeIf(zombie -> zombie.getId().equals(zombieInfo.getId()));
                this.communication.setContent("ZOMBIES_INFO", this.updatedZombies);

                // super.ZOMBIE_REMAIN--;
                // super.state.setZombieRemain(super.ZOMBIE_REMAIN);
                // super.levelState.setZombieOnState(super.ZOMBIE_REMAIN);
            }
        } else {
            CreateCharacter zombieCharacter = zombieMap.computeIfAbsent(zombieInfo.getId(), id -> {
                CreateCharacter newZombie = new CreateCharacter(this, id, zombieInfo.getProfile());
                newZombie.setBounds(zombieInfo.getX(), zombieInfo.getY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);

                super.content.add(newZombie);
                super.zombiesCharacters.add(newZombie);
                return newZombie;
            });

            zombieCharacter.setLocation(zombieInfo.getX(), zombieInfo.getY());
            zombieCharacter.setCharacterHp(zombieInfo.getHealth());
            zombieCharacter.repaint();
        }

    }

    @Override
    public void onPlayerAction(Player player) {
        System.out.printf("????? Player: x=%d | y=%d\n\n", player.getDirectionX(), player.getDirectionY());

        boolean updated = false;
        for (int i = 0; i < this.clientObjs.size(); i++) {
            ClientObj clientObj = this.clientObjs.get(i);
            if (this.parentClient.getId().equals(clientObj.getId())) {
                clientObj.setPlayer(player);
                updated = true;
                System.out.println("On Player Action > Updated player for client: " + clientObj.getClientName());

                this.clientObjs.set(i, clientObj);
            }
        }

        if (!updated) {
            System.out.println("Not find matching client for player update ):");
        }

        // update();
        // SwingUtilities.invokeLater(this::revalidateContent);
    }

    public void onPlayerTakeDamage(List<ClientObj> refClientObj) {
        for (int i = 0; i < this.clientObjs.size(); i++) {
            ClientObj updateObj = refClientObj.get(i);
            this.clientObjs.set(i, updateObj);

        }

    };

    @Override
    public void onShootBullet(CopyOnWriteArrayList<Bullet> bullets) {
        this.communication.setContent("BULLETS_INFO", bullets);

        // update();
        // SwingUtilities.invokeLater(this::revalidateContent);

    }

    @Override
    public void onZombieUpdate(Info updateZombie) {
        boolean zombieExists = false;
        for (int i = 0; i < this.updatedZombies.size(); i++) {
            Info existingZombie = this.updatedZombies.get(i);
            if (existingZombie.getId().equals(updateZombie.getId())) {
                this.updatedZombies.set(i, updateZombie);
                zombieExists = true;
                break;
            }
        }

        if (!zombieExists) {
            this.updatedZombies.add(updateZombie);
        }

        this.communication.setContent("ZOMBIES_INFO", this.updatedZombies);

        // SwingUtilities.invokeLater(() -> updateZombieOnFrame(updateZombie));
    }

}