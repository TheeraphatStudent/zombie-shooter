package client.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import client.Server;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;

    private boolean isReady;

    // ใช้สำหรับส่งและรับข้อมูลระหว่าง Server และ Client

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server > Received message: " + message);
                server.broadcastMessage(message, this);

            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } 
        // finally {
        //     try {
        //         clientSocket.close();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        //     server.removeClient(this);
        // }
    }

    public void sendMessage(String message) {
        out.println(message);

    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;

    }

    public boolean isReady() {
        return this.isReady;

    }
}