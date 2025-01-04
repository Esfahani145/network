package com.bytezone.network;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PeerToPeerClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public PeerToPeerClient() {
        // سازنده خالی یا تنظیمات اولیه
    }

    // اتصال به حریف
    public void findOpponent() {
        try {
            socket = new Socket("opponent-ip", 12345); // جایگزین با IP حریف
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to opponent.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ارسال پیام به حریف
    public void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // دریافت پیام از حریف
    public String receiveMessage() {
        try {
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // بستن ارتباط
    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
