package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public int createGame(GameData gameData) {
        int newId = nextId++;
        GameData newGame = new GameData(
                newId, gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game());
        games.put(newGame.gameID(), newGame);
        return newId;
    }

    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    public void clear() throws DataAccessException {
        games.clear();
    }
}
