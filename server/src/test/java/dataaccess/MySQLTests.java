package dataaccess;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class MySQLTests {

    @Test
    void verifyConnection() throws Exception{
        DatabaseManager.createDatabase();

    }
}
