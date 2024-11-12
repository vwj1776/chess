package service;

import dataaccess.DataAccess;

import dataaccess.UserResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;


public class ChessService {
    private final DataAccess dataAccess;

    private final HashMap<String, UserData> users = new HashMap<>();


    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Pet Shop is very simple.
    // A more complicated application would do the business logic in this
    // service.

    public UserResponse addUser(UserData user) {
        return dataAccess.addUser(user);
    }

    public UserData getUser(String username) {
        return dataAccess.getUser(username);
    }


    public UserResponse login(String username, String password) {
        return  dataAccess.login(username, password);
    }

    public void logout(String authToken) throws Exception {
        dataAccess.logout(authToken);
    }

    public String createGame(String gameName, String authToken){
        return dataAccess.createGame(gameName, authToken);
    }

    public boolean validateAuthToken(String authToken){
        return dataAccess.validateAuthToken(authToken);
    }

    public Collection<GameData> listGames(String authToken){
        return dataAccess.listGames(authToken);
    }

    public void clear(){
        dataAccess.clear();
    }

    public boolean joinGame(String authToken, String gameID, String playerColor){
        System.out.println("in service Join game");
        return dataAccess.joinGame(authToken, gameID, playerColor);
    }
}
