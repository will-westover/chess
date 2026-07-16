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
    private JoinGameservice joinGameService;

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
        joinGameservice = new JoinGameservice(authDAO, gameDAO);

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
    void login() throws Exception{
        registerService.registerClient("username", "password:)", "shanereese@byu.edu");
        AuthData auth = loginService.loginClient(new UserData("username", "password:)", null));
        assertNotNull(auth.authToken());
    }
    @Test
    void logOut() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        clearService.clear();
        assertNotNull(userDAO.getUser("username"));
    }
    @Test
    void listGames() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        clearService.clear();
        assertNotNull(userDAO.getUser("username"));
    }
    @Test
    void createGames() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        clearService.clear();
        assertNotNull(userDAO.getUser("username"));
    }
    @Test
    void joinGame() throws Exception{
        UserData user = new UserData("username", "password:)", "shanereese@byu.edu");
        registerService.registerClient(user);
        clearService.clear();
        assertNotNull(userDAO.getUser("username"));
    }
}
