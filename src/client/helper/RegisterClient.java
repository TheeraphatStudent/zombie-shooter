package client.helper;

import java.io.Serializable;

import models.ClientObj;

public class RegisterClient implements Serializable {
    private static final long serialVersionUID = 1L;

    private ClientObj clientIdentify;

    public RegisterClient(ClientObj clientObj) {
        this.clientIdentify = clientObj;

    }

    public ClientObj getAuthClient() {
        return this.clientIdentify;

    }

}
