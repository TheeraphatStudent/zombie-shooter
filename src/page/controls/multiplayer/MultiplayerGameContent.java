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
        this.clientConnect.clientSideSendObject(this.communication);
        addMovementListener(this);
    }

    private void startUpdateLoop() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::update, 100, 16, TimeUnit.MILLISECONDS);
    }

    private void update() {
        if (clientConnect != null && clientConnect.isConnected() && this.communication != null) {
            System.out.println("Updating game state...");
            replaceClientObjsFromServer();
            initializeMoment();
            SwingUtilities.invokeLater(this::revalidateContent);
            sendUpdateToServer();
        }
    }

    private void sendUpdateToServer() {
        try {
            clientConnect.clientSideSendObject(this.communication);

        } catch (Exception e) {
            System.err.println("Error sending update to server: " + e.getMessage());
        }
    }

    private void initializeMoment() {
        if (this.clientObjs == null || this.clientObjs.isEmpty()) {
            System.out.println("No client objects to initialize");
            return;
        }

        for (ClientObj clientObj : this.clientObjs) {
            System.out.println("Processing Client: " + clientObj.getClientName());

            Player player = clientObj.getPlayer();
            if (player == null) {
                System.out.println("Warning: Player is null for client " + clientObj.getClientName());
                continue;
            }
            CreateCharacter existingCharacter = characters.stream()
                    .filter(character -> character.getInitClientObj().getId().equals(clientObj.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingCharacter == null) {
                CreateCharacter character = new CreateCharacter(player.getCharacterNo(), false, clientObj);
                players.add(player);
                characters.add(character);
                character.setBounds(player.geDirectionX(), player.geDirectionY(), CHARACTER_WIDTH, CHARACTER_HEIGHT);
                content.add(character);
                System.out.println("Created new character for client: " + clientObj.getClientName());
            } else {
                existingCharacter.setLocation(player.geDirectionX(), player.geDirectionY());
                System.out.println("Updated existing character position for client: " + clientObj.getClientName());
            }
        }

        revalidateContent();
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

    // private void updateClientObjList(ClientObj newClientObj) {
    //     clientObjs.removeIf(existingClient -> existingClient.getId().equals(newClientObj.getId()));
    //     clientObjs.add(newClientObj);
    // }

    private void replaceClientObjsFromServer() {
        List<ClientObj> updatedClientObjs = contents.get("PLAYERS_INFO");
        System.out.println("Before Update ClientObj: " + updatedClientObjs);
    
        if (updatedClientObjs != null && !updatedClientObjs.isEmpty()) {
            // Create a new list to store updated clients
            List<ClientObj> newClientObjs = new ArrayList<>();
    
            // Update existing clients and add new ones
            for (ClientObj updatedClient : updatedClientObjs) {
                ClientObj existingClient = findClientById(updatedClient.getId());
                if (existingClient != null) {
                    // Update existing client
                    existingClient.setPlayer(updatedClient.getPlayer());
                    newClientObjs.add(existingClient);
                } else {
                    // Add new client
                    newClientObjs.add(updatedClient);
                }
            }
    
            // Replace the clientObjs list with the new list
            this.clientObjs.clear();
            this.clientObjs.addAll(newClientObjs);
        } else {
            System.out.println("Warning: Received empty or null PLAYERS_INFO");
        }
    
        System.out.println("After Update >> clientObjs: " + this.clientObjs);
    
        // Update the communication object with the latest clientObjs
        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
    }

    private ClientObj findClientById(String id) {
        for (ClientObj client : this.clientObjs) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    @Override
    public void onPlayerAction(Player player) {
        boolean updated = false;
        for (ClientObj clientObj : this.clientObjs) {
            if (this.parentClient.getId().equals(clientObj.getId())) {
                clientObj.setPlayer(player);
                updated = true;
                System.out.println("Updated player for client: " + clientObj.getClientName());
                break;
            }
        }

        if (!updated) {
            System.out.println("Warning: Could not find matching client for player update");
        }

        this.communication.setContent("PLAYERS_INFO", this.clientObjs);
        sendUpdateToServer();
    }


    @Override
    public void onShootBullet(CopyOnWriteArrayList<Bullet> bullets) {
        // Implement bullet synchronization if needed
    }
}
