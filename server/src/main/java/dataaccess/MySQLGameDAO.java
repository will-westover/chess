package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
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
        return null;
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

    }

    public void clear() throws DataAccessException{

    }
}
