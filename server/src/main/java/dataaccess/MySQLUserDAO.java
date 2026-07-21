package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{
    public void createUser(UserData data) throws DataAccessException{
        var sql = "INSERT INTO user (username, email, password) VALUES (?,?,?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql)) {
                prepareStatement.setString(1, data.username());
                prepareStatement.setString(2, data.email());
                var hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
                prepareStatement.setString(3, hashedPassword);
                prepareStatement.executeUpdate();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to create user", exception);
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        var sql = "SELECT username, email, password FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql)) {
                prepareStatement.setString(1, username);
                try (var result = prepareStatement.executeQuery()) {
                    if (result.next()) {
                        return new UserData(result.getString("username"),
                                            result.getString("email"),
                                            result.getString("password"));
                    }
                    return null;
                }
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to get authentication", exception);
        }
    }

    public void clear() throws DataAccessException{
        var sql = "TRUNCATE TABLE user";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql)) {
                prepareStatement.executeUpdate();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to delete user table", exception);
        }
    }
}
