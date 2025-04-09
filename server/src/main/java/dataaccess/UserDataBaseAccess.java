package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class UserDataBaseAccess implements DataAccess {

    public UserDataBaseAccess() throws ResponseException, DataAccessException {
        System.out.println("in userDatabase");
        configureDatabase();
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS AuthData (
          `authToken` VARCHAR(36) PRIMARY KEY,
          `username` VARCHAR(255) NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """,
        """
        CREATE TABLE IF NOT EXISTS GameData (
          `gameID` INT PRIMARY KEY AUTO_INCREMENT,
          `whiteUsername` VARCHAR(255),
          `blackUsername` VARCHAR(255),
          `gameName` VARCHAR(255),
          `game` BLOB NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """,
        """
        CREATE TABLE IF NOT EXISTS UserData (
          `username` VARCHAR(255) PRIMARY KEY,
          `password` VARCHAR(255) NOT NULL,
          `email` VARCHAR(255) UNIQUE NOT NULL
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };


    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager01.createDatabase();
        try (var conn = DatabaseManager01.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }



//    public AuthData getAuthToken(){
//
//    }

    public String addAuthToken(UserData user) throws DataAccessException {
        String authToken = AuthData.generateToken();
        var statement = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.setString(2, user.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to add auth token: " + e.getMessage());
        }

        return authToken;
    }


    @Override
    public UserResponse addUser(UserData user) throws ResponseException, DataAccessException {
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        return executeAddUser(statement, user.username(), user.password(), user.email());
    }

    public void createUser(UserData user) throws ResponseException, DataAccessException {
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        executeAddUser(statement, user.username(), user.password(), user.email());
    }

    private UserResponse executeAddUser(String statement, String username, String password, String email) throws ResponseException, DataAccessException {
        if(getUser(username) != null){
            throw new DataAccessException("Username already in database:" + username);
        }
        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);

            ps.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add user: %s", e.getMessage()));
        }
        return null;
    }



    @Override
    public UserData getUser(String username) throws ResponseException {
        var statement = "SELECT username, password, email FROM UserData WHERE username = ?";

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    var retrievedUsername = rs.getString("username");
                    var password = rs.getString("password");
                    var email = rs.getString("email");
                    var user = new UserData(retrievedUsername, password, email);
                    System.out.println(user);
                    return new UserData(retrievedUsername, password, email);
                }
            }
            return null;

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to find user: %s", e.getMessage()));
        }
    }

//    @Override
//    public UserResponse login(String username, String password) throws ResponseException {
//        UserData user = getUser(username);
//        UserResponse response = null;
//
//
//        if(Objects.equals(user.password(), password)){
//            response = new UserResponse(username, createAuthtoken());
//        }
//        return response;
//    }

    @Override
    public UserResponse login(String username, String password) throws ResponseException {
        UserData user = getUser(username);

        if (user != null && Objects.equals(user.password(), password)) {
            try {
                String token = addAuthToken(user);
                return new UserResponse(username, token);
            } catch (DataAccessException e) {
                throw new ResponseException(500, "Unable to generate auth token");
            }
        }

        throw new ResponseException(401, "Invalid login");
    }


    public String createAuthtoken(){
        return AuthData.generateToken();
    }

    @Override
    public void logout(String authToken) throws Exception {

    }



    public void addGame(GameData gameData) throws ResponseException {
        var statement = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            ps.setInt(1, gameData.gameID());
            ps.setString(2, gameData.whiteUsername());
            ps.setString(3, gameData.blackUsername());
            ps.setString(4, gameData.gameName());
            ps.setString(5, new Gson().toJson(gameData.game()));


            ps.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add Game: %s", e.getMessage()));
        }
    }

    private void executeCreatGame(String statement, String gameName, String authToken) throws ResponseException, DataAccessException {

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);

            ps.executeUpdate();
            if(!validateAuthToken(authToken)){
                throw new ResponseException(500, "invalid authtoken: %s");
            }

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add Game: %s", e.getMessage()));
        }
    }

    @Override
    public String createGame(String gameName, String authToken) throws ResponseException, DataAccessException {
        var statement = "INSERT INTO GameData (gameName, game) VALUES (?, ?)";

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            ps.setString(1, gameName);
            ps.setString(2, new Gson().toJson(new ChessGame())); // Create a default game state

            ps.executeUpdate();

            if (!validateAuthToken(authToken)) {
                throw new ResponseException(401, "invalid authtoken");
            }

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getInt(1)); // return the generated gameID
                }
            }

            throw new ResponseException(500, "Failed to create game");

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add Game: %s", e.getMessage()));
        }
    }


    @Override
    public boolean validateAuthToken(String authToken) {
        var sql = "SELECT authToken FROM AuthData WHERE authToken = ?";

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);

            try (var rs = ps.executeQuery()) {
                return rs.next(); // true if found
            }

        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public Collection<GameData> listGames(String authToken) throws ResponseException {
        validateAuthToken(authToken);

        var games = new ArrayList<GameData>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData";

        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                ChessGame game = deJsonGame(rs.getString("game")); // todo: get it from json?


                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to retrieve games: %s", e.getMessage()));
        }

        return games;
    }

    private ChessGame deJsonGame(String gameData) {
        return new Gson().fromJson(gameData, ChessGame.class);
    }



    @Override
    public void clear() {

    }

    @Override
    public boolean joinGame(String authToken, String gameID, String playerColor) {
        try (var conn = DatabaseManager01.getConnection()) {
            String column = switch (playerColor.toUpperCase()) {
                case "WHITE" -> "whiteUsername";
                case "BLACK" -> "blackUsername";
                default -> throw new IllegalArgumentException("Invalid player color: " + playerColor);
            };

            String statement = "UPDATE GameData SET " + column + " = ? WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, getUsernameFromAuth(authToken)); // Get from your auth table
                ps.setInt(2, Integer.parseInt(gameID));
                ps.executeUpdate();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getUsernameFromAuth(String token) throws SQLException, DataAccessException {
        var sql = "SELECT username FROM AuthData WHERE authToken = ?";
        try (var conn = DatabaseManager01.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("username");
                throw new DataAccessException("Invalid auth token");
            }
        }
    }



}
