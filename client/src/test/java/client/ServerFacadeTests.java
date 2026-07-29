package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.AuthResponse;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private AuthResponse registerTestUser() throws Exception {
        return facade.register("will", "wwest00@byu.edu", "password");
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }

    @Test
    void registerSuccess() throws Exception {
        var auth = registerTestUser();
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    void registerDuplicate() throws Exception {
        var auth = registerTestUser();
        Assertions.assertThrows(Exception.class, () -> facade.register("will", "wwest00@byu.edu",
                "password"));
    }

    @Test
    void loginSuccess() throws Exception {
        registerTestUser();
        var auth = facade.login("will", "password");
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    void loginPasswordError() throws Exception {
        registerTestUser();
        Assertions.assertThrows(Exception.class, () -> facade.login("will", "error"));
    }

    @Test
    void logoutSuccess() throws Exception {
        var auth = registerTestUser();
        Assertions.assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutTokenError() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.logout("error"));

    }

    @Test
    void createGameSuccess() throws Exception {
        var auth = registerTestUser();
        var game = facade.createGame("game", auth.authToken());
    }

    @Test
    void createGameTokenError() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.createGame("game", "error"));
    }

    @Test
    void listGameSuccess() throws Exception {
        var auth = registerTestUser();
        facade.createGame("game1", auth.authToken());
        facade.createGame("game2", auth.authToken());
        var list = facade.listGames(auth.authToken());
        Assertions.assertEquals(2, list.games().length);
    }

    @Test
    void listGamesTokenError() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.listGames("error"));
    }

    @Test
    void joinGameSuccess() throws Exception {
        var auth = registerTestUser();
        var game = facade.createGame("game", auth.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame("WHITE", game.gameID(), auth.authToken()));
    }

    @Test
    void joinGameTokenError() throws Exception {
        var auth = registerTestUser();
        var game = facade.createGame("game", auth.authToken());
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("WHITE", game.gameID(), "error"));
    }


}
