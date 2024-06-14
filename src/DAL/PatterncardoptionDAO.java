package DAL;

import Model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PatterncardoptionDAO implements IDAO {
    @Override
    public Object get(int id) throws SQLException {
        return null;
    }

    @Override
    public List getAll() throws SQLException {
        return Collections.emptyList();
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

        int idgame = (Integer) type;

        List<Player> players = new ArrayList<>();

        String selectQuery = "SELECT * FROM player WHERE idgame = ?";

        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(selectQuery);
            ps.setInt(1, idgame);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Player player = new Player();
                player.setIdPlayer(rs.getInt("idplayer"));
                player.setUsername(rs.getString("username"));
                player.setIdGame(idgame);
                player.setPlayStatus(rs.getString("playstatus"));
                player.setSeqNr(rs.getInt("seqnr"));
                player.setPrivateObjectiveCardColor(rs.getString("private_objectivecard_color"));
                player.setScore(rs.getInt("score"));
                players.add(player);
            }

            List<Integer> patternCardIds = new ArrayList<>();
            for (int i = 1; i <= 24; i++) {
                patternCardIds.add(i);
            }
            Collections.shuffle(patternCardIds);

            int patternCardIndex = 0;

            for (Player player : players) {
                String insertPatternCardOption = "INSERT INTO patterncardoption (idpatterncard, idplayer) VALUES (?, ?)";

                PreparedStatement psInsert = connection.prepareStatement(insertPatternCardOption);
                for (int i = 0; i < 4; i++) {
                    psInsert.setInt(1, patternCardIds.get(patternCardIndex));
                    psInsert.setInt(2, player.getIdPlayer());
                    psInsert.addBatch();

                    patternCardIndex++;
                }

                psInsert.executeBatch();
            }

            connection.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }

        return 0;
    }

    @Override
    public int update(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }
}
