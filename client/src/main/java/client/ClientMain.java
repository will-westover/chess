package client;

import chess.ChessGame;
import chess.ChessPiece;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        new Repl(port).run();
    }
}
