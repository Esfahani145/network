package org.example.player;

import java.io.PrintWriter;
import java.net.Socket;

public class Player {
    private String name;
    private Socket socket;
    private PrintWriter out;
    private char symbol; // Symbol of the player

    // Constructor with proper parameter types
    public Player(String name, Socket socket, char symbol) {
        this.name = name;
        this.socket = socket;
        this.symbol = symbol; // Assign the player symbol
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSymbol() {
        return symbol; // Return the player's symbol
    }

    public void sendMessage(String message) {
        out.println(message); // Send message over the socket output stream
    }
}