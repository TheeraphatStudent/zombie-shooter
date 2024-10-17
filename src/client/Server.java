package client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private int serverPort;
    private String serverIp;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        System.out.println("Create new server");

        this.serverPort = getServerPort();
        this.serverIp = getServerIp();

        try {
            this.serverSocket = new ServerSocket(serverPort);
            System.out.println("Server IP: " + serverIp);
            System.out.println("Server is listening on port " + serverPort);
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("Server Start");

        // Start a new thread for handling console input (messages from the server)
        new Thread(this::handleServerInput).start();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                System.out.println("Client added. Total clients: " + clients.size());
            }
        } catch (IOException e) {
            System.out.println("Error accepting client connection: " + e.getMessage());
        }
    }

    private void handleServerInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            broadcastMessage("Server Say: " + message, null); 
        
        }

    }

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);

            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client removed. Total clients: " + clients.size());
    }

    public int getServerPort() {
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
            System.out.println("Error getting server IP: " + e.getMessage());
        }
        return null;
    }
}
