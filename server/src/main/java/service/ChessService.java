package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import dataaccess.UserResponse;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;


public class ChessService {
    private final DataAccess dataAccess;

    private final HashMap<String, UserData> users = new HashMap<>();


    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserResponse addUser(UserData user) throws ResponseException, DataAccessException {
        return dataAccess.addUser(user);
    }

    public UserData getUser(String username) throws ResponseException {
        return dataAccess.getUser(username);
    }


    public UserResponse login(String username, String password) throws ResponseException {
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
        return dataAccess.listGames(authToken);
    }

    public void clear() throws ResponseException{
        dataAccess.clear();
    }

    public boolean joinGame(String authToken, String gameID, String playerColor) throws ResponseException{
        System.out.println("in service Join game" + "playerColor" + playerColor);
        return dataAccess.joinGame(authToken, gameID, playerColor);
    }
}
