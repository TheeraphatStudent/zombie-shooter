package models;

public class ClientObj {

    String clientName;
    String clientIp;

    public ClientObj(String clientName, String clientIp) {
        this.clientName = clientName;
        this.clientIp = clientIp;

    }

    public String getClientName() {
        return this.clientName;

    }

    public String getClientIp() {
        return this.clientIp;

    }
}
