package DAL;

import Model.Die;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoundTrackDAO {

    public List<Die> getDiceForRound(int gameId, int round) throws SQLException {
        String query = "SELECT * FROM gamedie WHERE idgame = ? AND roundtrack = ?";
        List<Die> dice = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            statement.setInt(2, round);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Die die = new Die(resultSet.getInt("idgame"),
                            resultSet.getInt("roundID"),
                            resultSet.getString("diecolor"),
                            resultSet.getInt("eyes"),
                            resultSet.getInt("dienumber"));
                    die.setRoundTrack(resultSet.getInt("roundtrack"));
                    dice.add(die);
                }
            }
        }
        return dice;
    }

    public ArrayList<Die> getLeftoverDice(int gameId, int currentRound) throws SQLException {
        String selectQuery = "SELECT * FROM gamedie WHERE idgame = ? AND roundID = ? AND roundtrack IS NULL";
        String checkQuery = "SELECT COUNT(*) FROM playerframefield WHERE idgame = ? AND dienumber = ? AND diecolor = ?";
        ArrayList<Die> leftoverDice = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            selectStmt.setInt(1, gameId);
            selectStmt.setInt(2, currentRound);

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                while (resultSet.next()) {
                    int dienumber = resultSet.getInt("dienumber");
                    String diecolor = resultSet.getString("diecolor");

                    checkStmt.setInt(1, gameId);
                    checkStmt.setInt(2, dienumber);
                    checkStmt.setString(3, diecolor);

                    try (ResultSet checkResult = checkStmt.executeQuery()) {
                        if (checkResult.next() && checkResult.getInt(1) == 0) {
                            Die die = new Die(gameId, currentRound, diecolor, resultSet.getInt("eyes"), dienumber);
                            leftoverDice.add(die);
                        }
                    }
                }
            }
        }
        return leftoverDice;
    }

    public void updateRoundTrackForDice(int gameId, int round, List<Die> dice) throws SQLException {
        String updateQuery = "UPDATE gamedie SET roundtrack = ? WHERE idgame = ? AND roundID = ? AND dienumber = ? AND diecolor = ? AND roundtrack IS NULL;";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            for (Die die : dice) {
                updateStmt.setInt(1, round);
                updateStmt.setInt(2, gameId);
                updateStmt.setInt(3, die.getRoundId());
                updateStmt.setInt(4, die.getDieNumber());
                updateStmt.setString(5, die.getDieColor());
                updateStmt.executeUpdate();
            }
        }
    }

    public int getRoundId(int gameId) {
        String query = "SELECT * FROM game WHERE idgame = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("current_roundID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getRound(int gameId) {
        String query = "SELECT * FROM game INNER JOIN round ON game.current_roundID = round.roundID WHERE game.idgame = ?;";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("round.roundnr");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
