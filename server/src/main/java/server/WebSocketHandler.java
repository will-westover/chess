package server;


import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void onMessage(Session session, String message){
        try{
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()){
                case CONNECT -> connect(session, command);
            }
        }catch (Exception e){
            sendError(session, "Error: " + e.getMessage());
        }
    }

    public void connect(Session session, UserGameCommand command) throws Exception{
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if(auth == null){sendError(session, "Error: invalid auth token"); return;}

        GameData game = gameDAO.getGame(command.getGameID());
        if(game == null){sendError(session, "Error: invalid game"); return;}

        connections.add(command.getGameID(), command.getAuthToken(), session);

        var loadMessage = new LoadGameMessage(game.game());
        connections.send(session, gson.toJson(loadMessage));

        String role = roleOf(auth.username(),game);
        var note = new NotificationMessage(auth.username() + " joined as " + role);
        connections.broadcast(command.getGameID(), command.getAuthToken(), gson.toJson(note));
    }

    private String roleOf(String username, GameData game){
        if(username.equals(game.whiteUsername())) return "white";
        if(username.equals(game.blackUsername())) return "black";
        return "an observer";
    }

    private void sendError(Session session, String message){
        try{
            connections.send(session, gson.toJson(new ErrorMessage(message)));
        } catch (Exception ignored){}
    }

    public void onConnect(Session session){

    }

    public void onClose (Session session){

    }
}
