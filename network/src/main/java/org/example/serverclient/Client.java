package org.example.serverclient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static PrintWriter out;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your player ID: ");
            String playerId = scanner.nextLine();
            out.println(playerId); // Send player ID to the server

            // Thread to receive messages from server
            new Thread(new IncomingMessagesHandler(in)).start();

            // Main loop to send messages (chat or game commands)
            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class IncomingMessagesHandler implements Runnable {
        private BufferedReader in;

        public IncomingMessagesHandler(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String incomingMessage;
            try {
                while ((incomingMessage = in.readLine()) != null) {
                    System.out.println("Received: " + incomingMessage);
                    // اینجا می‌توانید وضعیت تخته را به صورت خاص پردازش کنید
                    if (incomingMessage.startsWith("Current Board:")) {
                        System.out.println(incomingMessage); // نمایش وضعیت تخته
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection to server lost.");
                e.printStackTrace();
            }
        }
    }
}
