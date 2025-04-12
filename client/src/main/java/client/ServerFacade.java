package client;

import com.google.gson.Gson;
import dataaccess.GameListResponse;
import dataaccess.GameResponse;
import dataaccess.ResponseException;
import model.UserData;
import model.GameData;
import model.AuthData;
import dataaccess.UserResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public UserResponse register(String username, String password, String email) throws Exception {
        var url = new URL(serverUrl + "/user");
        var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        var requestBody = gson.toJson(new UserData(username, password, email));
        try (var outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
        }

        var status = connection.getResponseCode();
        if (status == 200) {
            try (var input = connection.getInputStream()) {
                var responseBody = new String(input.readAllBytes());
                return gson.fromJson(responseBody, UserResponse.class);
            }
        } else {
            try (var error = connection.getErrorStream()) {
                var errorMessage = new String(error.readAllBytes());
                throw new RuntimeException("Error: " + errorMessage);
            }
        }
    }


    public UserResponse login(String username, String password) throws ResponseException {
        var path = "/session";
        var request = new UserData(username, password, null);
        return makeRequest("POST", "/session", request, null, UserResponse.class);
    }


    @SuppressWarnings("unchecked")
    protected <T> T makeRequest(String method, String path, Object request, Map<String, String> headers, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");

            if (headers != null) {
                for (var entry : headers.entrySet()) {
                    http.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (request != null) {
                try (var body = http.getOutputStream()) {
                    String json = gson.toJson(request);
                    body.write(json.getBytes());
                }
            }

            int status = http.getResponseCode();
            if (status == 200) {
                if (responseClass == null || responseClass == Void.class) {
                    return null;
                }
                try (var reader = new InputStreamReader(http.getInputStream())) {
                    return gson.fromJson(reader, responseClass);
                }
            } else {
                try (var reader = new InputStreamReader(http.getErrorStream())) {
                    var error = gson.fromJson(reader, ErrorResponse.class);
                    throw new ResponseException(status, error.message);
                }
            }
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }




    private static class ErrorResponse {
        public String message;
    }

    public void logout(String authToken) throws Exception {
        var url = new URL(serverUrl + "/session");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("DELETE");
        http.setRequestProperty("Authorization", authToken);

        var status = http.getResponseCode();
        if (status != 200) {
            throw new ResponseException(status, readResponseBody(http));
        }
    }

    public String createGame(String gameName, String authToken) throws ResponseException {
        var request = Map.of("gameName", gameName);
        return makeRequest("POST", "/game", request, Map.of("Authorization", authToken), GameResponse.class).getGameID();
    }


    public List<GameData> listGames(String authToken) throws ResponseException {
        var headers = Map.of("Authorization", authToken);
        GameListResponse response = this.makeRequest("GET", "/game", null, headers, GameListResponse.class);
        return response.getGames();
    }


    public void joinGame(String authToken, String gameId, String playerColor) throws Exception {
        var request = Map.of(
                "gameID", gameId,
                "playerColor", playerColor
        );
        var headers = Map.of("Authorization", authToken);
        makeRequest("PUT", "/game", request, headers, null);
    }


    public void observeGame(String authToken, String gameId) throws Exception {
        var request = Map.of(
                "gameID", gameId,
                "playerColor", "observer"
        );
        var headers = Map.of("Authorization", authToken);
        makeRequest("PUT", "/game", request, headers, Void.class);
    }



    public void clear() throws ResponseException, IOException {
        var url = new URL(serverUrl + "/db");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("DELETE");

        var status = http.getResponseCode();
        if (status != 200) {
            throw new ResponseException(status, readResponseBody(http));
        }
    }

    private String readResponseBody(HttpURLConnection http) throws IOException {
        var inputStream = http.getErrorStream();
        if (inputStream == null) {
            inputStream = http.getInputStream();
        }

        try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            var response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }


}
