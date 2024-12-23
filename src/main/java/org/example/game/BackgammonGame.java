package org.example.game;

import org.example.player.Player;
import org.example.serverclient.Server; // Add this import
import java.util.List;

public class BackgammonGame {
    private List<Player> players;
    private GameBoard gameBoard;
    private int currentPlayerIndex;

    public BackgammonGame(List<Player> players) {
        this.players = players;
        this.gameBoard = new GameBoard();
        this.currentPlayerIndex = 0; // First player starts
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void startGame() {
        // Start the game
        Server.notifyPlayers(gameBoard.toString());
    }

    public void handleInput(Player player, String input) {
        // Validate turn and input
        if (!player.equals(players.get(currentPlayerIndex))) {
            notifyPlayer(player, "It's not your turn!");
            return;
        }

        String[] commandParts = input.split(" ");
        if (commandParts.length != 3) {
            notifyPlayer(player, "Invalid input format! Please enter a valid command.");
            return;
        }

        String action = commandParts[0];
        int x, y;
        try {
            x = Integer.parseInt(commandParts[1]);
            y = Integer.parseInt(commandParts[2]);
        } catch (NumberFormatException e) {
            notifyPlayer(player, "Please enter valid coordinates for x and y.");
            return;
        }

        if (action.equals("move")) {
            if (gameBoard.isValidMove(player, x, y)) {
                gameBoard.placePiece(x, y, player.getSymbol());
                notifyPlayers(player.getName() + " made a move: (" + x + "," + y + ")");
            } else {
                notifyPlayer(player, "Invalid move! Please try again.");
                return;
            }
        } else {
            notifyPlayer(player, "Unknown action! Please enter 'move x y'.");
            return;
        }

        // Update board and notify players
        notifyPlayers("Updated board status:\n" + gameBoard.toString());

        // Next player's turn
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        notifyPlayers("It's " + players.get(currentPlayerIndex).getName() + "'s turn.");
    }

    private void notifyPlayers(String message) {
        Server.notifyPlayers(message);
    }

    private void notifyPlayer(Player player, String message) {
        player.sendMessage(message);
    }
}
