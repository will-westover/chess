package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData) throws DataAccessException;
    Collection<GameData> listGames() throws  DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;

}
