package client;

import serverfacade.GameData;
import serverfacade.ServerFacade;
import ui.DrawBoard;

import java.util.Scanner;


public class Repl {

    private final ServerFacade facade;
    private String authToken = null;
    private GameData[] lastList = new GameData[0];

    public Repl(int port) {
        this.facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to the CS 240 chess game. Type 'help' to get started.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(authToken == null ? "\n [LOGGED OUT]>>> " : "\n[LOGGED IN] >>> ");
            String line = scanner.nextLine().trim();
            String[] tokens = line.split(" ");
            String cmd = tokens[0].toLowerCase();

            try {
                if (authToken == null) {
                    if (preLogin(cmd, tokens)) {
                        return;
                    }
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
                DrawBoard.design(tokens[2].equalsIgnoreCase("WHITE"));
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
                DrawBoard.design(true);
            }
            case "quit" -> {
                System.out.println("Log out first please");
            }
            default -> {
                System.out.println("Unknown command. Try 'help' instead.");
            }
        }
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
