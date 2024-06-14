package DAL;

import Model.Round;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RoundDAO implements IDAO {


    @Override
    public Round get(int id) throws SQLException {
        String query = "SELECT * FROM round WHERE roundID = ?";

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int roundId = rs.getInt("roundID");
                    int roundNr = rs.getInt("roundnr");
                    boolean clockwise = rs.getBoolean("clockwise");

                    return new Round(roundId, roundNr, clockwise);
                }
            }
        }

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