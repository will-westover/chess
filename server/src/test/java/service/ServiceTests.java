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

    @BeforeEach
    void setUp(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
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
        assertNotNull(userDAO.getUser("user1"));
    }
}
