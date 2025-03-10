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
    private Set<GameData> allGames = new HashSet<>();

    public UserResponse addUser(UserData user) {
        if(users.containsValue(user)){
            throw new IllegalStateException("User already exists");
        }
        if(user.password() == null){
            throw new IllegalArgumentException("No password, said like no capes in edna's voice fromm the Incredibles.");
        }
        users.put(user.username(), user);
        // Generate an auth token
        String authToken = AuthData.generateToken();

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
        UserData user = users.get(username);
        System.out.println(user);
        return user;
    }

    public UserResponse login(String username, String password) {
        UserData user = users.get(username);

        if (user == null) {
            // User not found, return null or throw an exception
            return null; // This will lead to a 401 Unauthorized response in the session method
        }

        // Check if the password matches
        if (!user.password().equals(password)) {
            // Password does not match, return null or throw an exception
            return null; // This will lead to a 401
        }
        String authToken = AuthData.generateToken();
        System.out.println(authToken);
        AuthData newAuthData = new AuthData(authToken, username);

        authData.put(authToken, newAuthData);
        authTokens.put(authToken, user);
        // Return a new UserResponse containing the username and authToken
        return new UserResponse(username, authToken);
    }


    public void logout(String authToken) throws Exception {
        System.out.println(authToken);
        if(authData.containsKey(authToken)){
            authData.remove(authToken);
        } else {
            throw new Exception("Error: unauthorized");

        }


        System.out.println(authData);

    }

    @Override
    public String createGame(String gameName, String authToken) {
        System.out.println(authToken);
        int randomFourDigit = 0;
        if(validateAuthToken(authToken)){

            ChessGame newGame = new ChessGame();

            UserData user = authTokens.get(authToken);

            randomFourDigit = 1000 + new Random(System.currentTimeMillis()).nextInt(9000);
            GameData newGameData = new GameData(randomFourDigit, null, null, gameName, newGame);
            gameData.put("" + randomFourDigit, newGameData);
            allGames.add(newGameData);
            System.out.println("valid auth token" + gameData);
        } else{
            throw new IllegalStateException("Error: unauthorized");
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

//    @Override
//    public GameData[] listGames(String authToken) {
//        if (validateAuthToken(authToken)) {
//            return gameData.values().toArray(new GameData[0]); // Convert values to GameData array
//        }
//        return null;
//    }


    @Override
    public void clear(){
        users.clear();
        authData.clear();
        gameData.clear();
        authTokens.clear();
        allGames.clear();
        System.out.println("----------------------clearing hopefully");
        System.out.println(authTokens);
        System.out.println(gameData);
        System.out.println(authData);
        System.out.println(users);

    }

    @Override
    public boolean joinGame(String authToken, String gameID, String playerColor) throws IllegalArgumentException {



        System.out.println("playerColor" + playerColor);

        if (!validateAuthToken(authToken)) {
            throw new IllegalArgumentException("unauthorized");
        }


        System.out.println("game is " + gameData.get(gameID));
        int intGameId = Integer.parseInt(gameID);
        GameData game = gameData.get(gameID); // should this be gameID????

        if (game == null || !(gameData.get(gameID).gameID() == intGameId)) {
            throw new IllegalArgumentException("bad request");
        }

//        if (playerColor.equalsIgnoreCase("black") || playerColor.equalsIgnoreCase("white") ) {
//            throw new IllegalArgumentException("bad request, invalid player color");
//        }


        if ("WHITE".equalsIgnoreCase(playerColor) && game.whiteUsername() != null ||
                "BLACK".equalsIgnoreCase(playerColor) && game.blackUsername() != null) {
            throw new IllegalArgumentException("already taken");
        }

        UserData user = authTokens.get(authToken);
        System.out.println("user" + user);
        // System.out.println("authTokens" + authTokens);


        if (user == null) {
            throw new IllegalArgumentException("unauthorized");
        }

        GameData updatedGame;
        if(playerColor.equalsIgnoreCase("WHITE")){
            updatedGame = new GameData(intGameId, user.username(), game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(intGameId, game.whiteUsername(), user.username(), game.gameName(), game.game());
        }
        gameData.put(gameID, updatedGame); // Update the game data
        allGames.add(updatedGame);
        System.out.println("gameData   " + gameData);

        return true; // Successful join
    }
    @Override
    public Collection<GameData> listGames(String authToken) {
        if(validateAuthToken(authToken)){
            return gameData.values();
        }
        return null;
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
