package client.helper;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

import client.Server;
import components.character.CreateCharacter;
import models.ClientObj;

public class ClientHandler implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    private Socket clientSocket;
    private Server server;

    private boolean isReady = true;

    // ใช้สำหรับส่งและรับข้อมูลระหว่าง Server และ Client
    private InputStream clientInSteam;
    private OutputStream clientOutSteam;

    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    // Game Content
    private Object receivedObject = null;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;

    }

    public void run() {
        try {
            System.out.println("!-!-!-!-! On Handler Run !-!-!-!-!");

            clientOutSteam = clientSocket.getOutputStream();
            objectOut = new ObjectOutputStream(new BufferedOutputStream(clientOutSteam));
            objectOut.flush();
            objectOut.reset();

            clientInSteam = clientSocket.getInputStream();
            objectIn = new ObjectInputStream(new BufferedInputStream(clientInSteam));

            new Thread(this::receiveObjectsFromClient).start();
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());

        }
        // finally {
        // try {
        // clientSocket.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // server.removeClient(this);
        // }
    }

    private void receiveObjectsFromClient() {
        System.out.println("Prepare Receive Object From Client!");
        // System.out.println(objectIn);

        try {
            while (!clientSocket.isClosed()) {
                this.receivedObject = objectIn.readObject();
                System.out.println("Client Handler > Received object: " + this.receivedObject.toString());

                if (this.receivedObject instanceof ClientObj) {
                    server.handleNewConnection(ClientHandler.this);

                }

            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving object: " + e.getMessage());
        }
    }

    public void sendObject(Object object) {
        try {
            synchronized (objectOut) {
                System.out.println();
                System.out.println("Client Handler > Send Object: " + object);

                objectOut.writeObject(object);
                objectOut.flush();
                objectOut.reset();
            }
        } catch (IOException e) {
            System.out.println("Error sending object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;

    }

    public boolean isReady() {
        return this.isReady;

    }

    public Object getClientReceiveObject() {
        return receivedObject;

    }
}