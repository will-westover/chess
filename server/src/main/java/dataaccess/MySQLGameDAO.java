package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO{
    public int createGame(GameData gameData) throws DataAccessException {
        var sql = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                prepareStatement.setString(1, gameData.whiteUsername());
                prepareStatement.setString(2, gameData.blackUsername());
                prepareStatement.setString(3, gameData.gameName());
                var json = new Gson().toJson(gameData.game());
                prepareStatement.setString(4, json);
                prepareStatement.executeUpdate();
                var keys = prepareStatement.getGeneratedKeys();
                if(keys.next()){
                    return keys.getInt(1);
                }
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to create game", exception);
        }
        return 0;
    }

    public Collection<GameData> listGames() throws DataAccessException{
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
        var games = new ArrayList<GameData>();

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(sql)) {
                try (var result = ps.executeQuery()) {
                    while (result.next()) {
                        games.add(new GameData(
                                result.getInt("gameID"),
                                result.getString("whiteUsername"),
                                result.getString("blackUsername"),
                                result.getString("gameName"),
                                new Gson().fromJson(result.getString("game"), ChessGame.class)
                        ));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to list games", ex);
        }
        return games;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql)) {
                prepareStatement.setInt(1, gameID);
                try (var result = prepareStatement.executeQuery()) {
                    if (result.next()) {
                        return new GameData(
                                result.getInt("gameID"),
                                result.getString("whiteUsername"),
                                result.getString("blackUsername"),
                                result.getString("gameName"),
                                new Gson().fromJson(result.getString("game"), ChessGame.class)
                        );
                    }
                    return null;
                }
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to get game", exception);
        }
    }

    public void updateGame(GameData gameData) throws DataAccessException{
        var sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?  WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                var json = new Gson().toJson(gameData.game());
                preparedStatement.setString(4, json);
                preparedStatement.setInt(5, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game", ex);
        }
    }

    public void clear() throws DataAccessException{
        var sql = "TRUNCATE TABLE game";

        try (var conn = DatabaseManager.getConnection()) {
            try (var prepareStatement = conn.prepareStatement(sql)) {
                prepareStatement.executeUpdate();
            }

        } catch (SQLException exception) {
            throw new DataAccessException("Error: failed to delete game table", exception);
        }
    }
}
