package org.example.game;

import org.example.player.Player;
import org.example.serverclient.Server;

import java.util.List;

public class BackgammonGame {
    private List<Player> players;
    private GameBoard gameBoard;
    private int currentPlayerIndex;

    public BackgammonGame(List<Player> players) {
        this.players = players;
        this.gameBoard = new GameBoard();
        this.currentPlayerIndex = 0; // اولین بازیکن شروع می‌کند
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void startGame() {
        // شروع بازی
        gameBoard.printBoard(); // چاپ تخته بازی
    }

    public void handleInput(Player player, String input) {
        // بررسی نوبت و اطمینان از اینکه ورودی معتبر است
        if (!player.equals(players.get(currentPlayerIndex))) {
            notifyPlayer(player, "در حال حاضر نوبت شما نیست!");
            return;
        }

        // پردازش ورودی (برای مثال، حرکت مهره)
        String[] commandParts = input.split(" ");
        if (commandParts.length != 3) {
            notifyPlayer(player, "فرمت ورودی نامعتبر است! لطفا ورودی صحیحی وارد کنید.");
            return;
        }

        // فرض کنید ورودی به فرمت "move x y" است
        String action = commandParts[0];
        int x;
        int y;

        try {
            x = Integer.parseInt(commandParts[1]);
            y = Integer.parseInt(commandParts[2]);
        } catch (NumberFormatException e) {
            notifyPlayer(player, "لطفا مختصات x و y را به درستی وارد کنید.");
            return;
        }

        // انجام حرکت (باید بعد از اعتبارسنجی که آیا حرکت مجاز است انجام شود)
        if (action.equals("move")) {
            // سنجش اینکه آیا حرکت مجاز است
            if (gameBoard.isValidMove(player, x, y)) { // فرض کنید متدی برای تعیین اعتبار حرکت وجود دارد
                gameBoard.placePiece(x, y, player.getSymbol()); // فرض کنیم هر بازیکن سمبل خاصی دارد
                notifyPlayers(player.getName() + " یک حرکت انجام داد: (" + x + "," + y + ")");
            } else {
                notifyPlayer(player, "حرکت نامعتبر است! لطفا دوباره تلاش کنید.");
                return;
            }
        } else {
            notifyPlayer(player, "عملیات ناشناخته! لطفا 'move x y' را وارد کنید.");
            return;
        }

        // به‌روزرسانی وضعیت تخته و ارسال به بازیکنان
        notifyPlayers("وضعیت جدید تخته:\n" + gameBoard.toString()); // ارسال وضعیت جدید تخته به بازیکنان

        // پیشرفت نوبت به بازیکن بعدی
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        notifyPlayers("نوبت " + players.get(currentPlayerIndex).getName() + " است.");
    }

    private void notifyPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    private void notifyPlayer(Player player, String message) {
        player.sendMessage(message);
    }
}