package dataaccess;

import ResponsesAndExceptions.DataAccessException;
import ResponsesAndExceptions.ResponseException;
import ResponsesAndExceptions.UserResponse;
import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    UserResponse addUser(UserData user) throws ResponseException, DataAccessException;

    UserData getUser(String username) throws ResponseException, DataAccessException;

    UserResponse login(String username, String password) throws ResponseException, DataAccessException;

    void logout(String authToken) throws Exception;

    String createGame(String gameName, String authToken) throws ResponseException, DataAccessException;

    boolean validateAuthToken(String authToken) throws ResponseException;

    Collection<GameData> listGames(String authToken) throws ResponseException;


    void clear() throws ResponseException;

    boolean joinGame(String authToken, String gameID, String playerColor) throws ResponseException;

    ChessGame getGame(Integer gameID) throws DataAccessException;
}
