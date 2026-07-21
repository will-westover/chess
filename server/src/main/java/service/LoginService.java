package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData loginClient(UserData user) throws ServiceException, DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        UserData response = userDAO.getUser(user.username());

        if (response == null || !BCrypt.checkpw(user.password(), response.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        String authtoken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authtoken, user.username());
        authDAO.createAuth(auth);
        return auth;
    }
}
