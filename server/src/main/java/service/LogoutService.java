package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;


public class LogoutService {
    private final AuthDAO authDAO;
    public LogoutService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void logoutClient(String authToken) throws ServiceException, DataAccessException {
        AuthValidation.validate(authDAO, authToken);
        authDAO.deleteAuth(authToken);

    }
}
