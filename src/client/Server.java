package client;

import java.io.*;
import java.net.*;
import java.util.*;

import client.helper.ClientHandler;
import client.helper.ServerHelper;
import components.character.CreateCharacter;
import models.ClientObj;

public class Server extends ServerHelper implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private final List<ClientObj> clientObjs = Collections.synchronizedList(new ArrayList<>());
    private final int serverPort;
    private final String serverIp;
    private volatile int requiredPlayersToStart = 1;
    private volatile boolean gameStarting = false;

    public Server() {
        super();
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

                synchronized (clients) {
                    clients.add(clientHandler);
                    System.out.println("Client added. Total clients: " + clients.size());
                }

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
                broadcastMessage("SERVER_MESSAGE:" + message, null);
            }
        }
    }

    public synchronized void handleNewConnection(ClientHandler newClient) {
        System.out.println("!!!!! New Client Connect !!!!!");

        ClientObj clientObj = newClient.getClientObj();

        if (clientObj != null) {
            clientObjs.add(clientObj);
            broadcastMessage("NEW_PLAYER", null);

            // Send existing players to the new client
            for (ClientObj existingClient : clientObjs) {
                if (existingClient != clientObj) {
                    newClient.sendObject(existingClient);
                }
            }
        } else {
            // Not found client obj

        }
    }

    private synchronized void checkGameStart() {
        if (!gameStarting && clients.size() >= requiredPlayersToStart) {
            gameStarting = true;
            broadcastMessage("START_COUNTDOWN", null);
        }
    }

    public synchronized void handleReadyToStart(ClientHandler readyClient) {
        if (allClientsReady()) {
            broadcastMessage("START_GAME", null);
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

    public void broadcastMessage(String message, ClientHandler sender) {
        System.out.println();
        System.out.println("On Broadcast Message!");
        System.out.println("Message (Server): " + message);
        System.out.println("Client Handler (Sender): " + sender);

        synchronized (clients) {
            System.out.println("===== All Client =====");
            for (ClientHandler receiver : clients) {
                System.out.println(receiver);

                if (receiver != sender && receiver.isReady()) {
                    try {
                        receiver.sendMessage(message);

                    } catch (Exception e) {
                        System.out.println("Error broadcasting to client: " + e.getMessage());
                        removeClient(receiver);
                    }
                }
            }
            System.out.println("===============");
        }

        System.out.println();
    }

    public void broadcastObject(Object object, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler receiver : clients) {
                if (receiver != sender && receiver.isReady()) {
                    try {
                        receiver.sendObject(object);
                    } catch (Exception e) {
                        System.out.println("Error broadcasting object to client: " + e.getMessage());
                        removeClient(receiver);
                    }
                }
            }
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        if (clientHandler.getClientObj() != null) {
            clientObjs.remove(clientHandler.getClientObj());
        }
        System.out.println("Client removed. Total clients: " + clients.size());

        // Notify remaining clients about the disconnection
        broadcastMessage("PLAYER_DISCONNECTED", clientHandler);

        // Check if we need to abort game start
        if (gameStarting && clients.size() < requiredPlayersToStart) {
            gameStarting = false;
            broadcastMessage("ABORT_START", null);
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