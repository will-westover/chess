package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
        final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData data) {
            users.put(data.username(),data);
    }

    public UserData getUser(String username) throws DataAccessException {
                return users.get(username);
    }
    public void clear(){
            users.clear();
    }
}
