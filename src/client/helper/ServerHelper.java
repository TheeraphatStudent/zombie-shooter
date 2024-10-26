package client.helper;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerHelper {

    private Map<String, Object[]> models;

    public int getAlreadyPort() {
        try {
            ServerSocket tempSocket = new ServerSocket(0);
            int port = tempSocket.getLocalPort();
            tempSocket.close();

            return port;
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return 0;

    }

    public String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException e) {
            return null;

        }
    }
}

