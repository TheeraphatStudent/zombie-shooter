package models;

import java.io.Serializable;
import java.util.UUID;

import client.Server;

public class ClientObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private String keyId = "";

    private String clientName = "";
    private transient Server serverOnClientSide = null;

    private Player player = null;

    public ClientObj(String clientName, Server serverOnClientSide) {
        this.clientName = clientName;
        this.serverOnClientSide = serverOnClientSide; 
        this.keyId = UUID.randomUUID().toString();

        System.out.println("Client Key Id: " + this.keyId);

    }

    // >>>>>>>>>> Setter >>>>>>>>>>

    public void setPlayer(Player requirePlayer) {
        this.player = requirePlayer;

    }

    // <<<<<<<<<< Getter <<<<<<<<<<

    public String getClientName() {
        return this.clientName;

    }

    public String getClientIp() {
        return this.serverOnClientSide.getServerIp();

    }

    public Server getClientServer() {
        return this.serverOnClientSide;

    }

    public Player getPlayer() {
        return this.player;

    }

    public String getId() {
        return this.keyId;

    }
}
