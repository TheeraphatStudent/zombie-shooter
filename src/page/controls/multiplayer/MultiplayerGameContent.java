package page.controls.multiplayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.Client;
import client.Server;
import components.character.CreateCharacter;
import models.ClientObj;
import models.Communication;
import models.Player;
import page.controls.GameContent;
import page.home.GameCenter;

public class MultiplayerGameContent extends GameContent {

    private boolean isHost;
    private Client clientConnect;
    private Server serverConnect;

    // ! Communication !
    private Communication communication;

    private Map<String, List> contents;
    private List<ClientObj> clientObjs;
    private List<Player> players;
    private List<CreateCharacter> characters;

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            Server serverConnect,
            List<ClientObj> clientObjs,
            Communication communication) {
        // ส่ง Client เรียกใช้งาน Super เพื่อสร้างหน้า GUI ของตัวเอง
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.serverConnect = serverConnect;

        this.isHost = true;

        // ตัวติดต่อศื่อสารระหว่าง Server และ Client
        this.communication = communication;
        this.contents = this.communication.getContent();
        this.clientObjs = clientObjs;

        // for (ClientObj clientObj : clientObjs) {
        // Player player = clientObj.getPlayer();
        // players.add(player);

        // }
        this.players = new ArrayList<>();
        this.characters = new ArrayList<>();

        // this.communication.setContent("CHARACTERS_INFO", players);

        System.out.println("-=-=-=-=-=| On Game Start - HOST |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(clientObjs);

    }

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            List<ClientObj> clientObjs,
            Communication communication) {
        // ส่ง Client เรียกใช้งาน Super เพื่อสร้างหน้า GUI ของตัวเอง
        super(gameCenter, clientObjSide);

        this.clientConnect = clientConnect;
        this.isHost = false;

        // ตัวติดต่อศื่อสารระหว่าง Server และ Client
        this.communication = communication;
        this.contents = this.communication.getContent();

        this.clientObjs = clientObjs;
        this.players = new ArrayList<>();
        this.characters = new ArrayList<>();

        // this.communication.setContent("CHARACTERS_INFO", players);
        System.out.println("-=-=-=-=-=| On Game Start - Client |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(clientObjs);

    }

    public void initializeMoment() {
        System.out.println("Multiplayer Initialize Moment Work!");

        for (ClientObj clientObj : clientObjs) {
            Player player = clientObj.getPlayer();
            if (!players.contains(player)) {
                System.out.println("Adding Player: " + player);
                players.add(player);
            }

            CreateCharacter character = player.getCharacter();
            if (!characters.contains(character)) {
                System.out.println("Adding Character: " + character);
                characters.add(character);
            }

            if (!(clientObj.equals(super.getClientObjParent()))) {
                System.out.println("Client Name: " + clientObj.getClientName());
                content.add(character);
            }
        }

        revalidateContent();
    }

    private void eventListener() {
        Thread listenerThread = new Thread(() -> {
            System.out.println("On Event Listening...\n");
            while (true) {
                try {
                    // clientConnect.clientSideSendObject(communication);

                    // this.contents = communication.getContent();
                    // clientObjs = this.contents.get("");

                    initializeMoment();
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

    @Override
    public void run() {

        System.out.println("!>!<!>!<! On Thread Run !>!<!>!<!");
        eventListener();
        initializeMoment();

        super.run();

    }

    // TODO - Host
    // 1. สร้าง Thread เพื่อรับข้อมูลจาก Server
    // 2. สร้าง Thread เพื่อส่งข้อมูลทุกอย่างที่เกิดขึ้นจาก Client ไปยัง Server
    // 3. อัพเดท Frame และ Client Obj

    // TODO - Client
}
