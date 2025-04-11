package client;

import com.google.gson.Gson;
import model.UserData;
import model.GameData;
import model.AuthData;
import dataaccess.UserResponse;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

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


    public UserResponse login(String username, String password) throws Exception {
        // TODO: Implement HTTP POST to /session
        return null;
    }

    public void logout(String authToken) throws Exception {
        // TODO: Implement HTTP DELETE to /session
    }

    public String createGame(String gameName, String authToken) throws Exception {
        // TODO: Implement HTTP POST to /game
        return null;
    }

    public List<GameData> listGames(String authToken) throws Exception {
        // TODO: Implement HTTP GET to /game
        return null;
    }

    public void joinGame(String authToken, String gameId, String playerColor) throws Exception {
        // TODO: Implement HTTP PUT to /game
    }

    public void observeGame(String authToken, String gameId) throws Exception {
        // TODO: Implement observe logic, same as joinGame but no color
    }

    public void clear() throws Exception {
        // TODO: Implement HTTP DELETE to /db
    }
}
