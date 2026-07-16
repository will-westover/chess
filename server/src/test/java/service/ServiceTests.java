package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ServiceTests.class);
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

    private AuthData registerUser() throws Exception{
        return registerService.registerClient(new UserData("username","password:)", "shanereese@byu.edu"));
    }

    @BeforeEach
    void setUp(){
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

    @Test
    void registerSuccess() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        AuthData auth = registerService.registerClient(user);
        assertNotNull(auth.authToken());
    }
    @Test
    void registerDuplicate() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        assertThrows(ServiceException.class, () -> registerService.registerClient(user));
    }
    @Test
    void clearSuccess() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        clearService.clear();
        assertNull(userDAO.getUser("username"));
    }
    @Test
    void loginSuccess() throws Exception{
        registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        AuthData auth = loginService.loginClient(new UserData("username", "password:)", null));
        assertNotNull(auth.authToken());
    }
    @Test
    void loginBadPassword() throws Exception{
        registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        assertThrows(ServiceException.class, () -> loginService.loginClient(new UserData("username", "WRONG", null)));
    }
    @Test
    void logOutSuccess() throws Exception{
        AuthData auth = registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        logoutService.logoutClient(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()));
    }
    @Test
    void logBadToken() throws Exception{
        assertThrows(ServiceException.class, () -> logoutService.logoutClient("badToken"));
    }

    @Test
    void listSuccess() throws Exception{
        AuthData auth = registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        createGameService.createGame(auth.authToken(), "game");
        var games = listGamesService.listGames(auth.authToken());
        assertEquals(1,games.size());
    }
    @Test
    void listBadToken() throws Exception{
        assertThrows(ServiceException.class, () -> listGamesService.listGames("badToken"));
    }
    @Test
    void createSuccess() throws Exception{
        AuthData auth = registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        int id = createGameService.createGame(auth.authToken(),"game");
        assertTrue(id>0);
    }
    @Test
    void createBadToken() throws Exception{
        assertThrows(ServiceException.class, () -> createGameService.createGame("badToken","game1"));
    }
    @Test
    void joinSuccess() throws Exception{
        AuthData auth = registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        int id = createGameService.createGame(auth.authToken(),"game");
        joinGameService.joinGame(auth.authToken(),"WHITE",id);
        assertEquals("username", gameDAO.getGame(id).whiteUsername());
    }
    @Test
    void joinBadColor() throws Exception{
        AuthData auth = registerService.registerClient(new UserData("username", "password:)", "shanereese@byu.edu"));
        int id = createGameService.createGame(auth.authToken(),"game");
        assertThrows(ServiceException.class, () -> joinGameService.joinGame(auth.authToken(),null,id));
    }
}
