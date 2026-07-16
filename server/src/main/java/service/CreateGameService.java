package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public int createGame(String authToken, String gameName) throws ServiceException, DataAccessException {
        AuthData auth = AuthValidtion.validate(authDAO, authToken);
        if(gameName == null){
            throw new ServiceException(400, "Error: bad request");
        }
        int game = gameDAO.createGame(new GameData(0,
                null, null, gameName, new ChessGame()));
        return game;
    }
}
