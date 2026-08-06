package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>>
            games = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        games.putIfAbsent(gameID, new ConcurrentHashMap<>());
        games.get(gameID).put(authToken, session);
    }

    public void remove(int gameID, String authToken) {
        var game = games.get(gameID);
        if (game != null) {
            game.remove(authToken);
        }
    }

    public void broadcast(int gameID, String excludeAuthToken, String message) throws IOException {
        var game = games.get(gameID);
        if (game == null) {
            return;
        }
        for (var entry : game.entrySet()) {
            String authToken = entry.getKey();
            Session session = entry.getValue();
            if (session.isOpen() && !authToken.equals(excludeAuthToken)) {
                session.getRemote().sendString(message);
            }
        }
    }

    public void send(Session session, String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
}
