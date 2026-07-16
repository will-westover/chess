package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;

public class AuthValidtion {
    public static AuthData validate(AuthDAO authDAO, String authToken) throws ServiceException, DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new ServiceException(401, "Error: unauthorized");
        }
        return auth;
    }
}
