package dataaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MySQLTests {

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
}
