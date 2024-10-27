package page.controls.multiplayer;

import java.io.Serializable;
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
        super.setPlayerToCenter(false);

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

        // this.communication.setContent("CHARACTERS_INFO", players);

        System.out.println("-=-=-=-=-=| On Game Start - HOST |=-=-=-=-=-\n");

    }

    public MultiplayerGameContent(
            GameCenter gameCenter,
            ClientObj clientObjSide,
            Client clientConnect,
            List<ClientObj> clientObjs,
            Communication communication) {
        // ส่ง Client เรียกใช้งาน Super เพื่อสร้างหน้า GUI ของตัวเอง
        super(gameCenter, clientObjSide);
        super.setPlayerToCenter(false);

        this.clientConnect = clientConnect;
        this.isHost = false;

        // ตัวติดต่อศื่อสารระหว่าง Server และ Client
        this.communication = communication;
        this.contents = this.communication.getContent();

        this.clientObjs = clientObjs;

        // this.communication.setContent("CHARACTERS_INFO", players);
        System.out.println("-=-=-=-=-=| On Game Start - Client |=-=-=-=-=-\n");
        System.out.println(contents.entrySet());
        System.out.println(clientObjs);

    }

    public void initializeMoment() {
        System.out.println("Multiplayer Initialize Moment Work!");

        for (ClientObj clientObj : clientObjs) {
            Player player = clientObj.getPlayer();
            players.add(player);

            CreateCharacter character = player.getCharacter();
            characters.add(character);

            if (!(clientObj == super.parentClient)) {
                System.out.println("Client Name: " + clientObj.getClientName());
                content.add(character);

            }

            revalidateContent();

        }

    }

    public void eventListener() {
        System.out.println("On Event Listening...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                clientConnect.clientSideSendObject(communication);

                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    @Override
    public void run() {
        super.run();
        System.out.println("!>!<!>!<! On Thread Run !>!<!>!<!");
        eventListener();

    }

    // TODO - Host
    // 1. สร้าง Thread เพื่อรับข้อมูลจาก Server
    // 2. สร้าง Thread เพื่อส่งข้อมูลทุกอย่างที่เกิดขึ้นจาก Client ไปยัง Server
    // 3. อัพเดท Frame และ Client Obj

    // TODO - Client
}
