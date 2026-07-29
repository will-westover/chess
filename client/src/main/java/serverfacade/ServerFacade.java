package serverfacade;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String baseUrl;
    public ServerFacade(int port){
        this.baseUrl = "http://localhost:" + port;
    }

    private <T> T makeRequest(String method, String path, Object body,
                              String authToken, Class<T> responseClass) throws Exception{
        URL url = new URL(baseUrl + path);
        HttpURLConnection http = (HttpURLConnection)  url.openConnection();
        http.setRequestMethod(method);

        if (authToken != null){
            http.addRequestProperty("authorization", authToken);
        }

        if(body != null){
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String reqJson = new Gson().toJson(body);
            try(OutputStream os = http.getOutputStream()){
                os.write(reqJson.getBytes());
            }
        }
        http.connect();

        if(http.getResponseCode() / 100 != 2){
            throw new Exception("failure: " + http.getResponseCode());
        }

        if (responseClass == null){
            return null;
        }

        try(InputStream is = http.getInputStream()){
            InputStreamReader reader = new InputStreamReader(is);
            return new Gson().fromJson(reader, responseClass);
        }
    }

}
