package client.helper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerHelper {
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
}
