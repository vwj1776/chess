package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws ResponseException, DataAccessException {
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
          `whiteUsername` VARCHAR(255) NOT NULL,
          `blackUsername` VARCHAR(255) NOT NULL,
          `gameName` VARCHAR(255) NOT NULL,
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
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public UserResponse addUser(UserData user) throws ResponseException {
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        return executeAddUser(statement, user.username(), user.password(), user.email());
    }


    private UserResponse executeAddUser(String statement, String username, String password, String email) throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            String authToken = AuthData.generateToken(); //todo:figure out authtoken

            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    var userId = rs.getInt(1); // where the heck do we get the authtoken from here?
                    return new UserResponse(username, String.valueOf(authToken));
                }
            }

            throw new ResponseException(500, "User could not be added. No ID was generated.");
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add user: %s", e.getMessage()));
        }
    }



    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public UserResponse login(String username, String password) {
        return null;
    }

    @Override
    public void logout(String authToken) throws Exception {

    }

    @Override
    public String createGame(String gameName, String authToken) {
        return "";
    }

    @Override
    public boolean validateAuthToken(String authToken) {
        return false;
    }

    @Override
    public Collection<GameData> listGames(String authToken) {
        return List.of();
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean joinGame(String authToken, String gameID, String playerColor) {
        return false;
    }
}
