package service;

public class ServiceException extends Exception {
    private final int status;

    public ServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
