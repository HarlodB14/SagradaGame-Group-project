package DAL;

import Model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO implements IDAO {

    @Override
    public Game get(int id) {
        String query = "SELECT * FROM game WHERE idgame = ?";

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int turnIdPlayer = rs.getInt("turn_idplayer");
                    int currentRoundId = rs.getInt("current_roundID");
                    Timestamp creationDate = rs.getTimestamp("creationdate");

                    Game game = new Game();

                    game.setId(rs.getInt("idgame"));
                    game.setCreationDate(creationDate);
                    game.setTurnIdPlayer(turnIdPlayer);
                    game.setCurrentRoundId(currentRoundId);

                    return game;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<Game> getAll() throws SQLException {
        String query = "SELECT * FROM game ORDER BY idgame";
        List<Game> games = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Game game = get(rs.getInt("idgame"));
                games.add(game);
            }
        }

        return games;
    }

    @Override
    public int save(Object type) throws SQLException {
        if (type instanceof Game) {
            Game game = (Game) type;

            if (game.getId() > 0) {
                return update(game);
            } else {
                return insert(game);
            }
        }

        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (!(type instanceof Game)) {
            return 0;
        }

        Game game = (Game) type;
        String query = "INSERT INTO game (current_roundID, creationdate) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, game.getCurrentRoundId());
            ps.setTimestamp(2, game.getCreationDate());
            ps.executeUpdate();
            connection.commit();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating game failed");
                }
            }
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    @Override
    public int update(Object type) throws SQLException {
        if (!(type instanceof Game)) {
            return 0;
        }

        Game game = (Game) type;
        String query = "UPDATE game SET turn_idplayer = ?, current_roundID = ?, creationdate = ? WHERE idgame = ?";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, game.getTurnIdPlayer());
            ps.setInt(2, game.getCurrentRoundId());
            ps.setTimestamp(3, game.getCreationDate());
            ps.setInt(4, game.getId());
            int result = ps.executeUpdate();
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

    public List<Game> getGamesSortedByDate(boolean ascending) throws SQLException {
        String query = "SELECT * FROM game ORDER BY creationdate " + (ascending ? "ASC" : "DESC");
        List<Game> games = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Game game = get(rs.getInt("idgame"));
                games.add(game);
            }
        }

        return games;
    }

    public int getLatestTurnIdFromDatabase(int gameId) throws SQLException {
        String query = "SELECT * FROM game WHERE idgame = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("turn_idplayer");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
