package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    public AuthData registerClient(UserData user) throws ServiceException, DataAccessException {
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new ServiceException(400, "Error: bad request");
        }
        if(userDAO.getUser(user.username()) != null) {
            throw new ServiceException(403, "Error: already taken");
        }
        userDAO.createUser(user);
        String authtoken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authtoken, user.username());
        authDAO.createAuth(auth);
        return auth;

    }

}

