package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import client.helper.ClientHandler;
import client.helper.ServerHelper;
import components.character.CreateCharacter;
import models.ClientObj;

public class Server extends ServerHelper implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ServerSocket serverSocket;
    private final int serverPort;
    private final String serverIp;

    // Communication Contain
    // private final List<ClientHandler> clients = Collections.synchronizedList(new
    // ArrayList<>());
    // private final List<ClientObj> clientObjs = Collections.synchronizedList(new
    // ArrayList<>());
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<ClientObj> clientObjs = new CopyOnWriteArrayList<>();

    private volatile int requiredPlayersToStart = 1;
    private volatile boolean gameStarting = false;

    public Server() {
        // super();
        System.out.println("Create new server");
        this.serverPort = getAlreadyPort();
        this.serverIp = getServerIp();

        try {
            this.serverSocket = new ServerSocket(serverPort);
            System.out.println("Server IP: " + serverIp);
            System.out.println("Server is listening on port " + serverPort);

        } catch (IOException e) {
            throw new RuntimeException("Error creating server socket: " + e.getMessage(), e);

        }
    }

    public void start() {
        System.out.println("Server Start");

        // Start server input handler in daemon thread
        Thread inputHandler = new Thread(this::handleServerInput);
        inputHandler.setDaemon(true);
        inputHandler.start();

        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                if (gameStarting || clients.size() >= requiredPlayersToStart) {
                    // Reject new connections if game is starting or full
                    try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                        out.println("GAME_FULL");
                        clientSocket.close();
                        continue;
                    }
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);

                clients.add(clientHandler);
                System.out.println("Client added. Total clients: " + clients.size());

                Thread handlerThread = new Thread(clientHandler);
                handlerThread.setDaemon(true);
                handlerThread.start();

                // Thread handleNewThread = new Thread(new Runnable() {
                // @Override
                // public void run() {
                // handleNewConnection(clientHandler);
                // }
                // });
                // handleNewThread.setDaemon(true);
                // handleNewThread.start();

                checkGameStart();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    private void handleServerInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (!serverSocket.isClosed()) {
                String message = scanner.nextLine();
                broadcastObject("SERVER_MESSAGE:" + message, null);
            }
        }
    }

    public synchronized void handleNewConnection(ClientHandler newClient) {
        System.out.println("!!!!! New Client Connect !!!!!");

        ClientObj clientObj = newClient.getClientObj();

        if (clientObj != null) {
            clientObjs.add(clientObj);
            broadcastObject("NEW_PLAYER", null);
            broadcastObject(clientObj, null);

        } else {
            // Not found client obj

        }
    }

    private synchronized void checkGameStart() {
        if (!gameStarting && clients.size() >= requiredPlayersToStart) {
            gameStarting = true;
            broadcastObject("START_COUNTDOWN", null);
        }
    }

    public synchronized void handleReadyToStart(ClientHandler readyClient) {
        if (allClientsReady()) {
            broadcastObject("START_GAME", null);
            // Give clients time to process START_GAME message
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // closeServer();
                    System.out.println("TODO: Close Server");
                }
            }, 1000);
        }
    }

    private synchronized boolean allClientsReady() {
        return clients.stream().allMatch(ClientHandler::isReady);
    }

    public void broadcastObject(Object object, ClientHandler sender) {
        System.out.println();
        System.out.println("On Broadcast Object!");
        System.out.println("Object (Server): " + object);
        System.out.println("Client Handler (Sender): " + sender);
        System.out.println();

        for (ClientHandler receiver : clients) {
            if (receiver != sender && receiver.isReady() && object != null) {
                synchronized (receiver) {
                    try {
                        System.out.println("Sending object to receiver: " + receiver);
                        receiver.sendObject(object);
                    } catch (Exception e) {
                        System.out.println("Error broadcasting object to client: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        if (clientHandler.getClientObj() != null) {
            clientObjs.remove(clientHandler.getClientObj());
            clients.remove(clientHandler);

        }
        System.out.println("Client removed. Total clients: " + clients.size());

        // Notify remaining clients about the disconnection
        broadcastObject("PLAYER_DISCONNECTED", clientHandler);

        // Check if we need to abort game start
        if (gameStarting && clients.size() < requiredPlayersToStart) {
            gameStarting = false;
            broadcastObject("ABORT_START", null);
        }
    }

    // private void closeServer() {
    // try {
    // for (ClientHandler client : clients) {
    // client.close();
    // }
    // serverSocket.close();
    // } catch (IOException e) {
    // System.out.println("Error closing server: " + e.getMessage());
    // }
    // }

    // Getters
    public String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setRequiredPlayersToStart(int numOfPlayers) {
        this.requiredPlayersToStart = numOfPlayers;
    }
}