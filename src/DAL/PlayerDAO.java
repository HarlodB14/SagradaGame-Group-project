package DAL;

import Model.PlayStatus;
import Model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerDAO implements IDAO {

    public Player getCurrentPlayerByName(String username, int gameid) throws SQLException {
        String query = "SELECT * FROM player WHERE username = ? AND idgame = ?";
        Player player = null;
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, gameid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int playerId = resultSet.getInt("idplayer");
                    int idgame = resultSet.getInt("idgame");
                    String playerName = resultSet.getString("username");
                    PlayStatus playStatus = PlayStatus.valueOf(resultSet.getString("playstatus").toUpperCase());
                    String privateObjectiveCardColor = resultSet.getString("private_objectivecard_color");
                    int idpatternCard = resultSet.getInt("idpatterncard");
                    int seqNr = resultSet.getInt("seqnr");
                    int score = resultSet.getInt("score");
                    player = new Player(playerId, playerName, idgame, playStatus, privateObjectiveCardColor, idpatternCard, seqNr, score);
                }
            }
        }
        return player;
    }

    public int getNumberOfPlayers(int gameId) {
        String query = "SELECT COUNT(*) FROM player WHERE idgame = ?";
        int numberOfPlayers = 0;
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    numberOfPlayers = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfPlayers;
    }

    @Override
    public Player get(int id) throws SQLException {
        String query = "SELECT * FROM player WHERE idplayer = ?";
        Player player = new Player();
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    player.setIdPlayer(resultSet.getInt("idplayer"));
                    player.setUsername(resultSet.getString("username"));
                    player.setPlayStatus(resultSet.getString("playstatus"));
                    player.setSeqNr(resultSet.getInt("seqnr"));
                }
            }
        }
        return player;
    }

    @Override
    public List<Player> getAll() throws SQLException {
        String query = "SELECT * FROM player";
        List<Player> players = new ArrayList<>();

        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Player player = new Player();
                player.setIdPlayer(rs.getInt("idplayer"));
                player.setUsername(rs.getString("username"));
                player.setIdGame(rs.getInt("idgame"));
                player.setPlayStatus(rs.getString("playstatus"));
                player.setSeqNr((Integer) rs.getObject("seqnr"));
                player.setPrivateObjectiveCardColor(rs.getString("private_objectivecard_color"));
                player.setIdPatternCard((Integer) rs.getObject("idpatterncard"));
                player.setScore((Integer) rs.getObject("score"));
                players.add(player);
            }
            return players;
        }
    }

    @Override
    public int save(Object type) throws SQLException {
        return 0;
    }

    public List<Player> loadPlayers(int gameId) {
        List<Player> players = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT idplayer, username, idgame, playstatus, seqnr, private_objectivecard_color, idpatterncard, score FROM player WHERE idgame = ?")) {
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(new Player(
                        rs.getInt("idplayer"),
                        rs.getString("username"),
                        rs.getInt("idgame"),
                        rs.getString("playstatus"),
                        rs.getInt("seqnr"),
                        rs.getString("private_objectivecard_color"),
                        rs.getInt("idpatterncard"),
                        rs.getInt("score")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (!(type instanceof Player)) {
            return 0;
        }

        Player player = (Player) type;
        String insertQuery = "INSERT INTO player (username, idgame, playstatus, seqnr, private_objectivecard_color, score) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getUsername());
            ps.setInt(2, player.getIdGame());
            ps.setString(3, player.getPlayStatus());
            ps.setObject(4, player.getSeqNr());
            ps.setString(5, player.getPrivateObjectiveCardColor());
            ps.setObject(6, player.getScore());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKey = ps.getGeneratedKeys();
                if (generatedKey.next()) {
                    connection.commit();
                    return generatedKey.getInt(1);
                }
            }
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }

        return 0;
    }

    public int getPlayerScore(int playerId, int gameId) throws SQLException {
        String query = "SELECT score FROM player WHERE idplayer = ? AND idgame = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            statement.setInt(2, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("score");
                } else {
                    return -1; // or some other default value indicating the player was not found
                }
            }
        }
    }

    public Player getPlayerById(int playerId) throws SQLException {
        String query = "SELECT * FROM player WHERE idplayer = ?";
        Player player = null;
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int idgame = resultSet.getInt("idgame");
                    String playerName = resultSet.getString("username");
                    PlayStatus playStatus = PlayStatus.valueOf(resultSet.getString("playstatus").toUpperCase());
                    String privateObjectiveCardColor = resultSet.getString("private_objectivecard_color");
                    int idpatternCard = resultSet.getInt("idpatterncard");
                    int seqNr = resultSet.getInt("seqnr");
                    int score = resultSet.getInt("score");
                    player = new Player(playerId, playerName, idgame, playStatus, privateObjectiveCardColor, idpatternCard, seqNr, score);
                }
            }
        }
        return player;
    }

    @Override
    public int update(Object type) throws SQLException {
        if (!(type instanceof Player)) {
            return 0;
        }

        Player player = (Player) type;
        String query = "UPDATE player SET seqnr = ?, score = ? WHERE idplayer = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            ps.setInt(1, player.getSeqNr());
            ps.setInt(2, player.getScore());
            ps.setInt(3, player.getIdPlayer());

            int result = ps.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            // Log the exception or handle it appropriately
            throw e;
        }
    }

    public int updatePlayStatus(int id, PlayStatus playStatus) throws SQLException {
        String query = "UPDATE player SET playstatus = ? WHERE idplayer = ?";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playStatus.toString());
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }

    public int getIdGamePlayer(int idgame, String username) {
        String query = "SELECT idgame FROM player WHERE idgame = ? AND username = ?";
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, idgame);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idgame");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Integer getIdPatternCard(int playerId, int gameId) {
        String query = "SELECT idpatterncard FROM player WHERE idplayer = ? AND idgame = ?";
        try (PreparedStatement preparedStatement = DatabaseConnector.getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, gameId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idpatterncard");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Player> getPlayersByGameId(int id) {
        String query = "SELECT * FROM player WHERE idgame = ?";
        List<Player> players = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Player player = new Player();
                    player.setIdPlayer(resultSet.getInt("idplayer"));
                    player.setUsername(resultSet.getString("username"));
                    player.setIdGame(resultSet.getInt("idgame"));
                    player.setPlayStatus(resultSet.getString("playstatus"));
                    player.setSeqNr((Integer) resultSet.getObject("seqnr"));
                    player.setPrivateObjectiveCardColor(resultSet.getString("private_objectivecard_color"));
                    player.setIdPatternCard((Integer) resultSet.getObject("idpatterncard"));
                    player.setScore((Integer) resultSet.getObject("score"));
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }


    public List<Player> getAllCurrentPlayers(String username) {
        String query = "SELECT * FROM player WHERE username = ? ";
        List<Player> players = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Player player = new Player();
                    player.setIdPlayer(resultSet.getInt("idplayer"));
                    player.setUsername(resultSet.getString("username"));
                    player.setIdGame(resultSet.getInt("idgame"));
                    player.setPlayStatus(resultSet.getString("playstatus"));
                    player.setSeqNr((Integer) resultSet.getObject("seqnr"));
                    player.setPrivateObjectiveCardColor(resultSet.getString("private_objectivecard_color"));
                    player.setIdPatternCard((Integer) resultSet.getObject("idpatterncard"));
                    player.setScore((Integer) resultSet.getObject("score"));
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public boolean allPlayersSelectedPatternCard(int gameId) throws SQLException {
        String query = "SELECT COUNT(*) AS total, " +
                "(SELECT COUNT(*) FROM player WHERE idgame = ? AND idpatterncard IS NOT NULL) AS selected " +
                "FROM player WHERE idgame = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, gameId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total") == resultSet.getInt("selected");
            }
            return false;
        }
    }

    public HashMap<Integer, Integer> getSeqrByPlayers(int gameId) throws SQLException {
        HashMap<Integer, Integer> players = new HashMap<>();

        String query = "SELECT * FROM player WHERE idgame = ? ORDER BY seqnr ASC";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int seqnr = resultSet.getInt("seqnr");
                int idplayer = resultSet.getInt("idplayer");
                players.put(resultSet.getInt("seqnr"), resultSet.getInt("idplayer"));
            }
            return players;
        }
    }

    public void setAllPlayersToFinished(int id) {
        String query = "UPDATE player SET playstatus = ? WHERE idgame = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, PlayStatus.FINISHED.toString());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlaystatus(int playerID) {
        String query = "SELECT playstatus FROM player WHERE idplayer = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, playerID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("playstatus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean canInvitePlayer(String accountName, String challengerName) {
        String query = "SELECT * FROM player " +
                "WHERE username = ? " +
                "AND (" +
                "    (username = ? AND playstatus = 'CHALLENGER') " +
                "    OR (" +
                "        idgame IN (" +
                "            SELECT idgame FROM player WHERE username = ? AND playstatus = 'CHALLENGER'" +
                "        )" +
                "        AND username = ? AND playstatus = 'CHALLENGEE'" +
                "    )" +
                ")";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, accountName);
            preparedStatement.setString(2, challengerName);
            preparedStatement.setString(3, challengerName);
            preparedStatement.setString(4, accountName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
