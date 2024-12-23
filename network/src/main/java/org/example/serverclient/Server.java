package org.example.serverclient;

import org.example.game.BackgammonGame;
import org.example.player.Player;
import org.example.game.GameBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private static final int PORT = 12345;
    private static List<PlayerHandler> players = new ArrayList<>();
    private static int currentPlayerIndex = 0;
    private static BackgammonGame game;
    private static GameBoard board;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started...");

            while (players.size() < 2) {
                Socket socket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(socket);
                players.add(playerHandler);
                new Thread(playerHandler).start();
            }

            if (players.size() == 2) {
                List<Player> gamePlayers = new ArrayList<>();
                for (PlayerHandler playerHandler : players) {
                    gamePlayers.add(playerHandler.getPlayer());
                }
                game = new BackgammonGame(gamePlayers);
                game.startGame();
                notifyPlayers("بازی شروع شد! نوبت بازیکن " + players.get(currentPlayerIndex).getPlayerName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyPlayers(String message) {
        for (PlayerHandler player : players) {
            player.sendMessage(message);
        }
    }

    public static class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
        private String playerName;
        private Player player;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Player getPlayer() {
            return player;
        }

        public String getPlayerName() {
            return playerName;
        }

        @Override
        public void run() {
            try {
                playerName = in.nextLine();
                player.setName(playerName);
                Server.notifyPlayers(playerName + " متصل شد!");
                Server.board.printBoard();

                while (true) {
                    String input = in.nextLine();
                    System.out.println("Received: " + input);

                    if (isCurrentPlayer()) {
                        game.handleInput(player, input);
                    } else {
                        out.println("در حال حاضر نوبت شما نیست!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void printBoard() {
            String boardState = board.toString(); // فرض کنید متد toString() وضعیت تخته را برمی‌گرداند
            System.out.println("Current Board:");
            System.out.println(boardState);
            notifyPlayers("Current Board:\n" + boardState); // ارسال وضعیت تخته به بازیکنان
        }

        private boolean isCurrentPlayer() {
            return players.indexOf(this) == currentPlayerIndex;
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
