package client;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverIp;
    private int serverPort;
    private BlockingQueue<String> messageQueue;
    private volatile boolean isConnected;

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.isConnected = false;
    }

    public void start() {
        try {
            socket = new Socket(serverIp, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            System.out.println("Connected to " + this.serverIp + ":" + this.serverPort);
            new Thread(this::receiveServerMessage).start();
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

    public String receiveMessageQueue() {
        try {
            return messageQueue.poll(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private void receiveServerMessage() {
        try {
            String message;
            while (isConnected && (message = in.readLine()) != null) {
                messageQueue.offer(message);
            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void disconnect() {
        isConnected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
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
