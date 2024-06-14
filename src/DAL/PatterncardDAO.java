package DAL;

import Model.Patterncard;
import Model.PlayStatus;
import Model.Player;
import Model.Square;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatterncardDAO implements IDAO {

    public static void loadPatterncardProperties(Patterncard patterncard) throws SQLException {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT name, difficulty FROM patterncard WHERE idpatterncard = ?")) {
            preparedStatement.setInt(1, patterncard.getPatternCardID());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    patterncard.setName(resultSet.getString("name"));
                    patterncard.setDifficulty(resultSet.getInt("difficulty"));
                }
            }
        }
    }

    @Override
    public Patterncard get(int id) throws SQLException {
        String query = "SELECT * FROM patterncard WHERE idpatterncard = ?";
        Patterncard patterncard = null;
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int difficulty = resultSet.getInt("difficulty");
                    int standardPatternCard = resultSet.getInt("standard");
                    patterncard = new Patterncard(id, name, difficulty, standardPatternCard);
                }
            }
        }
        return patterncard;
    }

    @Override
    public List<Patterncard> getAll() throws SQLException {
        List<Patterncard> patterncards = new ArrayList<>();
        String query = "SELECT * FROM patterncard";
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idpatterncard");
                    String name = resultSet.getString("name");
                    int difficulty = resultSet.getInt("difficulty");
                    int standardPatternCard = resultSet.getInt("standard");
                    Patterncard patterncard = new Patterncard(id, name, difficulty, standardPatternCard);
                    patterncards.add(patterncard);
                }
            }
        }
        return patterncards;
    }

    @Override
    public int save(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int update(Object type) throws SQLException {
        if (!(type instanceof Player)) {
            return 0;
        }

        Player player = (Player) type;
        String query = "UPDATE player SET idpatterncard = ? WHERE idplayer = ?";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, player.getIdPatternCard());
            statement.setInt(2, player.getIdPlayer());
            int result = statement.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    public void updateidPatterncard(int idpatterncard, int idplayer) throws SQLException {
        String query = "UPDATE player SET idpatterncard = ? WHERE idplayer = ?";
        try(Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idpatterncard);
            statement.setInt(2, idplayer);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }


    public boolean playerHasOption(int playerId) throws SQLException {
        String query = "SELECT * FROM patterncardoption WHERE idplayer = ?";
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

   public void insertShownPatterncard(int patternCardID, int playerId) throws SQLException {
    String query = "INSERT INTO patterncardoption (idpatterncard,idplayer) VALUES (?, ?)";
    try(Connection connection = DatabaseConnector.getConnection()) {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, patternCardID);
        statement.setInt(2, playerId);
        statement.executeUpdate();
        connection.commit();
    } catch (SQLException e) {
        DatabaseConnector.rollback();
        e.printStackTrace();
    }
}

    public void insertPatternCardOptions(List<Patterncard> patternCardsToAssign, int playerId) throws SQLException {
        int counter = 0;
        for (Patterncard patternCard : patternCardsToAssign) {
            if (counter >= 4) {
                break;
            }
            insertShownPatterncard(patternCard.getPatternCardID(), playerId);
            counter++;
        }
    }

    public List<Patterncard> getPatternCardOptionsForPlayer(int playerId) {
        List<Patterncard> patternCardOptions = new ArrayList<>();
        String query = "SELECT idpatterncard FROM patterncardoption WHERE idplayer = ?";
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(query)) {
            statement.setInt(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idPatternCard = resultSet.getInt("idpatterncard");
                    patternCardOptions.add(new Patterncard(idPatternCard));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patternCardOptions;
    }

//    public void savePatternCard(Patterncard patternCard) throws SQLException {
//        try (Connection connection = DatabaseConnector.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(
//                     "UPDATE Square SET dieColor = ?, dieEye = ? WHERE patternCardID = ? AND xPos = ? AND yPos = ?")) {
//            for (Square square : patternCard.getSquares()) {
//                // Assuming each square knows about its die
//                if (square.getDie() != null) {
//                    stmt.setString(1, square.getDie().getColour().toString());
//                    stmt.setInt(2, square.getDie().getEye());
//                    stmt.setInt(3, patternCard.getPatternCardID());
//                    stmt.setInt(4, square.getXPos());
//                    stmt.setInt(5, square.getYPos());
//                    stmt.executeUpdate();
//                }
//            }
//        }
//    }

    public Patterncard loadPatternCardsForPlayers(int gameId, int playerid) {
        String query = "SELECT p.idpatterncard, p.username, p.private_objectivecard_color, pc.name, pc.difficulty " +
                "FROM player p JOIN patterncard pc ON p.idpatterncard = pc.idpatterncard " +
                "WHERE p.idgame = ? AND p.idplayer = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, playerid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int idPatterncard = resultSet.getInt("idpatterncard");
                    String username = resultSet.getString("username");
                    String name = resultSet.getString("name");
                    int difficulty = resultSet.getInt("difficulty");

                    // Create Patterncard with basic data and no DB operations in the constructor
                    return new Patterncard(idPatterncard, playerid, username, name, difficulty, null);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


}
