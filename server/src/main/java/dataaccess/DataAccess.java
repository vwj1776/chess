package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    UserResponse addUser(UserData user) throws ResponseException, DataAccessException;

    UserData getUser(String username) throws ResponseException;

    UserResponse login(String username, String password) throws ResponseException;

    void logout(String authToken) throws Exception;

    String createGame(String gameName, String authToken) throws ResponseException, DataAccessException;

    boolean validateAuthToken(String authToken) throws ResponseException;

    // GameData[] listGames(String authToken);

    Collection<GameData> listGames(String authToken) throws ResponseException;


    void clear() throws ResponseException;

    boolean joinGame(String authToken, String gameID, String playerColor) throws ResponseException;
//    Collection<Pet> listPets() throws ResponseException;
//
//    Pet getPet(int id) throws ResponseException;
//
//    void deletePet(Integer id) throws ResponseException;
//
//    void deleteAllPets() throws ResponseException;
}
