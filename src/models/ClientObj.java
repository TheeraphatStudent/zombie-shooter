package models;

import java.io.Serializable;

import client.Server;

public class ClientObj implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient String clientName;
    private transient Server serverOnClientSide;

    public ClientObj(String clientName, Server serverOnClientSide) {
        this.clientName = clientName;
        this.serverOnClientSide = serverOnClientSide;

    }

    public String getClientName() {
        return this.clientName;

    }

    public String getClientIp() {
        return this.serverOnClientSide.getServerIp();

    }

    public Server getClientServer() {
        return this.serverOnClientSide;

    }
}
