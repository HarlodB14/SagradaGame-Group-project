package DAL;

import Model.Public_ObjectiveCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicObjectiveCardDAO implements IDAO {

    @Override
    public Object get(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Public_ObjectiveCard> getAll() throws SQLException {
        List<Public_ObjectiveCard> cards = new ArrayList<>();
        String query = "SELECT * FROM public_objectivecard";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cards.add(new Public_ObjectiveCard(
                        resultSet.getInt("idpublic_objectivecard"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("points")));
            }
        } catch (SQLException e) {
            //System.err.println("Error executing SQL statement: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return cards;
    }

    public List<Public_ObjectiveCard> getCardsForGame(int gameId) throws SQLException {
        List<Public_ObjectiveCard> cards = new ArrayList<>();
        String query = "SELECT p.* FROM public_objectivecard p INNER JOIN gameobjectivecard_public g ON p.idpublic_objectivecard = g.idpublic_objectivecard WHERE g.idgame = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cards.add(new Public_ObjectiveCard(
                        resultSet.getInt("idpublic_objectivecard"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("points")));
            }
        } catch (SQLException e) {
            //System.err.println("Error executing SQL statement: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return cards;
    }

    @Override
    public int save(Object type) throws SQLException {
        return 0;
    }

    @Override
    public int insert(Object type) throws SQLException {
        if (!(type instanceof Integer)) {
            throw new IllegalArgumentException("Expected an Integer for gameId.");
        }

        int gameId = (Integer) type;

        String selectQuery = "SELECT idpublic_objectivecard FROM public_objectivecard ORDER BY RAND() LIMIT 3";
        String insertQuery = "INSERT INTO gameobjectivecard_public (idgame, idpublic_objectivecard) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            connection.setAutoCommit(false);

            while (rs.next()) {
                int idObjectiveCard = rs.getInt("idpublic_objectivecard");

                insertStmt.setInt(1, gameId);
                insertStmt.setInt(2, idObjectiveCard);

                insertStmt.executeUpdate();
            }

            connection.commit();
            return 1;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            //System.err.println("Error executing SQL statement: " + e.getMessage());
            e.printStackTrace();
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
}
