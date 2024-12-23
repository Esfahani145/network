package org.example.game;

import org.example.player.Player;

public class GameBoard {
    private char[][] board;

    // Constructor to initialize the board
    public GameBoard() {
        board = new char[6][6]; // ایجاد یک تخته 6x6 برای نمایش

        // خالی کردن تخته نرد
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' '; // تخته خالی
            }
        }

        // مثال: قرار دادن مهره‌ها در موقعیت‌های ابتدایی
        board[0][1] = '*'; // مهره اول
        board[0][3] = '*'; // مهره دوم
        board[5][0] = 'O'; // مهره حریف
        board[5][1] = 'O'; // مهره دیگر حریف
    }

    // متد برای چاپ تخته
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

    // متد برای قرار دادن مهره (ساده)
    public void placePiece(int x, int y, char symbol) {
        board[x][y] = symbol;
    }

    public boolean isValidMove(Player player, int x, int y) {
        // بررسی کنید که آیا مختصات x و y در محدوده تخته هستند
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            return false; // خارج از محدوده
        }

        // بررسی کنید که آیا مکان خالی است یا مختص بازیکن
        if (board[x][y] == 0 || board[x][y] == player.getSymbol()) {
            return true;
        }

        return false; // اگر مهره متعلق به بازیکن نیست
    }

    @Override
    public String toString() {
        StringBuilder boardRepresentation = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                boardRepresentation.append(cell == 0 ? '.' : cell).append(' ');
            }
            boardRepresentation.append('\n');
        }
        return boardRepresentation.toString();
    }
}