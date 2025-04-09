package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


import model.GameData;
import model.UserData;

import java.util.Collection;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws ResponseException, DataAccessException {
        configureDatabase();
    }


    @Override
    public UserResponse addUser(UserData user) throws dataaccess.ResponseException, DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws dataaccess.ResponseException {
        return null;
    }

    @Override
    public UserResponse login(String username, String password) throws dataaccess.ResponseException {
        return null;
    }

    @Override
    public void logout(String authToken) throws Exception {

    }

    @Override
    public String createGame(String gameName, String authToken) throws dataaccess.ResponseException, DataAccessException {
        return "";
    }

    @Override
    public boolean validateAuthToken(String authToken) throws dataaccess.ResponseException {
        return false;
    }

    @Override
    public Collection<GameData> listGames(String authToken) throws dataaccess.ResponseException {
        return List.of();
    }

    @Override
    public void clear() throws dataaccess.ResponseException {

    }

    @Override
    public boolean joinGame(String authToken, String gameID, String playerColor) throws dataaccess.ResponseException {
        return false;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS AuthData (
                authToken VARCHAR(36) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS GameData (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(255) NOT NULL,
                blackUsername VARCHAR(255) NOT NULL,
                gameName VARCHAR(255) NOT NULL,
                game BLOB NOT NULL
            );
            """,
            """
            CREATE TABLE user_data (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL
            );
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
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
