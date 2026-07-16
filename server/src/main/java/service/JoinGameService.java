package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(String authToken, String playerColor, int gameId)
            throws ServiceException, DataAccessException {

        AuthData auth = AuthValidation.validate(authDAO, authToken);
        GameData game = gameDAO.getGame(gameId);
        if(playerColor == null){
            throw new ServiceException(400, "Error: bad request");
        }

        if(game == null){
            throw new ServiceException(400, "Error: bad request");
        }
        String username = auth.username();
        GameData updatedGame;

        if(playerColor.equals("WHITE")){
            if(game.whiteUsername() != null){
                throw new ServiceException(403, "Error: already taken");
            }
            updatedGame = new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game());
        }else if (playerColor.equals("BLACK")){
            if(game.blackUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }
            updatedGame = new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game());
        } else{
            throw new ServiceException(400, "Error: bad request");
        }
        gameDAO.updateGame(updatedGame);

    }
}
