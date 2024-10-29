package page.controls.multiplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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
import page.controls.GameContent;
import page.home.GameCenter;

public class MultiplayerGameContent extends GameContent implements PlayerBehaviorListener {

    private boolean isHost;
    private Client clientConnect;
    private Server serverConnect;
    private Communication communication;
    private Map<String, List> contents;
    private List<ClientObj> clientObjs;
    private List<Player> players;
    private List<CreateCharacter> characters;
    private ScheduledExecutorService executorService;

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            Server serverConnect,
            List<ClientObj> clientObjs) {
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.serverConnect = serverConnect;
        this.isHost = true;
        this.clientObjs = new CopyOnWriteArrayList<>(clientObjs);
        this.players = new CopyOnWriteArrayList<>();
        this.characters = new CopyOnWriteArrayList<>();
        this.communication = this.clientConnect.getCommunication();
        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
        this.contents = this.communication.getContent();

        System.out.println("-=-=-=-=-=| On Game Start - HOST |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(this.clientObjs);

        initializeEvent();
        startUpdateLoop();
    }

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            List<ClientObj> clientObjs) {
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.isHost = false;
        this.clientObjs = new CopyOnWriteArrayList<>(clientObjs);
        this.players = new CopyOnWriteArrayList<>();
        this.characters = new CopyOnWriteArrayList<>();
        this.communication = this.clientConnect.getCommunication();
        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
        this.contents = this.communication.getContent();

        System.out.println("-=-=-=-=-=| On Game Start - Client |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(this.clientObjs);

        initializeEvent();
        startUpdateLoop();
    }

    private void initializeEvent() {
        addMovementListener(this);
    }

    private void startUpdateLoop() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::update, 0, 16, TimeUnit.MILLISECONDS); // 60 FPS
    }

    private void update() {
        if (clientConnect != null && clientConnect.isConnected() && this.communication != null) {
            replaceClientObjsFromServer();
            SwingUtilities.invokeLater(this::updateGameState);
            sendUpdateToServer();
        }
    }

    @Override
    public void updateGameState() {
        initializeMoment();
        revalidateContent();
    }

    private void sendUpdateToServer() {
        try {
            clientConnect.clientSideSendObject(this.communication);
        } catch (Exception e) {
            System.err.println("Error sending update to server: " + e.getMessage());
        }
    }

    private void initializeMoment() {
        if (this.clientObjs == null) {
            return;
        }

        for (ClientObj clientObj : this.clientObjs) {
            Player player = clientObj.getPlayer();
            boolean alreadyExists = characters.stream()
                    .anyMatch(character -> character.getInitClientObj().getId()
                            .equals(clientObj.getId()));

            if (!alreadyExists) {
                CreateCharacter character = new CreateCharacter(player.getCharacterNo(), false, clientObj);
                players.add(player);
                characters.add(character);
                character.setBounds(player.geDirectionX(), player.geDirectionY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);
                content.add(character);
            } else {
                characters.stream()
                        .filter(character -> character.getInitClientObj().getId().equals(clientObj.getId()))
                        .findFirst()
                        .ifPresent(character -> {
                            character.setLocation(player.geDirectionX(), player.geDirectionY());
                        });
            }
        }
    }

    @Override
    public void disposeContent() {
        if (executorService != null) {
            executorService.shutdown();
        }
        clientConnect.disconnect();
        if (serverConnect != null) {
            serverConnect.closeServer();
        }
        super.disposeContent();
    }

    private void updateClientObjList(ClientObj newClientObj) {
        clientObjs.removeIf(existingClient -> existingClient.getId().equals(newClientObj.getId()));
        clientObjs.add(newClientObj);
    }

    private void replaceClientObjsFromServer() {
        List<ClientObj> updatedClientObjs = contents.get("PLAYERS_INFO");
        if (updatedClientObjs != null) {
            for (ClientObj updatedClient : updatedClientObjs) {
                updateClientObjList(updatedClient);
            }
        }
    }

    @Override
    public void onPlayerAction(Player player) {
        for (ClientObj clientObj : this.clientObjs) {
            if (this.parentClient.getId().equals(clientObj.getId())) {
                clientObj.setPlayer(player);
                updateClientObjList(clientObj);
                break;
            }
        }
        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
    }

    @Override
    public void onShootBullet(CopyOnWriteArrayList<Bullet> bullets) {
        // Implement bullet synchronization if needed
    }
}
