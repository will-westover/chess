package server;

import com.google.gson.Gson;
import dataAccess.*;
import io.javalin.*;
import model.AuthData;
import model.UserData;
import service.*;
import io.javalin.http.*;


public class Server {
    private final Javalin javalin;
    ClearService clearService;
    CreateGameService createGameService;
    JoinGameService joinGameService;
    LoginService loginService;
    LogoutService logoutService;
    RegisterService registerService;
    ListGamesService listGamesService;
    Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        registerService = new RegisterService(userDAO, authDAO);
        listGamesService = new ListGamesService(authDAO, gameDAO);


        javalin.delete("/db", ctx -> clearHandler(ctx));
        javalin.post("/user", ctx-> registerHandler(ctx));
        javalin.post("/session", ctx-> loginHandler(ctx));
        javalin.delete("/session", ctx -> logoutHandler(ctx));
        javalin.get("/game", ctx -> listHandler(ctx));
        javalin.post("/game", ctx-> createHandler(ctx));
        javalin.put("/game", ctx -> joinHandler(ctx));
        javalin.exception(ServiceException.class, (exception, ctx) ->{
            ctx.status(exception.getStatus());
            ctx.result(gson.toJson(new ErrorResult(exception.getMessage())));});
        javalin.exception(Exception.class,((exception, ctx) -> {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResult("Error: " + exception.getMessage())));}));


    }

    private void clearHandler(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.result("{}");
    }
    private void registerHandler(Context ctx) throws DataAccessException{
        UserData user = gson.fromJson(ctx.body(), UserData.class);
        AuthData auth = registerService.registerClient(user);
        ctx.result(gson.toJson(auth));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}