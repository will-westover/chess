package server;


import org.eclipse.jetty.websocket.api.Session;

public class WebSocketHandler {

    public void onConnect(Session session){

    }

    public void onMessage(Session session, String message){
        System.out.println("WS received: " + message);
    }
    public void onClose (Session session){

    }
}
