package Controller;

import DAL.ChatDAO;
import DAL.PlayerDAO;
import Model.Chat;
import Model.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {
    private ChatDAO chatDAO;
    private PlayerDAO playerDAO;
    private int gameId;

//    private static final int GAME_ID = 2; // Hardcoded gameId

    public ChatController(int gameId) {
        this.gameId = gameId;
        this.chatDAO = new ChatDAO();
        this.playerDAO = new PlayerDAO();
    }

    public Player getCurrentPlayer() {
        try {
            String username = ApplicationController.currentLoggedInAccount.getUsername();
            return playerDAO.getCurrentPlayerByName(username, gameId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean sendMessage(String message) {
        try {
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer != null && chatDAO.canSendMessage(currentPlayer.getIdPlayer())) {
                Chat chat = new Chat(currentPlayer.getIdPlayer(), new Timestamp(System.currentTimeMillis()), message);
                return chatDAO.insert(chat) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Chat> getMessages() {
        try {
            return chatDAO.listMessagesByGameId(gameId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Integer, String> getPlayerMap() {
        try {
            List<Player> players = playerDAO.getPlayersByGameId(gameId);
            Map<Integer, String> playerMap = new HashMap<>();
            for (Player player : players) {
                playerMap.put(player.getIdPlayer(), player.getUsername());
            }
            return playerMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
