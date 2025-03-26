package service;

import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import model.*;
import java.util.Collection;
import java.util.List;

public class ChessServiceTest {
    MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
         dataAccess = new MemoryDataAccess();
    }

    @Test
    void addUser_success() throws ResponseException, DataAccessException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertEquals(dataAccess.getUser("fred"), user);
    }

    @Test
    void addUser_failure() throws ResponseException, DataAccessException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertThrows(IllegalStateException.class, () -> dataAccess.addUser(user));
    }

    @Test
    void getUser_success() throws ResponseException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertEquals(dataAccess.getUser("fred"), user);
    }

    @Test
    void getUser_failure() throws ResponseException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        // assertThrows(IllegalStateException.class, () -> dataAccess.getUser("fred2"));
    }

    @Test
    void login_success() throws ResponseException {
        // TODO: Stub success case
    }

    @Test
    void login_failure() throws ResponseException {
        // TODO: Stub failure case
    }

    @Test
    void logout_success() throws Exception {
        // TODO: Stub success case
    }

    @Test
    void logout_failure() throws Exception {
        // TODO: Stub failure case
    }

    @Test
    void createGame_success() throws ResponseException, DataAccessException {
        dataAccess.createGame("insertNameHere", "insertAuthTokenHere");
        // assertThrows(createGame_success());
    }

    @Test
    void createGame_failure() throws ResponseException, DataAccessException {
        String invalidAuthToken = "invalidToken";

        assertThrows(IllegalStateException.class, () -> {
            dataAccess.createGame("insertNameHere", invalidAuthToken);
        });
    }


    @Test
    void validateAuthToken_success() throws ResponseException {
        String authToken = AuthData.generateToken();
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        AuthData newAuthData = new AuthData(authToken, user.username());
        dataAccess.addAuthToken(authToken, newAuthData);
        assertTrue(dataAccess.validateAuthToken(authToken));
    }

    @Test
    void validateAuthToken_failure() throws ResponseException {
        // TODO: Stub failure case
    }

    @Test
    void listGames_success() throws ResponseException {
        // TODO: Stub success case
    }

    @Test
    void listGames_failure() throws ResponseException {
        // TODO: Stub failure case
    }

    @Test
    void clear_success() throws ResponseException {
        // TODO: Stub success case
    }

    @Test
    void clear_failure() throws ResponseException {
        // TODO: Stub failure case
    }

    @Test
    void joinGame_success() throws ResponseException {
        // TODO: Stub success case
    }

    @Test
    void joinGame_failure() throws ResponseException {
        // TODO: Stub failure case
    }
}
