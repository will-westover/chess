package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData data) throws DataAccessException;
    UserData getUser(String Username) throws DataAccessException;
    void clear() throws DataAccessException;
}