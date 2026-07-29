package client;

import serverfacade.GameData;
import serverfacade.ServerFacade;

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
                    register <USERNAME><PASSWORD><EMAIL> - create account
                    login <USERNAME><PASSWORD> - sign in
                    quit - exit
                    help - show this menu""");
            case "quit" -> {
                System.out.println("Bye bye");
                return true;
            }
            case "register" -> {
                var auth = facade.register(tokens[1], tokens[3], tokens[2]);
                authToken = auth.authToken();
                System.out.println("Logged in as" + auth.username());
            }
            case "login" -> {
                var auth = facade.login(tokens[1], tokens[2]);
                authToken = auth.authToken();
                System.out.println("Logged in as" + auth.username());
            }
            default -> System.out.println("Command not recognized, type 'help' instead");
        }
        return false;
    }

    private void postLogin(String cmd, String[] tokens) throws Exception {
        switch (cmd) {
            case "help" -> System.out.println("""
                    create <NAME> - create a new game
                    list - list all games
                    play <NUMBER><WHITE||BLACK> join a game
                    observe <NUMBER>- watch a game
                    logout - sign out
                    quit - exit 
                    help - show menu
                    """);
            case "logout" -> {
                facade.logout(authToken);
                authToken = null;
                System.out.println("Logged out.");
            }
            case "create" -> {
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
                int gameId = lastList[Integer.parseInt(tokens[1]) - 1].gameID();
                facade.joinGame(tokens[2].toUpperCase(), gameId, authToken);
                System.out.println("Joined game: ");
                //add the draw board here
            }
            case "observe" -> {
                lastList[Integer.parseInt(tokens[1]) - 1].gameID();
                System.out.println("Joined game: ");
                //add the draw board here too
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
        return e.getMessage();
    }

}
