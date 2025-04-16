package service;

import chess.ChessMove;
import dataaccess.DataAccess;
import responsesandexceptions.DataAccessException;
import responsesandexceptions.ResponseException;
import responsesandexceptions.UserResponse;
import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ChessService {
    public final DataAccess dataAccess;

    private final HashMap<String, UserData> users = new HashMap<>();
    private final Set<Integer> resignedGames = new HashSet<>();
    public static final Set<GameData> ALL_GAME_DATA = new HashSet<>();


    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ChessService() throws ResponseException, DataAccessException {
        this.dataAccess = new UserDataBaseAccess();
    }


    public UserResponse addUser(UserData user) throws ResponseException, DataAccessException {
        return dataAccess.addUser(user);
    }

    public UserData getUser(String username) throws ResponseException, DataAccessException {
        return dataAccess.getUser(username);
    }


    public UserResponse login(String username, String password) throws ResponseException, DataAccessException {
        return  dataAccess.login(username, password);
    }

    public void logout(String authToken) throws Exception {
        dataAccess.logout(authToken);
    }

    public String createGame(String gameName, String authToken) throws ResponseException, DataAccessException {
        return dataAccess.createGame(gameName, authToken);
    }

    public boolean validateAuthToken(String authToken) throws ResponseException{
        return dataAccess.validateAuthToken(authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException{
        ALL_GAME_DATA.addAll(dataAccess.listGames(authToken));
        return dataAccess.listGames(authToken);
    }

    public void clear() throws ResponseException{
        dataAccess.clear();
    }

    public boolean joinGame(String authToken, String gameID, String playerColor) throws ResponseException{
        System.out.println("in service Join game" + "playerColor" + playerColor);
        return dataAccess.joinGame(authToken, gameID, playerColor);
    }

    public ChessGame getGame(Integer gameID) throws ResponseException {
        try {
            return dataAccess.getGame(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Unable to fetch game: " + e.getMessage());
        }
    }

    public ChessGame.TeamColor getPlayerColor(String auth, GameData game) {
        try {
            String username = getUsernameFromAuth(auth);
            if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                return ChessGame.TeamColor.WHITE;
            } else if (game.blackUsername() != null && game.blackUsername().equals(username)) {
                return ChessGame.TeamColor.BLACK;
            } else {
                return null; // Observer or not a player
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void saveGame(Integer gameId, ChessGame chessGame) {
        try {
           dataAccess.saveGame(gameId, chessGame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsernameFromAuth(String auth) {
        try {
            return dataAccess.getUsernameFromAuth(auth);
        } catch (Exception e) {
            return null;
        }
    }



    public void resignGame(int gameId) {
        resignedGames.add(gameId);
    }

    public boolean isGameResigned(int gameId) {
        return resignedGames.contains(gameId);
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws ResponseException {
        // 1. Validate the auth token
        if (!validateAuthToken(authToken)) {
            throw new ResponseException(401, "Invalid auth token");
        }

        // 2. Get the game
        ChessGame game = getGame(gameId);
        if (game == null) {
            throw new ResponseException(404, "Game not found");
        }

        // 3. Check if the game is over
        if (isGameResigned(gameId) || game.getGameOver()) {
            throw new ResponseException(403, "Game is already over");
        }

        // 4. Get the user's team color
        GameData gameData = ALL_GAME_DATA.stream()
                .filter(g -> g.gameID() == gameId)
                .findFirst()
                .orElseThrow(() -> new ResponseException(404, "Game data not found"));

        ChessGame.TeamColor playerColor = getPlayerColor(authToken, gameData);
        if (playerColor == null) {
            throw new ResponseException(403, "Observers cannot make moves");
        }

        // 5. Check if it's the user's turn
        if (game.getTeamTurn() != playerColor) {
            throw new ResponseException(403, "Not your turn");
        }

        // 6. Attempt the move
        try {
            game.makeMove(move);
        } catch (Exception e) {
            throw new ResponseException(400, "Invalid move: " + e.getMessage());
        }

        // 7. Save the game back to the database
        saveGame(gameId, game);
    }

}
