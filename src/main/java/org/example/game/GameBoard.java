package org.example.game;

import org.example.player.Player;

public class GameBoard {
    private char[][] board;

    public GameBoard() {
        board = new char[6][6]; // Create a 6x6 board

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' '; // Initialize with empty spaces
            }
        }

        // Example: initial positions of pieces
        board[0][1] = '*'; // First piece
        board[0][3] = '*'; // Second piece
        board[5][0] = 'O'; // Opponent's piece
        board[5][1] = 'O'; // Another opponent's piece
    }

    public void printBoard() {
        System.out.println("-------------------------");
        for (int i = 0; i < 6; i++) {
            System.out.print("| ");
            for (int j = 0; j < 6; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------------------");
        }
    }

    public void placePiece(int x, int y, char symbol) {
        board[x][y] = symbol;
    }

    public boolean isValidMove(Player player, int x, int y) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            return false; // Out of bounds
        }

        return board[x][y] == ' ' || board[x][y] == player.getSymbol(); // Valid if empty or player's symbol
    }

    @Override
    public String toString() {
        StringBuilder boardRepresentation = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                boardRepresentation.append(cell == ' ' ? '.' : cell).append(' ');
            }
            boardRepresentation.append('\n');
        }
        return boardRepresentation.toString();
    }
}
