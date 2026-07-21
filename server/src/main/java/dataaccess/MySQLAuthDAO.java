package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO{

    void createAuth(AuthData authData) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?,?)";

        try (var conn = DatabaseManager.getConnection()) {
            try(var prepareStatement = conn.prepareStatement(sql)){
                prepareStatement.setString(1, authData.authToken());
                prepareStatement.setString(2, authData.username());
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to create tables", exception);
        }
    }

    AuthData getAuth(String authToken) throws DataAccessException{

    }

    void deleteAuth(String authToken) throws DataAccessException{

    }

    void clear() throws DataAccessException{

    }
}
