package client;

import java.io.*;
import java.net.*;
import java.util.*;

import client.helper.ClientHandler;

public class Server {
    private ServerSocket serverSocket;
    private int serverPort;
    private String serverIp;
    private List<ClientHandler> clients = new ArrayList<>();

    int requiredPlayersToStart = 1;

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

        // สร้าง Thread มารอรับข้อมูลจาก Server เพื่อส่งไปยัง Client
        new Thread(this::handleServerInput).start();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // สร้าง Thread สำหรับ Client เพื่อรอรับข้อมูลจาก Client ที่จะส่งเข้ามายัง
                // Server
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
        for (ClientHandler receiver : clients) {
            if (receiver != sender) {
                System.out.println("Sended Work!");

                // Client : A, B, C, D (Server)

                // A -> B, C
                // B -> A, C
                // C -> A, B

                // โดย D ที่เป็น Server เป็นสื่อกลางในการส่ง

                receiver.sendMessage(message);

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

    public void handleNewConnection(ClientHandler newClient) {
        broadcastMessage("NEW_PLAYER", null);
        if (clients.size() >= requiredPlayersToStart) {
            broadcastMessage("START_COUNTDOWN", null);
        }
    }

    public void handleReadyToStart(ClientHandler readyClient) {
        readyClient.setReady(true);
        if (allClientsReady()) {
            broadcastMessage("START_GAME", null);
        }
    }

    private boolean allClientsReady() {
        return clients.stream().allMatch(ClientHandler::isReady);
    }

    public void setRequiredPlayersToStart(int numOfPlayers) {
        this.requiredPlayersToStart = numOfPlayers;

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
