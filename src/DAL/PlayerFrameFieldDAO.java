package DAL;

import Model.Die;
import Model.PlayerFrameField;
import Model.Square;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerFrameFieldDAO implements IDAO {
    @Override
    public Object get(int id) throws SQLException {
        return null;
    }

    @Override
    public List getAll() throws SQLException {
        return Collections.emptyList();
    }

    public ArrayList<PlayerFrameField> getAll(int gameId) throws SQLException {
        ArrayList<PlayerFrameField> playerFrameFields = new ArrayList<>();
        String query = "SELECT idplayer, position_x, position_y, idgame, dienumber, diecolor " +
                "FROM playerframefield WHERE idgame = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gameId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerFrameField playerFrameField = new PlayerFrameField(
                            rs.getInt("idgame"),
                            rs.getInt("idplayer"),
                            rs.getInt("position_x"),
                            rs.getInt("position_y"),
                            rs.getInt("dienumber"),
                            rs.getString("diecolor")
                    );
                    playerFrameFields.add(playerFrameField);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving PlayerFrameFields for gameId: " + gameId, e);
        }
        return playerFrameFields;
    }

    public ArrayList<PlayerFrameField> getAllForPlayer(int gameId, int playerId) throws SQLException {
        ArrayList<PlayerFrameField> playerFrameFields = new ArrayList<>();
        String query = "SELECT idplayer, position_x, position_y, idgame, dienumber, diecolor " +
                "FROM playerframefield WHERE idgame = ? AND idplayer = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerFrameField playerFrameField = new PlayerFrameField(
                            rs.getInt("idgame"),
                            rs.getInt("idplayer"),
                            rs.getInt("position_x"),
                            rs.getInt("position_y"),
                            rs.getInt("dienumber"),
                            rs.getString("diecolor")
                    );
                    playerFrameFields.add(playerFrameField);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving PlayerFrameFields for gameId: " + gameId + " and playerId: " + playerId, e);
        }
        return playerFrameFields;
    }

    @Override
    public int save(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (!(type instanceof Integer)) {
            return 0;
        }

        int gameid = (Integer) type;

        List<Integer> playerIds = new ArrayList<Integer>();

        String selectQuery = "SELECT idplayer FROM player WHERE idgame = ?";

        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(selectQuery);
            ps.setInt(1, gameid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int playerId = rs.getInt("idplayer");
                playerIds.add(playerId);
            }

            String insertQuery = "INSERT INTO playerframefield (idplayer, position_x, position_y, idgame) VALUES (?, ?, ?, ?)";

            PreparedStatement psInsert = connection.prepareStatement(insertQuery);
            for (int playerId : playerIds) {
                for (int x = 1; x <= 5; x++) {
                    for (int y = 1; y <= 4; y++) {
                        psInsert.setInt(1, playerId);
                        psInsert.setInt(2, x);
                        psInsert.setInt(3, y);
                        psInsert.setInt(4, gameid);
                        psInsert.addBatch();
                    }
                }

                psInsert.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }

        return 20 * playerIds.size();
    }

    @Override
    public int update(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }

    public List<PlayerFrameField> loadPlayerFrameFields(int gameId) {
        List<PlayerFrameField> playerFrameFields = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT idplayer, position_x, position_y, idgame, dienumber, diecolor FROM playerframefield WHERE idgame = ?")) {
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                playerFrameFields.add(new PlayerFrameField(
                        rs.getInt("idgame"),
                        rs.getInt("idplayer"),
                        rs.getInt("position_x"),
                        rs.getInt("position_y"),
                        rs.getInt("dienumber"),
                        rs.getString("diecolor")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerFrameFields;
    }

public void placeDieOnPlayerFrameField(Square square, Die selectedDie, int playerID) throws SQLException {
    String query = "UPDATE playerframefield SET dienumber = ?, diecolor = ? WHERE idplayer = ? AND position_x = ? AND position_y = ?";

    try (Connection connection = DatabaseConnector.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
        connection.setAutoCommit(false);
        ps.setInt(1, selectedDie.getDieNumber());
        ps.setString(2, selectedDie.getDieColor());
        ps.setInt(3, playerID);
        ps.setInt(4, square.getXPos());
        ps.setInt(5, square.getYPos());

        ps.executeUpdate();
        connection.commit();
    } catch (SQLException e) {
        DatabaseConnector.rollback();
        e.printStackTrace();
    }
}
}
