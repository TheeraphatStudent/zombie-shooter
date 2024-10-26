package client;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.concurrent.*;

import client.helper.Communication;
import components.character.CreateCharacter;
import components.character.ManageCharacterElement;
import models.ClientObj;
import models.Player;
import utils.UseGlobal;

public class Client implements Serializable, ManageCharacterElement {
    private static final long serialVersionUID = 1L;

    private Socket clientSocket;
    private ObjectOutputStream objOutStream;
    private ObjectInputStream objInStream;

    private String serverIp;
    private int serverPort;

    // ! Communication Contain
    private Communication communication;
    private String message = "";

    // Queue
    // private BlockingQueue<String> messageQueue;
    // private BlockingQueue<Object> objectQueue;

    private volatile boolean isConnected;

    // Character
    private ClientObj clientObj;

    public Client(String serverIp, int serverPort, ClientObj clientObj) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        // this.messageQueue = new LinkedBlockingQueue<>();
        // this.objectQueue = new LinkedBlockingQueue<>();

        this.communication = new Communication();

        this.isConnected = false;
        this.clientObj = clientObj;
    }

    public void start() {
        try {
            System.out.println("On Client Start!");

            clientSocket = new Socket(serverIp, serverPort);
            isConnected = true;

            objOutStream = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            objOutStream.flush(); // Ensure the stream is ready
            objInStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            System.out.println("Connected to " + this.serverIp + ":" + this.serverPort);

            CreateCharacter character = new CreateCharacter(false, clientObj);

            int frameWidth = UseGlobal.getWidth();
            int frameHeight = UseGlobal.getHeight();

            // Spawn Position
            int spawnPositionX = new Random().nextInt(frameWidth - CHARACTER_WIDTH);
            int spawnPositionY = new Random().nextInt(frameHeight - CHARACTER_HEIGHT);

            spawnPositionX = Math.max(0, Math.min(spawnPositionX, frameWidth - CHARACTER_WIDTH));
            spawnPositionY = Math.max(0, Math.min(spawnPositionY, frameHeight - CHARACTER_HEIGHT));
            System.out.printf("Spawn: x=%d | y=%d\n", spawnPositionX, spawnPositionY);

            System.out.printf("Spawn: x=%d | y=%d\n", spawnPositionX, spawnPositionY);

            character.setBounds(spawnPositionX, spawnPositionY, CHARACTER_WIDTH, CHARACTER_HEIGHT);
            this.clientObj.setPlayer(new Player(character, null));

            clientSideSendObject(this.clientObj);

            Thread objectThread = new Thread(this::receiveServerObject);

            objectThread.setPriority(Thread.MAX_PRIORITY);
            objectThread.setDaemon(true);

            objectThread.start();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            disconnect();
        }
    }

    public void clientSideSendObject(Object object) {
        System.out.println("Client Send Object > " + object.toString());
        System.out.println("Before Send Object > Client Name: " + ((ClientObj) object).getClientName());

        try {
            synchronized (objOutStream) {
                objOutStream.writeObject(object);
                objOutStream.flush();
                objOutStream.reset();
            }
        } catch (IOException e) {
            System.out.println("Error sending object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // public String receiveMessageQueue() {
    // System.out.println(messageQueue);
    // try {
    // return messageQueue.poll(5, TimeUnit.MINUTES);
    // } catch (InterruptedException e) {
    // return null;
    // }
    // }

    // public Object receiveObjectQueue() {
    // System.out.println(objectQueue);
    // try {
    // return objectQueue.poll(5, TimeUnit.MINUTES);
    // } catch (Exception e) {
    // return null;
    // }
    // }

    public String getMessage() {
        return this.message;

    }

    public void resetMessage() {
        this.message = "";

    }

    public Communication getCommunication() {
        return this.communication;

    }

    private void receiveServerObject() {
        System.out.println(">>>>> Receive Server Object Work! <<<<<");
        try {
            while (isConnected && !clientSocket.isClosed()) {
                Object object = (Object) objInStream.readObject();
                if (object != null) {
                    System.out.println("Received object from server: " + object);
                    System.out.println(object.getClass());
                    System.out.println(object.toString());

                    if (object instanceof Communication) {
                        this.communication = (Communication) object;

                    } else if (object instanceof String) {
                        this.message = (String) object;
                        // System.out.println("????? Message > " + this.message);
                    }

                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        System.out.println("Client Disconnect!");
        isConnected = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (objInStream != null) {
                objInStream.close();
            }
            if (objOutStream != null) {
                objOutStream.close();
            }
        } catch (IOException e) {
            System.out.println("Error during disconnection: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
