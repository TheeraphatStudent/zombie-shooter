package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    private int serverPort;
    private String serverIp;

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            // Get the server's IP address
            System.out.println("Server IP: " + serverIp);
            System.out.println("Server is listening on port " + serverPort);

            // Wait for a client to connect
            // Socket clientSocket = serverSocket.accept();
            // System.out.println("Client connected");

            serverSocket.close();
            // clientSocket.close();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public int getServerPort() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            this.serverPort = serverSocket.getLocalPort();
            serverSocket.close();
            return this.serverPort;

        } catch (IOException exc) {
            exc.printStackTrace();

        }

        return 0;

    }

    public String getServerIp() {
        try {
            this.serverIp = InetAddress.getLocalHost().getHostAddress();
            return this.serverIp;

        } catch (Exception e) {
        }

        return null;

    }
}
