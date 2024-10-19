package client;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader userInput;

    // เป็น Ip และ Port ที่ต้องการ Connect ไปยัง Server
    private String serverIp;
    private int serverPort;

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        try {
            socket = new Socket(serverIp, serverPort);

            // ใช้ส่งข้อมูลจาก client ไปยัง Server Socket
            System.out.println("Socket Output");
            out = new PrintWriter(socket.getOutputStream(), true);

            // ใช้รับข้อมูลจาก Socket
            System.out.println("Socket Input");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server at " + this.serverIp + ":" + this.serverPort);
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("Client Start");

        try {
            new Thread(this::receiveMessages).start();

            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
    
    public String receiveMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
            return null;
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Client > Received: " + message);

            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
        }
    }
}