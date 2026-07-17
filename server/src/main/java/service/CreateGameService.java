package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public int createGame(String authToken, String gameName) throws ServiceException, DataAccessException {
        AuthData auth = AuthValidation.validate(authDAO, authToken);
        if (gameName == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        int gameID = gameDAO.createGame(new GameData(0,
                null, null, gameName, new ChessGame()));
        return gameID;
    }
}
