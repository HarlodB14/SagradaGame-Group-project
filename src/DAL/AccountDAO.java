package DAL;

import Model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements IDAO {

    @Override
    public Object get(int id) throws SQLException {
        throw new UnsupportedOperationException("Fetching by ID is not supported for accounts.");
    }

    public Object get(String username) throws SQLException {
        String query = "SELECT * FROM account WHERE username = ?";

        try (PreparedStatement ps = DatabaseConnector.getConnection().prepareStatement(query)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");

                    return new Account(username, password);
                }
            }
        }

        return null;
    }

    @Override
    public List<Account> getAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM account")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                Account account = new Account(username, password);
                accounts.add(account);
            }
        }
        return accounts;
    }

    @Override
    public int save(Object type) throws SQLException {
        // Not applicable for Account saving
        return 0;
    }


    public boolean usernameExists(String username) throws SQLException {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM account WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0; // Returns true if count is greater than 0 (i.e., if the username exists)
            }
        }
        return false; // Return false if an error occurs or if the count is 0
    }


    public boolean exists(Object type) throws SQLException {
        if (!(type instanceof Account)) {
            throw new IllegalArgumentException("Object must be an instance of account");
        }

        Account account = (Account) type;
        String username = account.getUsername();
        String password = account.getPassword();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM account WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0; // Returns true if count is greater than 0 (i.e., if the account exists with the given username and password)
            }
        }
        return false; // Return false if an error occurs or if the count is 0
    }


    @Override
    public int insert(Object type) throws SQLException {
        Account account = (Account) type;
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO account (username, password) VALUES (?, ?)");
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            int result = stmt.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    @Override
    public int update(Object type) throws SQLException {
        Account account = (Account) type;
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement("UPDATE account SET password = ? WHERE username = ?");
            stmt.setString(1, account.getPassword());
            stmt.setString(2, account.getUsername());
            int result = stmt.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }

    @Override
    public int delete(Object type) throws SQLException {
        Account account = (Account) type;
        try (Connection connection = DatabaseConnector.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM account WHERE username = ?");
            stmt.setString(1, account.getUsername());
            int result = stmt.executeUpdate();
            connection.commit();
            return result;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            throw e;
        }
    }
}
