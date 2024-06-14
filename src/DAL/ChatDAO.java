package DAL;

import Model.Chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO implements IDAO<Chat> {

    @Override
    public Chat get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Chat> getAll() throws SQLException {
        return null;
    }

    @Override
    public int save(Chat chat) throws SQLException {
        return 0;
    }

    @Override
    public int insert(Chat chat) throws SQLException {
        String sql = "INSERT INTO chatline (idplayer, time, message) VALUES (?, CURRENT_TIMESTAMP, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, chat.getIdPlayer());
            statement.setString(2, chat.getMessage());
            return statement.executeUpdate();
        }
    }

    @Override
    public int update(Chat chat) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Chat chat) throws SQLException {
        return 0;
    }

    public List<Chat> listMessagesByGameId(int gameId) throws SQLException {
        List<Chat> listMessage = new ArrayList<>();
        String sql = "SELECT c.idplayer, c.time, c.message " +
                "FROM chatline c " +
                "JOIN player p ON c.idplayer = p.idplayer " +
                "WHERE p.idgame = ? " +
                "ORDER BY c.time DESC";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idPlayer = resultSet.getInt("idplayer");
                    Timestamp time = resultSet.getTimestamp("time");
                    String message = resultSet.getString("message");
                    Chat chatMessage = new Chat(idPlayer, time, message);
                    listMessage.add(chatMessage);
                }
            }
        }
        return listMessage;
    }

    public boolean canSendMessage(int playerId) throws SQLException {
        String sql = "SELECT * FROM chatline WHERE idplayer = ? AND DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s') = DATE_FORMAT(CURRENT_TIMESTAMP, '%Y-%m-%d %H:%i:%s')";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next();
            }
        }
    }
}
