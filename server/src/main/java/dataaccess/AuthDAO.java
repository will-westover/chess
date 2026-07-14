package dataaccess;

import model.AuthData;
import java.util.Collection;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken)throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear()throws DataAccessException;

}
