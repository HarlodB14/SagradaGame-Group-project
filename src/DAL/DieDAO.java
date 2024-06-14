package DAL;

import Enumerations.Colour;
import Model.Die;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DieDAO implements IDAO {

    @Override
    public Object get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Die> getAll() throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public int save(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (type instanceof Integer) {
            int gameId = (Integer) type;
            String query = "INSERT INTO gamedie (idgame, dienumber, diecolor, eyes) VALUES (?,?,?,?)";
            try (Connection connection = DatabaseConnector.getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement ps = connection.prepareStatement(query);
                for (int i = 1; i <= 5; i++) {
                    int dieNumber = 1;
                    for (int k = 1; k <= 3; k++) {
                        for (int j = 1; j <= 6; j++) {
                            ps.setInt(1, gameId);
                            ps.setInt(2, dieNumber);
                            ps.setString(3, getColorName(i));
                            ps.setInt(4, j);
                            ps.addBatch();
                            dieNumber++;
                        }
                    }
                }
                ps.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                DatabaseConnector.rollback();
                throw e;
            }
        }
        return 0;
    }

    public List<Die> loadAllDice(int gameId) {
        List<Die> dice = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT idgame, dienumber, diecolor, eyes, roundtrack, roundID FROM gamedie WHERE idgame = ?")) {
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dice.add(new Die(
                        rs.getInt("idgame"),
                        rs.getInt("roundID"),
                        rs.getString("diecolor"),
                        rs.getInt("eyes"),
                        rs.getInt("dienumber")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dice;
    }

    public List<Die> loadAvailableDice(int gameId, int currentRoundId) {
        if (currentRoundId % 2 == 0) {
            currentRoundId -= 1;
        }

        List<Die> availableDice = new ArrayList<>();
        String query = "SELECT idgame, dienumber, diecolor, eyes FROM gamedie " +
                "WHERE idgame = ? AND roundID = ? AND roundtrack IS NULL AND NOT EXISTS (" +
                "SELECT * FROM playerframefield WHERE playerframefield.idgame = gamedie.idgame AND " +
                "playerframefield.diecolor = gamedie.diecolor AND playerframefield.dienumber = gamedie.dienumber)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, currentRoundId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableDice.add(new Die(
                        rs.getInt("idgame"),
                        currentRoundId,
                        rs.getString("diecolor"),
                        rs.getInt("eyes"),
                        rs.getInt("dienumber")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return availableDice;
    }

    @Override
    public int update(Object type) throws SQLException {
        return 0;
    }

    public int updateEyes(Die die) throws SQLException {
        String query = "UPDATE gamedie SET eyes = ? WHERE idgame = ? AND dienumber = ? AND diecolor = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, die.getEyes());
            ps.setInt(2, die.getIdGame());
            ps.setInt(3, die.getDieNumber());
            ps.setString(4, die.getDieColor());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }

    private String getColorName(int colorId) {
        switch (colorId) {
            case 1:
                return "blue";
            case 2:
                return "red";
            case 3:
                return "green";
            case 4:
                return "purple";
            case 5:
                return "yellow";
            default:
                throw new IllegalArgumentException("Invalid color ID");
        }
    }

    public void giveDieGameIDBasedOnNumberOfPlayers(int gameId, int numberOfPlayers) {
        List<Die> dice = loadAllDice(gameId); // Loads all dice, possibly without round IDs set
        Collections.shuffle(dice); // Randomize the order of dice

        int dicePerRound;
        switch (numberOfPlayers) {
            case 2:
                dicePerRound = 5;
                break;
            case 3:
                dicePerRound = 7;
                break;
            case 4:
                dicePerRound = 9;
                break;
            default:
                throw new IllegalStateException("Unexpected number of players: " + numberOfPlayers);
        }

        int totalRounds = 10; // Standard 10 rounds for Sagrada
        int totalDiceNeeded = dicePerRound * totalRounds;

        if (dice.size() < totalDiceNeeded) {
            throw new IllegalStateException("Not enough dice for the game configuration. Required: " + totalDiceNeeded + ", Available: " + dice.size());
        }

        try (Connection connection = DatabaseConnector.getConnection()) {
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE gamedie SET roundID = ? WHERE idgame = ? AND dienumber = ? AND diecolor = ?");

            // Assign round IDs to the first 'totalDiceNeeded' dice
            for (int i = 0; i < totalDiceNeeded; i++) {
                Die die = dice.get(i);
                int roundId = (i / dicePerRound) * 2  + 1; // Determine the round ID based on index

                updateStmt.setInt(1, roundId);
                updateStmt.setInt(2, gameId);
                updateStmt.setInt(3, die.getDieNumber());
                updateStmt.setString(4, die.getDieColor());
                updateStmt.addBatch();

                if (i % dicePerRound == (dicePerRound - 1)) { // Execute batch after filling a round
                    updateStmt.executeBatch();
                }
            }
            updateStmt.executeBatch(); // Ensure all remaining updates are executed
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dice with round IDs", e);
        }
    }

    public int getDieEyes(String dieColor, int dieNumber, int idgame) throws SQLException {
        String query = "SELECT * FROM gamedie WHERE diecolor = ? AND dienumber = ? AND idgame = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dieColor);
            stmt.setInt(2, dieNumber);
            stmt.setInt(3, idgame);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("eyes");
                }
            }
        }

        return 0;
    }


}
