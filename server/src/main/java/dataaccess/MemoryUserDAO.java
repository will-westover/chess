package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
        final private HashMap<String, UserData> users = new HashMap<>();
        private int nextId =1;
        UserData createUser(UserData data){
            users.put(data.userName(),data);
        };
        String getUsername(UserData data){
            return data.
        }

}
