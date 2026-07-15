package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;


public class LogoutService {
    private final AuthDAO authDAO;
    public LogoutService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void logoutClient(String authToken) throws ServiceException, DataAccessException {
        AuthValidtion.validate(authDAO, authToken);
        authDAO.deleteAuth(authToken);

    }
}
