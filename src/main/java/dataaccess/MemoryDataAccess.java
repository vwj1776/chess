package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authData = new HashMap<>();
    final private HashMap<String, GameData> gameData = new HashMap<>();
    private final HashMap<String, UserData> authTokens = new HashMap<>(); // New map for auth tokens


    public UserResponse addUser(UserData user) {
        users.put(user.username(), user);
        // Generate an auth token
        String authToken = AuthData.generateToken(); // Assuming this method generates a new token

        // Create a response object
        UserResponse response = new UserResponse(user.username(), authToken);
        // Print the current state of users
        System.out.println(users);
        authTokens.put(authToken, user); // Link authToken to user
        AuthData newAuthData = new AuthData(authToken, user.username());

        authData.put(authToken, newAuthData);
        // Return the response object as JSON
        return response;
    }

    public UserData getUser(String username) {
        System.out.println("in memory");

        // Retrieve the user from the map
        UserData user = users.get(username);
        System.out.println(user);

        // Return user, can be null if not found
        return user;
    }

    public UserResponse login(String username, String password) {
        // Check if the user exists
        UserData user = users.get(username);

        if (user == null) {
            // User not found, return null or throw an exception
            return null; // This will lead to a 401 Unauthorized response in the session method
        }

        // Check if the password matches
        if (!user.password().equals(password)) {
            // Password does not match, return null or throw an exception
            return null; // This will lead to a 401 Unauthorized response in the session method
        }

        // If authentication is successful, generate a new auth token
        String authToken = AuthData.generateToken();
        System.out.println(authToken);
        AuthData newAuthData = new AuthData(authToken, username);

        authData.put(authToken, newAuthData);

        // Return a new UserResponse containing the username and authToken
        return new UserResponse(username, authToken);
    }


    public void logout(String authToken){
        System.out.println(authToken);

        authData.remove(authToken);
        System.out.println(authData);

    }

    @Override
    public String createGame(String gameName, String authToken) {
        System.out.println(authToken);
        int randomFourDigit = 0;
        if(validateAuthToken(authToken)){

            ChessGame newGame = new ChessGame();


            randomFourDigit = 1000 + new Random(System.currentTimeMillis()).nextInt(9000);
            GameData newGameData = new GameData(randomFourDigit, "username", null, gameName, newGame);
            gameData.put(authToken, newGameData);
            System.out.println("valid auth token" + gameData);
        }


        return "" + randomFourDigit;
    }

    public boolean validateAuthToken(String authToken){
        return authData.containsKey(authToken);
    }

//    @Override
//    public GameData listGames(String authToken) {
//        if(validateAuthToken(authToken)){
//            return gameData.get(authToken); // TODO: return all games???
//        }
//        return null;
//    }

    @Override
    public GameData[] listGames(String authToken) {
        if (validateAuthToken(authToken)) {
            return gameData.values().toArray(new GameData[0]); // Convert values to GameData array
        }
        return null;
    }


    @Override
    public void clear(){
        users.clear();
        authData.clear();
        gameData.clear();
        authTokens.clear();
    }

    @Override
    public boolean joinGame(String authToken, String gameID, String playerColor) throws IllegalArgumentException {
//        System.out.println("in service Join game");
       //  System.out.println("authtokenValid?" + validateAuthToken(authToken));
        System.out.println("authToken" + authToken);
//        System.out.println("gameID" + gameID);
//        System.out.println("playerColor" + playerColor);

        if (!validateAuthToken(authToken)) {
            throw new IllegalArgumentException("unauthorized");
        }


        // System.out.println(gameData.get(gameID));

        GameData game = gameData.get(authToken); // should this be gameID????
        System.out.println(gameData);
        if (game == null) {
            throw new IllegalArgumentException("bad request");
        }

        // Check if color is already taken
        if ("WHITE".equals(playerColor) && game.whiteUsername() != null ||
                "BLACK".equals(playerColor) && game.blackUsername() != null) {
            throw new IllegalArgumentException("already taken");
        }

        // Get the username from the auth token
        UserData user = authTokens.get(authToken);
        System.out.println("user" + user);
        System.out.println("authTokens" + authTokens);


        if (user == null) {
            throw new IllegalArgumentException("unauthorized");
        }

        // Update the game record with the player's username for the specified color
        GameData updatedGame = "WHITE".equals(playerColor)
                ? new GameData(game.gameID(), user.username(), game.blackUsername(), game.gameName(), game.game())
                : new GameData(game.gameID(), game.whiteUsername(), user.username(), game.gameName(), game.game());

        gameData.put(gameID, updatedGame); // Update the game data

        return true; // Successful join
    }


//    @Override
//    public GameData[] listGames(String authToken) {
//        UserData[] usersByAuthToken = getUsersByAuthToken(authToken);
//        System.out.println(Arrays.toString(usersByAuthToken) + "in memory");
//
//        if (usersByAuthToken.length == 0) {
//            return new GameData[0];
//        }
//
//        String username = usersByAuthToken[0].username();
//
//        return gameData.values().stream()
//                .filter(game -> username.equals(game.whiteUsername()) || username.equals(game.blackUsername()))
//                .toArray(GameData[]::new);
//    }


//    public UserData[] getUsersByAuthToken(String authToken) {
//        UserData user = authTokens.get(authToken);
//        System.out.println(user + "user");
//        System.out.println(authToken + "authToken");
//
//        if (user != null) {
//            return new UserData[]{user};
//        } else {
//            return new UserData[0];
//        }
//    }

    //    public Collection<Pet> listPets() {
//        return pets.values();
//    }
//
//
//    public Pet getPet(int id) {
//        return pets.get(id);
//    }
//
//    public void deletePet(Integer id) {
//        pets.remove(id);
//    }
//
//    public void deleteAllPets() {
//        pets.clear();
//    }
}
