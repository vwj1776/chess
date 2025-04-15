package client;

import chess.ChessGame;
import ResponsesAndExceptions.ResponseException;
import model.GameData;
import ui.BoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
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
            return formatError(e.getMessage());
        } catch (Exception e) {
            return "Error: Unexpected failure - " + formatError(e.getMessage());

        }
    }

    private String createGame(String... params) throws Exception {
        if (params.length >= 1) {
            String gameName = String.join(" ", params);
            server.createGame(gameName, authToken);
            listGames();
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
            return formatError(e.getMessage());
        }
    }


    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            if (lastListedGames.isEmpty()) {
                lastListedGames = server.listGames(mainClient.getAuthToken());
            }

            int gameIndex = Integer.parseInt(params[0]);

            if (gameIndex < 1 || gameIndex > lastListedGames.size()) {
                throw new ResponseException(400, "Invalid game number. Try running listgames first.");
            }

            String actualGameId = String.valueOf(lastListedGames.get(gameIndex - 1).gameID());
            String color = params[1].toUpperCase();

            server.joinGame(mainClient.getAuthToken(), actualGameId, color);
            // TODO: fetch game from server or database
            ChessGame game = new ChessGame();

            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color);
            BoardPrinter.draw(game, teamColor);
            return "Joined game " + gameIndex + " as " + color;
        }
        throw new ResponseException(400, "Usage: joingame <game number> <WHITE|BLACK>");
    }





    private String observeGame(String... params) throws Exception {
        if (params.length == 1) {
            if (lastListedGames.isEmpty()) {
                listGames(); // Populate the game list
            }

            int gameIndex = Integer.parseInt(params[0]);

            if (gameIndex < 1 || gameIndex > lastListedGames.size()) {
                throw new ResponseException(400, "Invalid game number. Try running listgames first.");
            }

            String actualGameId = String.valueOf(lastListedGames.get(gameIndex - 1).gameID());

            server.observeGame(actualGameId, authToken);
            // TODO: fetch game from server or database
            ChessGame game = new ChessGame();

            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf("WHITE");
            BoardPrinter.draw(game, teamColor);
            return "Observing game " + gameIndex;
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
