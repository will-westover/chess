package serverfacade;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private final String baseUrl;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    private <T> T makeRequest(String method, String path, Object body,
                              String authToken, Class<T> responseClass) throws Exception {
        URL url = new URL(baseUrl + path);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);

        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }

        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String reqJson = new Gson().toJson(body);
            try (OutputStream os = http.getOutputStream()) {
                os.write(reqJson.getBytes());
            }
        }
        http.connect();

        if (http.getResponseCode() / 100 != 2) {
            throw new Exception("failure: " + http.getResponseCode());
        }

        if (responseClass == null) {
            return null;
        }

        try (InputStream is = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(is);
            return new Gson().fromJson(reader, responseClass);
        }
    }

    public AuthResponse register(String username, String email, String password) throws Exception {
        var request = new RegisterRequest(username, email, password);
        return makeRequest("POST", "/user", request, null, AuthResponse.class);
    }

    public AuthResponse login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", request, null, AuthResponse.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    public CreateGameResponse createGame(String name, String authToken) throws Exception {
        var request = new CreateGameRequest(name);
        return makeRequest("POST", "/game", request, authToken, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, authToken, ListGamesResponse.class);
    }

    public void joinGame(String color, int gameID, String authToken) throws Exception {
        makeRequest("PUT", "/game", null, authToken, JoinGameRequest.class);
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }


}
