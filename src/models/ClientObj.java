package models;

import client.Server;

public class ClientObj {

    String clientName;
    Server serverOnClientSide;

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
