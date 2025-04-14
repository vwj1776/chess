package client;

import dataaccess.ResponseException;
import dataaccess.UserResponse;

import java.util.Arrays;

public class PreLoginClient implements UIClient {
    private final ServerFacade server;
    private final ChessClient chessClient;

    public PreLoginClient(ServerFacade server, ChessClient chessClient) {
        this.server = server;
        this.chessClient = chessClient;
    }

    @Override
    public String eval(String input) {
        var tokens = input.trim().split(" ");
        var command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (command) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "help" -> help();
            case "quit" -> "quit";
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String register(String... params) {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            try {
                UserResponse response = server.register(username, password, email);
                chessClient.setAuthToken(response.getAuthToken());
                chessClient.promoteToPostLogin();
                return String.format("Welcome, %s! You are now registered and logged in.\n%s", username, chessClient.help());
            } catch (ResponseException e) {
                return "Error: " + e.getMessage();
            } catch (Exception e) {
                return "Unexpected error: " + e.getMessage();
            }
        }
        return "Usage: register <username> <password> <email>";
    }

    private String login(String... params) {
        if (params.length == 2) {
            var username = params[0];
            var password = params[1];
            try {
                UserResponse response = server.login(username, password);
                chessClient.setAuthToken(response.getAuthToken());
                chessClient.promoteToPostLogin();
                return String.format("Welcome back, %s! You are now logged in.\n%s", username, chessClient.help());
            } catch (ResponseException e) {
                return "Error: " + e.getMessage();
            } catch (Exception e) {
                return "Unexpected error: " + e.getMessage();
            }
        }
        return "Usage: login <username> <password>";
    }

    @Override
    public String help() {
        return """
                Pre-Login Commands:
                - register <username> <password> <email>
                - login <username> <password>
                - help
                - quit
                """;
    }
}
