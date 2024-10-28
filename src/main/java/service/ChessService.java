package service;

import dataaccess.DataAccess;

import dataaccess.UserResponse;
import model.AuthData;
import model.UserData;

import java.util.HashMap;


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

    public void logout(String authToken) {
        dataAccess.logout(authToken);
    }
}
