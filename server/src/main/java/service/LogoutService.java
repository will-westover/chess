package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;


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
