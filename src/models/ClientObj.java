package models;

import java.io.Serializable;

import client.Server;
import components.character.CreateCharacter;

public class ClientObj implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientName = "";
    private transient Server serverOnClientSide = null;

    private Player player = null;

    public ClientObj(String clientName, Server serverOnClientSide) {
        this.clientName = clientName;
        this.serverOnClientSide = serverOnClientSide;

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
}
