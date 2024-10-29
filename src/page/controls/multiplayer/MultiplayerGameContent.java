package page.controls.multiplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

    // ! Communication !
    private Communication communication;

    private Map<String, List> contents;
    private List<ClientObj> clientObjs;
    private List<Player> players;
    private List<CreateCharacter> characters;

    private Thread listenerThread;

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            Server serverConnect,
            List<ClientObj> clientObjs) {
        // ส่ง Client เรียกใช้งาน Super เพื่อสร้างหน้า GUI ของตัวเอง
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.serverConnect = serverConnect;

        this.isHost = true;

        this.clientObjs = clientObjs;
        this.players = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.communication = this.clientConnect.getCommunication();
        this.communication.setContent("PLAYERS_INFO", clientObjs);

        this.contents = this.communication.getContent();

        // this.communication.setContent("CHARACTERS_INFO", players);

        System.out.println("-=-=-=-=-=| On Game Start - HOST |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(clientObjs);

        initializeEvent();

    }

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            List<ClientObj> clientObjs) {
        // เรียกใช้งาน Super เพื่อสร้างหน้า GUI ของตัวเอง
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.isHost = false;

        this.clientObjs = clientObjs;
        this.players = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.communication = this.clientConnect.getCommunication();
        this.communication.setContent("PLAYERS_INFO", clientObjs);

        this.contents = this.communication.getContent();


        // this.communication.setContent("CHARACTERS_INFO", players);
        System.out.println("-=-=-=-=-=| On Game Start - Client |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(clientObjs);

        initializeEvent();
    }

    private void initializeEvent() {
        addMovementListener(this);

    }

    public void initializeMoment() {
        for (ClientObj clientObj : this.clientObjs) {
            Player player = clientObj.getPlayer();
            if (!players.contains(player)) {
                players.add(player);

            }

            CreateCharacter character = new CreateCharacter(player.getCharacterNo(), false, parentClient);
            if (!characters.contains(character)) {
                characters.add(character);

            }

            if (!(clientObj.equals(super.getClientObjParent()))) {
                content.add(character);

            }
        }

    }

    private void eventListener() {
        listenerThread = new Thread(() -> {
            System.out.println("Multiplayer Game Content > On Event Listening...\n");
            while (true) {
                try {
                    if (clientConnect != null && clientConnect.isConnected() && this.communication != null) {
                        System.out.println("Multiplayer Game Content > On Client Connect!");

                        // clientConnect.clientSideSendObject(this.communication);

                        System.out.println("Contents: " + contents.entrySet());

                        replaceClientObjsFromServer(); // Replace outdated `clientObjs` with updated list

                        SwingUtilities.invokeLater(() -> {
                            initializeMoment();
                            revalidateContent();
                        });

                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Event listener interrupted: " + e.getMessage());
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    System.err.println("Error in event listener: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void updateClientObjList(ClientObj newClientObj) {
        ClientObj clientToRemove = null;
        for (ClientObj existingClient : this.clientObjs) {
            if (existingClient.getId().equals(newClientObj.getId())) {
                clientToRemove = existingClient;
                break;
            }
        }
        
        if (clientToRemove != null) {
            this.clientObjs.remove(clientToRemove);
        }
        
        this.clientObjs.add(newClientObj);
    }
    

    private void replaceClientObjsFromServer() {
        System.out.println("Replace Client Object");
        
        List<ClientObj> updatedClientObjs = contents.get("PLAYERS_INFO");
        System.out.println("Update Client Objs: " + updatedClientObjs);

        for (ClientObj updatedClient : updatedClientObjs) {
            updateClientObjList(updatedClient);
        }
    }

    @Override
    public void run() {

        // System.out.println("!>!<!>!<! On Thread Run !>!<!>!<!");
        eventListener();
        super.run();

    }

    @Override
    public void onPlayerAction(Player player) {
        for (ClientObj clientObj : this.clientObjs) {
            if (this.parentClient.getId().equals(clientObj.getId())) {
                System.out.printf("Player On: x=%d | y=%d\n", player.geDirectionX(), player.geDirectionY());
                
                clientObj.setPlayer(player); 
                updateClientObjList(clientObj);
                break;
            }
        }

        try {
            // System.out.println("!!!!!!!!!! - Sending Communication To Server - !!!!!!!!!!");
            this.communication.setContent("PLAYERS_INFO", clientObjs);
            clientConnect.clientSideSendObject(this.communication);

        } catch (Exception e) {
            System.err.println("Error sending player update to server: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            initializeMoment();
            revalidateContent();
        });
    }

    @Override
    public void onShootBullet(CopyOnWriteArrayList<Bullet> bullets) {
        // System.out.println("!!!Shoot!!!");
        // System.out.println(bullets);

    }

    // TODO - Host
    // 1. สร้าง Thread เพื่อรับข้อมูลจาก Server
    // 2. สร้าง Thread เพื่อส่งข้อมูลทุกอย่างที่เกิดขึ้นจาก Client ไปยัง Server
    // 3. อัพเดท Frame และ Client Obj

    // TODO - Client
}
