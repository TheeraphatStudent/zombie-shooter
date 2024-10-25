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
    private BufferedOutputStream buffOut;
    private ObjectOutputStream objOutSteam;

    private BufferedInputStream buffIn;
    private ObjectInputStream objInSteam;

    private BlockingQueue<String> messageQueue;
    private BlockingQueue<Object> objectQueue;

    private volatile boolean isConnected;

    // Character
    private ClientObj clientObj;
    // private CreateCharacter character;
    // private Player player;

    public Client(String serverIp, int serverPort, ClientObj clientObj) {
        super();
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.objectQueue = new LinkedBlockingQueue<>();

        this.isConnected = false;
        this.clientObj = clientObj;

        // this.character = new CreateCharacter(false, this.clientObj);
    }

    public void start() {
        try {
            System.out.println("On Client Start!");

            clientSocket = new Socket(serverIp, serverPort);
            isConnected = true;

            buffOut = new BufferedOutputStream(clientSocket.getOutputStream());
            objOutSteam = new ObjectOutputStream(buffOut);
            objOutSteam.flush();

            buffIn = new BufferedInputStream(clientSocket.getInputStream());
            objInSteam = new ObjectInputStream(buffIn);
            
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Connected to " + this.serverIp + ":" + this.serverPort);

            clientSideSendObject(this.clientObj);
            // clientSideSendObject(this.character);

            // Get content from server
            Thread messageThread = new Thread(this::receiveServerMessage);
            Thread objectThread = new Thread(this::receiveServerObject);

            messageThread.setDaemon(true);

            objectThread.setPriority(Thread.MAX_PRIORITY);
            objectThread.setDaemon(true);

            messageThread.start();
            objectThread.start();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            disconnect();

        }
    }

    public void clientSideSendMessage(String message) {
        if (isConnected && out != null) {
            out.println(message);
            out.flush();

        } else {
            System.out.println("Cannot send message. Not connected to server.");
        }
    }

    public void clientSideSendObject(Object object) {
        System.out.println("Client Send Object > " + object.toString());

        synchronized (objOutSteam) {
            try {
                objOutSteam.writeObject(object);
                objOutSteam.flush();
                objOutSteam.reset();

            } catch (IOException e) {
                System.out.println("Error sending object: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // public String receiveMessageQueue() {
    // try {
    // String message = in.readLine();
    // if (message != null) {
    // System.out.println("Received from server: " + message);
    // }
    // return message;
    // } catch (IOException e) {
    // System.out.println("Error receiving message: " + e.getMessage());
    // return null;
    // }
    // }

    public String receiveMessageQueue() {
        System.out.println(messageQueue);

        try {
            return messageQueue.poll(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
            return null;
        }
    }

    public Object receiveObjectQueue() {
        System.out.println(objectQueue);

        try {
            return objectQueue.poll(5, TimeUnit.SECONDS);

        } catch (Exception e) {
            return null;

        }

    }

    // รับ Object ?ี่ส่งมาจาก Server
    private void receiveServerObject() {
        System.out.println(">>>>> Receive Server Object Work! <<<<<");
        
        try {
            synchronized (objInSteam) {
                Object object = null;

                while (isConnected && !clientSocket.isClosed()) {
                    System.out.println(objInSteam);

                    object = objInSteam.readObject();

                    if (object != null) {
                        System.out.println("Get Object >>> " + object);
                        objectQueue.offer(object);

                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving object: " + e.getMessage());
            e.printStackTrace();

        }
    }

    private void receiveServerMessage() {
        System.out.println(">>>>> Receive Server Message Work! <<<<<");

        try {
            synchronized (in) {
                String message = null;
                while (isConnected && !clientSocket.isClosed()) {
                    message = in.readLine();
                    if (message != null) {
                        messageQueue.offer(message);

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
            e.printStackTrace();

        }
    }

    // Connection
    public void disconnect() {
        System.out.println("Client Disconnect!");

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
