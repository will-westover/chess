package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MySQLTests {

    private MySQLAuthDAO authDAO;
    private MySQLUserDAO userDAO;
    private MySQLGameDAO gameDAO;

    private AuthData makeAuth(){
        return new AuthData("testing", "tester");
    }
    private UserData makeUser(){
        return new UserData("tester", "tester@gmail.com", "password");
    }

    private GameData makeGame(){
        return new GameData(123, "player1", "player2","game", new ChessGame() );
    }

    @BeforeEach
    void setUp() throws Exception{
        DatabaseManager.createTable();
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
    }

    @Test
    void verifyConnection() throws Exception {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            try (var prepareStatement = connection.prepareStatement("SELECT 1 + 1")) {
                try (var result = prepareStatement.executeQuery()){
                result.next();
                assertEquals(2, result.getInt(1));
                }
            }
        }

    }
    @Test
    void createAuthSuccess() throws Exception{
        var auth = makeAuth();
        authDAO.createAuth(auth);

        var located = authDAO.getAuth("testing");
        assertNotNull(located);
        assertEquals("tester", located.username());
    }

    @Test
    void createAuthDuplicate() throws Exception{
        var auth = makeAuth();
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    void getAuthSuccess() throws Exception{
        var auth = makeAuth();
        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("testing"));
    }

    @Test
    void getAuthNotFound() throws Exception{
        assertNull(authDAO.getAuth("nope"));
    }

    @Test
    void deleteAuthSuccess() throws Exception{
        var auth = makeAuth();
        authDAO.createAuth(auth);

        authDAO.deleteAuth("testing");
        assertNull(authDAO.getAuth("testing"));

    }

    @Test
    void deleteAuthNonexistent() throws Exception{
        assertDoesNotThrow(() -> authDAO.deleteAuth("nope"));
    }

    @Test
    void clearAuthSuccess() throws Exception{
        authDAO.createAuth(makeAuth());
        authDAO.clear();
        assertNull(authDAO.getAuth("testing"));
    }

    @Test
    void createUserSuccess() throws Exception{
        var user = makeUser();
        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("tester"));
    }

    @Test
    void createUserDuplicate() throws Exception{
        var user = makeUser();
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    void getUserSuccess() throws Exception{
        var user = makeUser();
        userDAO.createUser(user);

        assertEquals("tester@gmail.com",userDAO.getUser("tester").email());
    }

    @Test
    void getUserNotFound() throws Exception{
        assertNull(userDAO.getUser("nobody"));
    }

    @Test
    void clearUserSuccess() throws Exception {
        var user = makeUser();
        userDAO.createUser(user);

        userDAO.clear();
        assertNull(userDAO.getUser("tester"));
    }


    @Test
    void createGameSuccess() throws Exception{
        int id = gameDAO.createGame(makeGame());

        assertTrue(id>0);
    }

    @Test
    void createGameNullName() throws Exception{
        var nullGame = new GameData(0, null,null,null, new ChessGame());

        assertThrows(DataAccessException.class, () ->gameDAO.createGame(nullGame));
    }

    @Test
    void getGameSuccess() throws Exception{
        int id = gameDAO.createGame(makeGame());

        assertEquals("game", gameDAO.getGame(id).gameName());
    }

    @Test
    void getGameNotFound() throws Exception{
        assertNull(gameDAO.getGame(9999));
    }

    @Test
    void listGameSuccess() throws Exception{
        int id1 = gameDAO.createGame(makeGame());
        int id2 = gameDAO.createGame(makeGame());

        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void listGamesEmpty() throws Exception{
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void updateGameSuccess() throws Exception{
        int id = gameDAO.createGame(makeGame());

        gameDAO.updateGame(new GameData(id, "will", null, "game",
                            new ChessGame()));
        assertEquals("will", gameDAO.getGame(id).whiteUsername());
    }

    @Test
    void updateGameNonexistent() throws Exception {
        assertDoesNotThrow(()-> gameDAO.updateGame(new GameData(4378, null,
                null,"random", new ChessGame())));
    }

    @Test
    void clearGameSuccess() throws Exception {
        int id = gameDAO.createGame(makeGame());
        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }


}

