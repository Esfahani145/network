package org.example.serverclient;

import org.example.game.BackgammonGame;
import org.example.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static List<PlayerHandler> players = new ArrayList<>();
    private static int currentPlayerIndex = 0;
    private static BackgammonGame game;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started...");

            while (true) {
                Socket socket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(socket);
                players.add(playerHandler);
                ClientHandler clientHandler = new ClientHandler(socket, playerHandler);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyPlayers(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private PlayerHandler playerHandler;
        private boolean inGame = false;

        public ClientHandler(Socket socket, PlayerHandler playerHandler) {
            this.socket = socket;
            this.playerHandler = playerHandler;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your username:");
                username = in.readLine();
                playerHandler.setPlayerName(username);
                broadcastOnlineUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from " + username + ": " + message);
                    if (!inGame && message.startsWith("start ")) {
                        String opponent = message.substring(6);
                        startGameWithOpponent(opponent);
                    } else if (inGame) {
                        if (isCurrentPlayer()) {
                            game.handleInput(playerHandler.getPlayer(), message);
                            notifyPlayers(username + ": " + message);
                        } else {
                            out.println("It's not your turn!");
                        }
                    } else {
                        notifyPlayers(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clientHandlers.remove(this);
                    broadcastOnlineUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isCurrentPlayer() {
            return players.indexOf(playerHandler) == currentPlayerIndex;
        }

        private void startGameWithOpponent(String opponent) {
            for (ClientHandler handler : clientHandlers) {
                if (handler.getUsername().equals(opponent) && !handler.inGame) {
                    handler.inGame = true;
                    this.inGame = true;

                    List<Player> gamePlayers = new ArrayList<>();
                    char[] symbols = {'*', 'O'};
                    gamePlayers.add(new Player(this.username, this.socket, symbols[0]));
                    gamePlayers.add(new Player(handler.getUsername(), handler.socket, symbols[1]));

                    game = new BackgammonGame(gamePlayers);
                    game.startGame();
                    notifyPlayers("Initial Board:\n" + game.getGameBoard().toString());
                    notifyPlayers("Game started! It's " + gamePlayers.get(currentPlayerIndex).getName() + "'s turn.");
                    return;
                }
            }
            out.println("Opponent not found or already in a game.");
        }

        private void broadcastOnlineUsers() {
            StringBuilder users = new StringBuilder("Online Users: ");
            for (ClientHandler handler : clientHandlers) {
                users.append(handler.getUsername()).append(" ");
            }
            for (ClientHandler handler : clientHandlers) {
                handler.sendMessage(users.toString());
            }
        }
    }

    private static class PlayerHandler {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;
        private Player player;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public Socket getSocket() {
            return socket;
        }
    }
}
