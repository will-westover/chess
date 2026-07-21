package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MySQLTests {

    private MySQLAuthDAO authDAO;

    private AuthData initializeAuth(){
        return new AuthData("testing", "tester");
    }
    private UserData initializeUser(){
        return new UserData("tester", "tester@gmail.com", "password");
    }

    @BeforeEach
    void setUp() throws Exception{
        DatabaseManager.createTable();
        authDAO = new MySQLAuthDAO();
        authDAO.clear();
    }

    @Test
    void verifyConnection() throws Exception {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            try (var prepareStatement = connection.prepareStatement("SELECT 1 + 1")) {
                var result = prepareStatement.executeQuery();
                result.next();
                assertEquals(2, result.getInt(1));
            }
        }

    }
    @Test
    void createAuthSuccess() throws Exception{
        var auth = initializeAuth();

        authDAO.createAuth(auth);

        var located = authDAO.getAuth("testing");
        assertNotNull(located);
        assertEquals("tester", located.username());
    }

    @Test
    void createAuthDuplicate() throws Exception{
        var auth = initializeAuth();

        authDAO.createAuth(auth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    void getAuthSuccess() throws Exception{

    }

    @Test
    void getAuthNotFound() throws Exception{

    }

    @Test
    void deleteAuthSuccess() throws Exception{

    }

    @Test
    void deleteAuthNonexistent() throws Exception{

    }

    @Test
    void clearSuccess() throws Exception{

    }

    @Test
    void createUserSuccess() throws Exception{

    }

    @Test
    void createUserDuplicate() throws Exception{

    }

    @Test
    void getUserSuccess() throws Exception{

    }

    @Test
    void getUserNotFound() throws Exception{

    }

    @Test
    void createGameSuccess() throws Exception{

    }

    @Test
    void createGameNullName() throws Exception{

    }

    @Test
    void getGameSuccess() throws Exception{

    }

    @Test
    void getGameNotFound() throws Exception{

    }

    @Test
    void listGameSuccess() throws Exception{

    }

    @Test
    void listGamesEmpty() throws Exception{

    }

    @Test
    void listGame() throws Exception{

    }

    @Test
    void updateGameSuccess() throws Exception{

    }

    @Test
    void updateGameNonexistent() throws Exception{

    }


}

