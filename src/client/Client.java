package client;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.List;
import java.util.concurrent.*;

import client.helper.Communication;
import models.ClientObj;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private Socket clientSocket;
    private ObjectOutputStream objOutStream;
    private ObjectInputStream objInStream;

    private String serverIp;
    private int serverPort;

    // ! Communication Contain
    private Communication communication;

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
            objOutStream.flush();  // Ensure the stream is ready
            objInStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            System.out.println("Connected to " + this.serverIp + ":" + this.serverPort);

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
    //     System.out.println(messageQueue);
    //     try {
    //         return messageQueue.poll(5, TimeUnit.MINUTES);
    //     } catch (InterruptedException e) {
    //         return null;
    //     }
    // }

    // public Object receiveObjectQueue() {
    //     System.out.println(objectQueue);
    //     try {
    //         return objectQueue.poll(5, TimeUnit.MINUTES);
    //     } catch (Exception e) {
    //         return null;
    //     }
    // }

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

                    // if (object instanceof String) {
                    //     messageQueue.offer((String) object);

                    // } else {
                    //     objectQueue.offer(object);

                    // }

                    if (object instanceof Communication) {
                        // System.out.println("\n/*/*/*/*/ Receive Communication! /*/*/*/*/\n");

                        this.communication = (Communication) object;
                        // Map<String, List<ClientObj>> content = communication.getContent();
                        // // System.out.println(content.entrySet());
                        
                        // for (Object obj : content.entrySet()) {
                        //     System.out.println(obj);

                        // }

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
