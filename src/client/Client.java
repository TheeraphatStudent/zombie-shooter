package client;

import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.*;

import components.character.CreateCharacter;
import models.ClientObj;
import models.Player;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private Socket clientSocket;
    private Socket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    private String serverIp;
    private int serverPort;

    // รับ - ส่ง ข้อมูล
    private ObjectOutputStream objOutSteam;
    private ObjectInputStream objectSteamIn;

    private BlockingQueue<String> messageQueue;
    private volatile boolean isConnected;

    // Character
    private ClientObj clientObj;
    private CreateCharacter character;
    private Player player;

    public Client(String serverIp, int serverPort, ClientObj clientObj) {
        super();
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.isConnected = false;
        this.clientObj = clientObj;

        this.character = new CreateCharacter(false, this.clientObj);
    }

    public void start() {
        try {
            clientSocket = new Socket(serverIp, serverPort);
            isConnected = true;

            // Initialize output streams first
            objOutSteam = new ObjectOutputStream(clientSocket.getOutputStream());
            objOutSteam.flush();
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Then initialize input streams
            objectSteamIn = new ObjectInputStream(clientSocket.getInputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Connected to " + this.serverIp + ":" + this.serverPort);

            sendObject(this.character);
            sendObject(this.clientObj);

            // Get content from server
            new Thread(this::receiveServerMessage).start();
            new Thread(this::receiveServerObject).start();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (isConnected && out != null) {
            out.println(message);
        } else {
            System.out.println("Cannot send message. Not connected to server.");
        }
    }

    public void sendObject(Object object) {
        System.out.println("Client Send Object > " + object.toString());

        try {
            objOutSteam.writeObject(object);
            objOutSteam.flush();

        } catch (IOException e) {
            System.out.println("Error sending object: " + e.getMessage());
            e.printStackTrace();

        }
    }

    public String receiveMessageQueue() {
        try {
            return messageQueue.poll(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
            return null;
        }
    }

    // รับ Object ?ี่ส่งมาจาก Server
    private void receiveServerObject() {
        try {
            Object object;
            while (isConnected && !clientSocket.isClosed() && (object = objectSteamIn.readObject()) != null) {
                object = objectSteamIn.readObject();
                System.out.println("Received object: " + object.getClass().getName() + " with hashcode: " + object.hashCode());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving object: " + e.getMessage());
            e.printStackTrace();

        } finally {
            disconnect();

        }
    }

    private void receiveServerMessage() {
        try {
            String message;
            while (isConnected && !clientSocket.isClosed() && (message = in.readLine()) != null) {
                messageQueue.offer(message);
            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnect(); 
        }
    }
    

    // Connection
    public void disconnect() {
        isConnected = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Error during disconnection: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
