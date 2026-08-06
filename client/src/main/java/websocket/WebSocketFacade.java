package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;


public class WebSocketFacade extends Endpoint {
    private final Gson gson = new Gson();
    private final ServerMessageObserver observer;
    private final Session session;

    public WebSocketFacade(int port, ServerMessageObserver observer) throws Exception {
        this.observer = observer;
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String json) {
                ServerMessage base = gson.fromJson(json, ServerMessage.class);
                ServerMessage full = switch (base.getServerMessageType()) {
                    case LOAD_GAME -> gson.fromJson(json, LoadGameMessage.class);
                    case NOTIFICATION -> gson.fromJson(json, NotificationMessage.class);
                    case ERROR -> gson.fromJson(json, ErrorMessage.class);
                };
                observer.notify(full);
            }
        });
    }


    public void send(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }
}
