package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class LoginService {
    //in order to login we need to check if the suer exists
    // then we need to create an auth token and return it
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public LoginService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData loginClient(UserData user) throws ServiceException, DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        UserData response = userDAO.getUser(user.username());

        if (response == null || !response.password().equals(user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        String authtoken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authtoken, user.username());
        authDAO.createAuth(auth);
        return auth;
        }
}
