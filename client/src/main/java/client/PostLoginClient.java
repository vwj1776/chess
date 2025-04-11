package client;

import dataaccess.ResponseException;
import model.GameData;

import java.util.Arrays;
import java.util.Collection;

public class PostLoginClient implements UIClient {

    private final ServerFacade server;
    private final ChessClient mainClient;
    private final String authToken;

    public PostLoginClient(ServerFacade server, ChessClient mainClient, String authToken) {
        this.server = server;
        this.mainClient = mainClient;
        this.authToken = authToken;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.trim().split(" ");
            var command = tokens.length > 0 ? tokens[0].toLowerCase() : "";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (command) {
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "joingame" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command. Type 'help' for available commands.";
            };
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: Unexpected failure - " + e.getMessage();
        }
    }

    private String createGame(String... params) throws Exception {
        if (params.length >= 1) {
            String gameName = String.join(" ", params);
            server.createGame(gameName, authToken);
            return "Game created: " + gameName;
        }
        throw new ResponseException(400, "Expected: creategame <game name>");
    }

    private String listGames() throws Exception {
        Collection<GameData> games = server.listGames(authToken);
        if (games.isEmpty()) {
            return "No games found.";
        }

        StringBuilder sb = new StringBuilder("Games:\n");
        int index = 1;
        for (GameData game : games) {
            sb.append(index++)
                    .append(". ")
                    .append(game.gameName())
                    .append(" [White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "—")
                    .append(", Black: ").append(game.blackUsername() != null ? game.blackUsername() : "—")
                    .append("]\n");
        }
        return sb.toString();
    }

    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            int gameNumber = Integer.parseInt(params[0]);
            String color = params[1];
            server.joinGame(String.valueOf(gameNumber), color, authToken);
            return "Joined game " + gameNumber + " as " + color;
        }
        throw new ResponseException(400, "Expected: joingame <game number> <WHITE|BLACK>");
    }

    private String observeGame(String... params) throws Exception {
        if (params.length == 1) {
            int gameNumber = Integer.parseInt(params[0]);
            server.observeGame(String.valueOf(gameNumber), authToken);
            return "Observing game " + gameNumber;
        }
        throw new ResponseException(400, "Expected: observe <game number>");
    }

    private String logout() throws Exception {
        server.logout(authToken);
        mainClient.logout();
        return "Logged out successfully.";
    }

    @Override
    public String help() {
        return """
                Commands:
                - creategame <game name>
                - listgames
                - joingame <game number> <WHITE|BLACK>
                - observe <game number>
                - logout
                - help
                - quit
                """;
    }
}
