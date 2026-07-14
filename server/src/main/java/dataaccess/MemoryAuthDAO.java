package dataaccess;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, UserData> users = new HashMap<>();
    private int nextId =1;
    UserData createUser(UserData data){
        users.add(data);
    }
    String getUsername(UserData data){
        return data.
    }
}
