package client;

import dataaccess.ResponseException;
import dataaccess.UserResponse;
import model.AuthData;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private String authToken = null;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.trim().split(" ");
            var command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (command) {
                case "register" -> register(params);
                case "help" -> help();
                case "quit" -> "Goodbye!";
                default -> "Unknown command. Type 'help' for options.";
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String register(String... params) throws Exception {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            UserResponse response = server.register(username, password, email);
            authToken = response.getAuthToken();
            return String.format("Welcome, %s! You are now registered and logged in.", username);
        }
        throw new ResponseException(400, "Expected: register <username> <password> <email>");
    }

    String help() {
        return """
                Commands:
                - register <username> <password> <email>
                - help
                - quit
                """;
    }
}
