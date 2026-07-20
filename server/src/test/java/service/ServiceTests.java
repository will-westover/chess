package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private RegisterService registerService;
    private ClearService clearService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGamesService listGamesService;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;


    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        listGamesService = new ListGamesService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);

    }

    private UserData makeUser() {
        return new UserData("username", "shanereese@byu.edu", "password:)");
    }

    private AuthData registerUser() throws Exception {
        return registerService.registerClient(makeUser());
    }

    @Test
    void registerSuccess() throws Exception {
        AuthData auth = registerUser();
        assertNotNull(auth.authToken());
    }

    @Test
    void registerDuplicate() throws Exception {
        registerUser();
        assertThrows(ServiceException.class, () -> registerService.registerClient(makeUser()));
    }

    @Test
    void clearSuccess() throws Exception {
        registerUser();
        clearService.clear();
        assertNull(userDAO.getUser("username"));
    }

    @Test
    void loginSuccess() throws Exception {
        registerUser();
        AuthData auth = loginService.loginClient(new UserData("username", null, "password:)"));
        assertNotNull(auth.authToken());
    }

    @Test
    void loginBadPassword() throws Exception {
        AuthData auth = registerUser();
        assertThrows(ServiceException.class, () -> loginService.loginClient(new UserData("username", "WRONG", null)));
    }

    @Test
    void logOutSuccess() throws Exception {
        AuthData auth = registerUser();
        logoutService.logoutClient(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logBadToken() throws Exception {
        assertThrows(ServiceException.class, () -> logoutService.logoutClient("badToken"));
    }

    @Test
    void listSuccess() throws Exception {
        AuthData auth = registerUser();
        createGameService.createGame(auth.authToken(), "game");
        var games = listGamesService.listGames(auth.authToken());
        assertEquals(1, games.size());
    }

    @Test
    void listBadToken() throws Exception {
        assertThrows(ServiceException.class, () -> listGamesService.listGames("badToken"));
    }

    @Test
    void createSuccess() throws Exception {
        AuthData auth = registerUser();
        int id = createGameService.createGame(auth.authToken(), "game");
        assertTrue(id > 0);
    }

    @Test
    void createBadToken() throws Exception {
        assertThrows(ServiceException.class, () -> createGameService.createGame("badToken", "game1"));
    }

    @Test
    void joinSuccess() throws Exception {
        AuthData auth = registerUser();
        int id = createGameService.createGame(auth.authToken(), "game");
        joinGameService.joinGame(auth.authToken(), "WHITE", id);
        assertEquals("username", gameDAO.getGame(id).whiteUsername());
    }

    @Test
    void joinBadColor() throws Exception {
        AuthData auth = registerUser();
        int id = createGameService.createGame(auth.authToken(), "game");
        assertThrows(ServiceException.class, () -> joinGameService.joinGame(auth.authToken(), null, id));
    }
}
