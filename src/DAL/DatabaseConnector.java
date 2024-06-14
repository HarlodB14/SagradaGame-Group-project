package DAL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String DATABASE_SETTINGS_JSON = "src/dal/databaseSettings.json";
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnector() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            JSONObject jsonObject;
            try (FileReader reader = new FileReader(DATABASE_SETTINGS_JSON)) {
                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(reader);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                throw new SQLException("Failed to load database JSON file", e);
            }

            JSONObject dbObject = (JSONObject) jsonObject.get("db");
            String url = (String) dbObject.get("url");
            String username = (String) dbObject.get("username");
            String password = (String) dbObject.get("password");

            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    //rollback methode voor transacties
    public static void rollback() throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }

    // Method to properly close the connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                //System.out.println("Database connection closed successfully.");
            } catch (SQLException e) {
                //System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
}
