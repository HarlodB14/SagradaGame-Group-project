package DAL;

import Model.FavorToken;
import Model.Game;
import Model.Patterncard;
import Model.PurchaseHistoryEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FavorTokenDAO implements IDAO {
    @Override
    public Object get(int id) throws SQLException {
        throw new UnsupportedOperationException("FavorTokenDAO does not support get() with single parameter.");
    }

    public FavorToken get(int gameId, int favorTokenId) throws SQLException {
        String query = "SELECT * FROM gamefavortoken WHERE idgame = ? AND idfavortoken = ?";

        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, favorTokenId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer roundId = rs.getInt("roundID");
                    Integer idPlayer = rs.getInt("idplayer");
                    Integer gameToolCardId = rs.getInt("gametoolcard");
                    return new FavorToken(favorTokenId, gameId, roundId, idPlayer, gameToolCardId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List getAll() throws SQLException {
        throw new UnsupportedOperationException("FavorTokenDAO does not support getAll() without parameters.");
    }

    public List<FavorToken> getAllFromGame(int gameId) throws SQLException {
        String query = "SELECT * FROM gamefavortoken WHERE idgame = ?";
        List<FavorToken> favorTokens = new ArrayList<>();

        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, gameId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FavorToken favorToken = get(gameId, rs.getInt("idfavortoken"));
                    favorTokens.add(favorToken);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favorTokens;
    }

    public List<FavorToken> getAllFromPlayer(int playerId) throws SQLException {
        String query = "SELECT * FROM gamefavortoken WHERE idplayer = ?";
        List<FavorToken> favorTokens = new ArrayList<>();

        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("gametoolcard") == 0 && rs.getInt("roundID") == 0) {
                        FavorToken favorToken = get(playerId, rs.getInt("idfavortoken"));
                        favorTokens.add(favorToken);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return favorTokens;
    }

    @Override
    public int save(Object type) throws SQLException {
        if (type instanceof FavorToken) {
            FavorToken favorToken = (FavorToken) type;

            if (favorToken.getIdGame() > 0 && favorToken.getIdFavorToken() > 0) {
                return update(favorToken);
            } else {
                return insert(favorToken);
            }
        }

        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (!(type instanceof Integer)) {
            return 0;
        }

        int idgame = (Integer) type;
        String insertQuery = "INSERT INTO gamefavortoken (idfavortoken, idgame) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            for (int i = 1; i <= 24; i++) {
                ps.setInt(1, i);
                ps.setInt(2, idgame);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            connection.commit();
            return results.length;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    @Override
    public int update(Object type) throws SQLException {
        if (!(type instanceof FavorToken)) {
            return 0;
        }

        FavorToken favorToken = (FavorToken) type;
        String query = "UPDATE gamefavortoken SET idplayer = ?, gametoolcard = ?, roundID = ? WHERE idgame = ? AND idfavortoken = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(query);
            if (favorToken.getIdPlayer() != null) {
                ps.setInt(1, favorToken.getIdPlayer());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            if (favorToken.getGameToolCard() != null) {
                ps.setInt(2, favorToken.getGameToolCard());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            if (favorToken.getRoundID() != null) {
                ps.setInt(3, favorToken.getRoundID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setInt(4, favorToken.getIdGame());
            ps.setInt(5, favorToken.getIdFavorToken());

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
        throw new UnsupportedOperationException("FavorTokenDAO does not support delete() with single parameter.");
    }

    public int delete(int gameId, int favorTokenId) throws SQLException {
        String query = "DELETE FROM gamefavortoken WHERE idgame = ? AND idfavortoken = ?";
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, gameId);
            stmt.setInt(2, favorTokenId);
            int result = stmt.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    public int getToolCardPrice(int toolCardId, int gameId) {
        String query = "SELECT COUNT(*) AS count FROM gamefavortoken WHERE idgame = ? AND gametoolcard = ?";
        int price = 1;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, toolCardId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 0) {
                        price = 2;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return price;
    }

    public ArrayList<PurchaseHistoryEntry> getPurchaseHistory(int gameId, int toolcardId) {
        // Corrected SQL query to count purchases by each player
        String query = "SELECT idplayer, roundID, COUNT(*) as count FROM gamefavortoken WHERE idgame = ? AND gametoolcard = ? GROUP BY idplayer, roundID";
        ArrayList<PurchaseHistoryEntry> historyEntryArrayList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, toolcardId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseHistoryEntry purchaseHistoryEntry = new PurchaseHistoryEntry(rs.getInt("idplayer"), rs.getInt("roundID"), rs.getInt("count"));
                    historyEntryArrayList.add(purchaseHistoryEntry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyEntryArrayList;
    }

    public void assignFavorTokensFromPatternCard(Patterncard patternCard, int playerId, int gameId) throws SQLException {
        int favorTokenCount = patternCard.getDifficulty();
        List<FavorToken> gameFavorTokens = this.getAllFromGame(gameId);

        for (FavorToken favorToken : gameFavorTokens) {
            if (favorToken.getRoundID() == 0 && favorToken.getGameToolCard() == 0 && favorToken.getIdPlayer() == 0) {
                favorToken.setIdPlayer(playerId);
                favorToken.setGameToolCard(null);
                favorToken.setRoundID(null);
                this.save(favorToken);

                favorTokenCount--;

                if (favorTokenCount == 0) {
                    break;
                }
            }
        }
    }

    public void placeOnGameToolCard(int favorTokenCount, int gameId, int playerId, int gameToolCardId, int roundId) throws SQLException {
        List<FavorToken> gameFavorTokens = this.getAllFromGame(gameId);

        for (FavorToken favorToken : gameFavorTokens) {
            if (favorToken.getRoundID() == 0 && favorToken.getGameToolCard() == 0 && favorToken.getIdPlayer() == playerId) {
                favorToken.setIdPlayer(playerId);
                favorToken.setGameToolCard(gameToolCardId);
                favorToken.setRoundID(roundId);
                this.save(favorToken);

                favorTokenCount--;

                if (favorTokenCount == 0) {
                    break;
                }
            }
        }
    }

    public boolean hasBoughtToolCards(int gameId, int playerId, int roundId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM gamefavortoken WHERE idgame = ? AND idplayer = ? AND roundID = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, roundId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int count = rs.getInt("count");
                return count > 0;

            }
        }
    }


}
