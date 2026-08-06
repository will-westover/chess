package client;

import chess.ChessMove;
import chess.ChessPosition;
import serverfacade.GameData;
import serverfacade.ServerFacade;
import ui.DrawBoard;

import java.util.Scanner;

import chess.ChessGame;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

public class Repl implements ServerMessageObserver {
    private WebSocketFacade ws;
    private final ServerFacade facade;
    private String authToken = null;
    private GameData[] lastList = new GameData[0];
    private final int port;
    private boolean inGame = false;
    private int currentGameID;
    private String playerColor;
    private ChessGame currentGame;

    public Repl(int port){
        this.port = port;
        this.facade = new ServerFacade(port);
    }


    @Override
    public void notify(ServerMessage message){
        switch (message.getServerMessageType()){
            case LOAD_GAME -> {
                currentGame = ((LoadGameMessage)message).getGame();
                DrawBoard.design(currentGame, !"BLACK".equals(playerColor));
            }
            case NOTIFICATION -> System.out.println("\n" + ((NotificationMessage)message).getMessage());
            case ERROR -> System.out.println("\n" + ((ErrorMessage)message).getErrorMessage());
        }
    }

    public void run() {
        System.out.println("Welcome to the CS 240 chess game. Type 'help' to get started.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(authToken == null ? "\n [LOGGED OUT]>>> " : inGame ? "\n[GAME] >>> " : "\n[LOGGED IN] >>> ");
            String line = scanner.nextLine().trim();
            String[] tokens = line.split(" ");
            String cmd = tokens[0].toLowerCase();

            try {
                if (authToken == null) {
                    if (preLogin(cmd, tokens)) {
                        return;
                    }
                } else if (inGame){
                    gamePlay(cmd, tokens);
                } else {
                    postLogin(cmd, tokens);
                }
            } catch (Exception e) {
                System.out.println("Error: " + friendly(e));
            }
        }

    }

    private boolean preLogin(String cmd, String[] tokens) throws Exception {
        switch (cmd) {
            case "help" -> System.out.println("""
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to sign in
                    quit - exit
                    help - show this menu""");
            case "quit" -> {
                System.out.println("Bye bye");
                return true;
            }
            case "register" -> {
                if (tokens.length != 4) {
                    System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                    return false;
                }
                var auth = facade.register(tokens[1], tokens[3], tokens[2]);
                authToken = auth.authToken();
                System.out.println("Logged in as " + auth.username());
            }
            case "login" -> {
                if (tokens.length != 3) {
                    System.out.println("Usage: register <USERNAME> <PASSWORD>");
                    return false;
                }
                var auth = facade.login(tokens[1], tokens[2]);
                authToken = auth.authToken();
                System.out.println("Logged in as " + auth.username());
            }
            default -> System.out.println("Command not recognized, type 'help' instead");
        }
        return false;
    }

    private void postLogin(String cmd, String[] tokens) throws Exception {
        switch (cmd) {
            case "help" -> System.out.println("""
                    create <NAME> - to create a new game
                    list - to list all games
                    play <NUMBER> <WHITE||BLACK> to join a game
                    observe <NUMBER> - to watch a game
                    logout - to sign out
                    quit - to exit 
                    help - to show menu
                    """);
            case "logout" -> {
                facade.logout(authToken);
                authToken = null;
                System.out.println("Logged out.");
            }
            case "create" -> {
                if (tokens.length != 2) {
                    System.out.println("Usage: create <NAME>");
                    return;
                }
                facade.createGame(tokens[1], authToken);
                System.out.println("Created game: " + tokens[1]);
            }
            case "list" -> {
                lastList = facade.listGames(authToken).games();
                for (int i = 0; i < lastList.length; i++) {
                    var games = lastList[i];
                    System.out.printf("%d. %s white:%s black:%s%n",
                            i + 1, games.gameName(),
                            games.whiteUsername() == null ? "-" : games.whiteUsername(),
                            games.blackUsername() == null ? "-" : games.blackUsername());
                }
            }
            case "play" -> {
                if (tokens.length < 3) {
                    System.out.println("Usage: play <NUMBER> <WHITE||BLACK> ");
                    return;
                }
                int number;
                try {
                    number = Integer.parseInt(tokens[1]);
                } catch (Exception exception) {
                    System.out.println("Game number must be a number. Please try again.");
                    return;
                }
                if (number < 1 || number > lastList.length) {
                    System.out.println("No valid game for " + tokens[1] + "Please try again with 'list'.");
                    return;
                }
                int gameId = lastList[number - 1].gameID();
                facade.joinGame(tokens[2].toUpperCase(), gameId, authToken);
                System.out.println("Joined game: ");
                ws = new WebSocketFacade(port, this);
                ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId));
                inGame = true;
                currentGameID = gameId;
                playerColor = tokens[2].toUpperCase();
            }
            case "observe" -> {
                if (tokens.length < 2) {
                    System.out.println("Usage: observe <NUMBER>");
                    return;
                }
                int number;
                try {
                    number = Integer.parseInt(tokens[1]);
                } catch (Exception exception) {
                    System.out.println("Game number must be a number. Please try again.");
                    return;
                }
                if (number < 1 || number > lastList.length) {
                    System.out.println("No valid game for " + tokens[1] + ". Please try again with 'list'.");
                    return;
                }
                System.out.println("Observing game: ");
                int gameId = lastList[number -1].gameID();
                ws = new WebSocketFacade(port, this);
                ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId));
                inGame = true;
                currentGameID = gameId;
                playerColor = null;
            }
            case "quit" -> {
                System.out.println("Log out first please");
            }
            default -> {
                System.out.println("Unknown command. Try 'help' instead.");
            }
        }
    }

    private void gamePlay(String cmd, String[] tokens) throws Exception{
        switch (cmd){
            case "help" -> System.out.println("""
                    redraw - redraw the board
                    move <FROM> <TO> make a move (ex: move e3 e4);
                    highlight <POS> - show legal moves for a piece
                    leave - leave the game
                    resign - forfeit the game
                    help - show this menu
                    """);
            case "redraw" -> DrawBoard.design(currentGame, !"BLACK".equals(playerColor));
            case "leave" -> {
                ws.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, currentGameID));
                inGame = false;
            }
            case "resign" -> {}
            case "move" -> {
                if(tokens.length < 3){
                    System.out.println("Usage: move <FROM> <TO> (ex: move e5 e4");
                } else {
                    ChessPosition start = parsePos(tokens[1]);
                    ChessPosition end = parsePos(tokens[2]);
                    ws.send(new MakeMoveCommand(authToken, currentGameID,
                            new ChessMove(start, end, null)));
                }
            }
            case "highlight" -> {}
            default -> System.out.println("Unknown command. Try typing 'help'. ");
        }
    }

    private ChessPosition parsePos(String string){
        int col = string.charAt(0) - 'a' +1;
        int row = string.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private String friendly(Exception e) {
        String msg = e.getMessage();
        if (msg == null) {
            return "Something went wrong. Please try something else";
        }
        if (msg.contains("already taken")) {
            return "Sorry, that name is already taken";
        }
        if (msg.contains("unauthorized")) {
            return "Invalid name or password";
        }
        if (msg.contains("bad request")) {
            return "Bad request- please check your input";
        }
        return "Sorry, something went wrong. Please try 'list' or try again";
    }

}
