package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
        final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData data) {
            users.put(data.userName(),data);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
            if(users.get(username) != null){
                return users.get(username);
            } else {
                return null;
            }
    }
    public void clear(){
            users.clear();
    }
}
