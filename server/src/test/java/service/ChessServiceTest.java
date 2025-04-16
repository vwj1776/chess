package service;

import static org.junit.jupiter.api.Assertions.*;

import responsesandexceptions.ResponseException;
import responsesandexceptions.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import model.*;
import java.util.Collection;

public class ChessServiceTest {
    MemoryDataAccess dataAccess;
    String authToken;
    UserData user;
    AuthData newAuthData;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
        authToken = AuthData.generateToken();
        user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        newAuthData = new AuthData(authToken, user.username());
    }

    @Test
    void addUserSuccess() throws IllegalStateException {
        dataAccess.addUser(user);
        assertEquals(dataAccess.getUser("fred"), user);
    }

    @Test
    void addUserFailure() throws IllegalStateException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertThrows(IllegalStateException.class, () -> dataAccess.addUser(user));
    }

    @Test
    void getUserSuccess() throws IllegalStateException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertEquals(dataAccess.getUser("fred"), user);
    }

    @Test
    void getUserFailure() throws IllegalStateException {
        UserData user = new UserData("fred", "fred@fred.com", "fred@fred.com");
        dataAccess.addUser(user);
        assertThrows(IllegalStateException.class, () -> dataAccess.getUser("fred2"));
    }

    @Test
    void loginSuccess() {
        dataAccess.addUser(user);
        dataAccess.login(user.username(), user.password());
        assertTrue(dataAccess.isLoggedInByUser(user));
    }

    @Test
    void loginFailure() {
        dataAccess.login(user.username(), "notTheRightPassword");
        assertThrows(IllegalStateException.class, () -> dataAccess.getUser("fred"));
    }

    @Test
    void logoutSuccess() throws Exception {
        dataAccess.addUser(user);
        UserResponse response = dataAccess.login(user.username(), user.password());
        dataAccess.logout(response.getAuthToken());
        assertFalse(dataAccess.isLoggedInByToken(response.getAuthToken()));
    }

    @Test
    void logoutFailure() {
        assertThrows(Exception.class, () -> dataAccess.logout("invalidToken"));
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        dataAccess.clear();
        dataAccess.addAuthToken(authToken, newAuthData);
        dataAccess.createGame("TestGame1", authToken);
        dataAccess.createGame("TestGame2", authToken);

        Collection<GameData> games = dataAccess.listGames(authToken);
        System.out.println("games" + games);
        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    void listGamesFailure() {
        Collection<GameData> games = dataAccess.listGames("invalidToken");
        assertNull(games);
    }

    @Test
    void clearSuccess() {
        dataAccess.addUser(user);
        dataAccess.clear();
        assertThrows(IllegalStateException.class, () -> dataAccess.getUser(user.username()));
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        dataAccess.addUser(user);
        UserResponse response = dataAccess.login(user.username(), user.password());
        String gameId = dataAccess.createGame("Chess Match", response.getAuthToken());
        assertTrue(dataAccess.joinGame(response.getAuthToken(), gameId, "WHITE"));
    }

    @Test
    void joinGameFailureInvalidAuth() {
        assertThrows(IllegalArgumentException.class, () -> dataAccess.joinGame("invalidToken", "1", "WHITE"));
    }

    @Test
    void joinGameFailureInvalidGame() {
        dataAccess.addUser(user);
        UserResponse response = dataAccess.login(user.username(), user.password());
        assertThrows(IllegalArgumentException.class, () -> dataAccess.joinGame(response.getAuthToken(), "9999", "WHITE"));
    }

    @Test
    void joinGameFailureAlreadyTaken() throws ResponseException {
        dataAccess.addUser(user);
        UserResponse response1 = dataAccess.login(user.username(), user.password());
        String gameId = dataAccess.createGame("Chess Match", response1.getAuthToken());
        dataAccess.joinGame(response1.getAuthToken(), gameId, "WHITE");

        UserData user2 = new UserData("bob", "bob@bob.com", "password");
        dataAccess.addUser(user2);
        UserResponse response2 = dataAccess.login(user2.username(), user2.password());

        assertThrows(IllegalArgumentException.class, () -> dataAccess.joinGame(response2.getAuthToken(), gameId, "WHITE"));
    }
}
