package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.AuthResponse;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private AuthResponse registerTestUser() throws Exception{
        return facade.register("will", "wwest00@byu.edu","password");
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }
    @BeforeEach
    void clear() throws Exception{
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void registerDuplicate() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void loginSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void loginPasswordError() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void logoutSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void logoutTokenError() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void createGameSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void createGameTokenError() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void listGameSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void listGamesTokenError() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void joinGameSuccess() throws Exception {
        var auth = registerTestUser();
    }

    @Test
    void joinGameTokenError() throws Exception {
        var auth = registerTestUser();
    }



}
