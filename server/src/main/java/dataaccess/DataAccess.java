package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public interface DataAccess {
    UserResponse addUser(UserData user);

    UserData getUser(String username);

    UserResponse login(String username, String password);

    void logout(String authToken) throws Exception;

    String createGame(String gameName, String authToken);

    boolean validateAuthToken(String authToken);

    // GameData[] listGames(String authToken);

    Collection<GameData> listGames(String authToken);


    void clear();

    boolean joinGame(String authToken, String gameID, String playerColor);
//    Collection<Pet> listPets() throws ResponseException;
//
//    Pet getPet(int id) throws ResponseException;
//
//    void deletePet(Integer id) throws ResponseException;
//
//    void deleteAllPets() throws ResponseException;
}
