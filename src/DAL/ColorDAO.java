package DAL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO implements IDAO {

    @Override
    public Object get(int id) throws SQLException {
        throw new UnsupportedOperationException("Fetching by ID is not supported for accounts.");
    }

    public Object get(String id) throws SQLException {
        String color = null;
        String selectQuery = "SELECT * FROM color WHERE id = ?";
        try (PreparedStatement statement = DatabaseConnector.getConnection().prepareStatement(selectQuery)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    color = resultSet.getString("color");
                }
            }
        }

        return color;
    }

    @Override
    public List<String> getAll() throws SQLException {
        List<String> colors = new ArrayList<>();
        String selectQuery = "SELECT * FROM color";

        try (Statement statement = DatabaseConnector.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                String color = resultSet.getString("color");
                colors.add(color);
            }
        }

        return colors;

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
