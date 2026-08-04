package websocket.commands;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
