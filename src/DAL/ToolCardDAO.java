package DAL;

import Model.Toolcard;
import Model.ToolcardFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolCardDAO implements IDAO {

    @Override
    public Toolcard get(int id) throws SQLException {
        String query = "SELECT * FROM toolcard WHERE idtoolcard = ?";

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idToolcard = rs.getInt("idtoolcard");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    return new Toolcard(idToolcard, name, description);
                }
            }
        }

        return null;
    }

    @Override
    public List<Toolcard> getAll() throws SQLException {
        String query = "SELECT * FROM toolcard";
        List<Toolcard> toolcards = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int idToolcard = rs.getInt("idtoolcard");
                String name = rs.getString("name");
                String description = rs.getString("description");
                toolcards.add(new Toolcard(idToolcard, name, description));
            }
        }

        return toolcards;
    }

    public List<Toolcard> getGameToolcards(int idgame) throws SQLException {
        String query = "SELECT * FROM gametoolcard WHERE idgame = ?";
        List<Toolcard> toolcards = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idgame);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idToolcard = rs.getInt("idtoolcard");
                    // omschrijving, naam vanuit factory ophalen want dat moet nederlands blijven voor in de view
                    Toolcard toolcardDetails = (Toolcard) ToolcardFactory.createToolcard(idToolcard);
                    if (toolcardDetails == null) {
                        continue;
                    }
                    Toolcard toolcard = new Toolcard(idToolcard, toolcardDetails.getName(), toolcardDetails.getDescription(), idgame);
                    toolcards.add(toolcard);
                }
            }
        }

        return toolcards;
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

        int gameId = (Integer) type;

        // Select 3 random tool cards
        String selectQuery = "SELECT idtoolcard FROM toolcard ORDER BY RAND() LIMIT 3";

        // Insert the tool cards into gametoolcard
        String insertQuery = "INSERT INTO gametoolcard (idtoolcard, idgame) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ResultSet rs = stmt.executeQuery(selectQuery);

            while (rs.next()) {
                int idToolCard = rs.getInt("idtoolcard");

                ps.setInt(1, idToolCard);
                ps.setInt(2, gameId);

                ps.executeUpdate();
            }
            connection.commit();
            return 1;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }


    @Override
    public int update(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int delete(Object type) throws SQLException {
        return 0;
    }

    public int getGameToolCardId(int gameId, int toolCardId) throws SQLException {
        String query = "SELECT gametoolcard FROM gametoolcard WHERE idgame = ? AND idtoolcard = ?";

        try (PreparedStatement stmt = DatabaseConnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, toolCardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("gametoolcard");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // return 0 or throw an exception if the gametoolcardid is not found
    }
}
