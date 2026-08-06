package server;


import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case MAKE_MOVE -> makeMove(session, gson.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(session, command);
            }
        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void makeMove(Session session, MakeMoveCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        if (auth == null) {
            sendError(session, "Error: invalid auth token");
            return;
        }
        GameData gameData = gameDAO.getGame(command.getGameID());

        if (gameData == null) {
            sendError(session, "Error: invalid game");
            return;
        }
        ChessGame game = gameData.game();

        if (game.isGameOver()) {
            sendError(session, "Error: game is now over");
            return;
        }
        String username = auth.username();

        ChessGame.TeamColor color;
        if (username.equals(gameData.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            sendError(session, "Error: observers cannot make moves");
            return;
        }

        if (game.getTeamTurn() != color) {
            sendError(session, "Error: please wait for your turn");
            return;
        }
        try {
            game.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            sendError(session, "Error: invalid move");
            return;
        }

        connections.broadcast(command.getGameID(), null, gson.toJson(new LoadGameMessage(game)));
        connections.broadcast(command.getGameID(), command.getAuthToken(),
                gson.toJson(new NotificationMessage(username + " made a move")));

        ChessGame.TeamColor opponent = (color == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        if (game.isInCheckmate(opponent)) {
            connections.broadcast(command.getGameID(), null,
                    gson.toJson(new NotificationMessage("Checkmate! " + username + " wins")));
            game.setGameOver(true);
        } else if (game.isInStalemate(opponent)) {
            connections.broadcast(command.getGameID(), null,
                    gson.toJson(new NotificationMessage("Stalemate")));
            game.setGameOver(true);
        } else if (game.isInCheck(opponent)) {
            connections.broadcast(command.getGameID(), null,
                    gson.toJson(new NotificationMessage("Check")));
        }
        gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game));
    }

    private void leave(Session session, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(session, "Error: invalid auth token");
            return;
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        String username = auth.username();

        if (gameData != null) {
            if (username.equals(gameData.whiteUsername())) {
                gameDAO.updateGame(new GameData(gameData.gameID(), null, gameData.blackUsername(),
                        gameData.gameName(), gameData.game()));
            } else if (username.equals(gameData.blackUsername())) {
                gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                        null, gameData.gameName(), gameData.game()));
            }
        }
        connections.remove(command.getGameID(), command.getAuthToken());
        connections.broadcast(command.getGameID(), command.getAuthToken(),
                gson.toJson(new NotificationMessage(username + " left the game")));
    }

    private void resign(Session session, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(session, "Error: invalid auth token");
            return;
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            sendError(session, "Error: invalid game");
            return;
        }
        String username = auth.username();

        boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
        if (!isPlayer) {
            sendError(session, "Error: observers cannot resign");
            return;
        }
        if (gameData.game().isGameOver()) {
            sendError(session, "Error: game is over");
            return;
        }
        ChessGame game = gameData.game();
        game.setGameOver(true);
        gameDAO.updateGame(new GameData(gameData.gameID(),
                gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
        connections.broadcast(command.getGameID(), null,
                gson.toJson(new NotificationMessage(username + " resigned")));
    }

    public void connect(Session session, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(session, "Error: invalid auth token");
            return;
        }

        GameData game = gameDAO.getGame(command.getGameID());
        if (game == null) {
            sendError(session, "Error: invalid game");
            return;
        }

        connections.add(command.getGameID(), command.getAuthToken(), session);

        var loadMessage = new LoadGameMessage(game.game());
        connections.send(session, gson.toJson(loadMessage));

        String role = roleOf(auth.username(), game);
        var note = new NotificationMessage(auth.username() + " joined as " + role);
        connections.broadcast(command.getGameID(), command.getAuthToken(), gson.toJson(note));
    }

    private String roleOf(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return "white";
        }
        if (username.equals(game.blackUsername())) {
            return "black";
        }
        return "an observer";
    }

    private void sendError(Session session, String message) {
        try {
            connections.send(session, gson.toJson(new ErrorMessage(message)));
        } catch (Exception ignored) {
        }
    }

    public void onConnect(Session session) {

    }

    public void onClose(Session session) {

    }
}
