package client;

import chess.ChessGame;
import dataaccess.ResponseException;
import model.GameData;
import ui.BoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PostLoginClient implements UIClient {

    private final ServerFacade server;
    private final ChessClient mainClient;
    private final String authToken;
    private List<GameData> lastListedGames = new ArrayList<>();

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

    private String listGames() {
        try {
            var games = server.listGames(mainClient.getAuthToken());
            lastListedGames = games;

            StringBuilder sb = new StringBuilder("Games:\n");
            int index = 1;
            for (var game : games) {
                sb.append(String.format("%d. %s | White: %s | Black: %s\n",
                        index++,
                        game.gameName(),
                        game.whiteUsername() == null ? "none" : game.whiteUsername(),
                        game.blackUsername() == null ? "none" : game.blackUsername()));
            }
            return sb.toString();

        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }


    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            int gameIndex = Integer.parseInt(params[0]);

            if (gameIndex < 1 || gameIndex > lastListedGames.size()) {
                throw new ResponseException(400, "Invalid game number. Try running listgames first.");
            }

            String actualGameId = String.valueOf(lastListedGames.get(gameIndex - 1).gameID());
            String color = params[1];

            server.joinGame(mainClient.getAuthToken(), actualGameId, color);
            // TODO: fetch game from server or database
            ChessGame game = new ChessGame(); // Replace with real fetch if you have it

            // Print board
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color);
            BoardPrinter.draw(game, teamColor);
            return "Joined game " + gameIndex + " as " + color;
        }
        throw new ResponseException(400, "Usage: joingame <game number> <WHITE|BLACK>");
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
