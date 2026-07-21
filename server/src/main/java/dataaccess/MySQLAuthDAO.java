package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO{

    public void createAuth(AuthData authData) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?,?)";

        try (var conn = DatabaseManager.getConnection()) {
            try(var prepareStatement = conn.prepareStatement(sql)){
                prepareStatement.setString(1, authData.authToken());
                prepareStatement.setString(2, authData.username());
                prepareStatement.executeUpdate();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to create tables", exception);
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        var sql = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try(var prepareStatement = conn.prepareStatement(sql)){
                prepareStatement.setString(1, authToken);
                try(var result = prepareStatement.executeQuery()){
                    if (result.next()){
                        return new AuthData(result.getString("authToken"),
                                            result.getString("username"));
                    }
                    return null;
                }
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to get authentication", exception);
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{

    }

    public void clear() throws DataAccessException{

    }
}
