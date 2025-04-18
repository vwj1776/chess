package dataaccess;

import responsesandexceptions.DataAccessException;
import responsesandexceptions.ResponseException;
import responsesandexceptions.UserResponse;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JUnitTests {

    private static UserDataBaseAccess dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new UserDataBaseAccess();
        dao.clear();
    }

    @Test
    void addUserPositive() {
        UserData user = new UserData("alice", "password123", "alice@mail.com");
        assertDoesNotThrow(() -> dao.addUser(user));
    }

    @Test
    void addUserNegativeDuplicateUsername() throws Exception {
        UserData user1 = new UserData("bob", "pass", "bob@mail.com");
        dao.addUser(user1);
        UserData user2 = new UserData("bob", "diffpass", "bob2@mail.com");
        assertThrows(ResponseException.class, () -> dao.addUser(user2));
    }

    @Test
    void getUserPositive() throws Exception {
        UserData user = new UserData("carol", "secret", "carol@mail.com");
        dao.addUser(user);
        UserData result = dao.getUser("carol");
        assertNotNull(result);
        assertEquals("carol", result.username());
    }

    @Test
    void getUserNegativeNotFound() throws Exception {
        UserData result = dao.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    void createGamePositive() throws Exception {
        UserData user = new UserData("dave", "123", "dave@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Fun Game", token);
        assertNotNull(gameId);
    }

    @Test
    void createGameNegativeInvalidToken() {
        assertThrows(ResponseException.class, () -> dao.createGame("Bad Game", "fake-token"));
    }

    @Test
    void joinGamePositive() throws Exception {
        UserData user = new UserData("ellen", "pw", "ellen@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Join Game", token);
        boolean success = dao.joinGame(token, gameId, "WHITE");
        assertTrue(success);
    }

    @Test
    void joinGameNegativeInvalidColor() throws Exception {
        UserData user = new UserData("frank", "pw", "frank@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Oops Game", token);
        assertThrows(IllegalArgumentException.class, () -> dao.joinGame(token, gameId, "PURPLE"));
    }

    @Test
    void validateAuthTokenPositive() throws Exception {
        UserData user = new UserData("gina", "pw", "gina@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        assertTrue(dao.validateAuthToken(token));
    }

    @Test
    void validateAuthTokenNegative() {
        assertFalse(dao.validateAuthToken("invalid-token"));
    }

    @Test
    void listGamesPositive() throws Exception {
        UserData user = new UserData("harry", "pw", "harry@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        dao.createGame("G1", token);
        dao.createGame("G2", token);
        Collection<GameData> games = dao.listGames(token);
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegativeInvalidToken() {
        assertThrows(ResponseException.class, () -> dao.listGames("fake-token"));
    }

    @Test
    void addAuthTokenPositive() throws Exception {
        UserData user = new UserData("authme", "pw", "authme@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void addAuthTokenNegativeUserDoesNotExist() {
        UserData fakeUser = new UserData("ghost", "pw", "ghost@mail.com");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> dao.addAuthToken(fakeUser));
        assertTrue(exception.getMessage().contains("User does not exist"));
    }

    @Test
    void loginPositive() throws Exception {
        UserData user = new UserData("testUser", "securePassword", "test@mail.com");
        dao.addUser(user);

        UserResponse response = dao.login("testUser", "securePassword");

        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertNotNull(response.getAuthToken());
    }

    @Test
    void loginNegativeWrongPassword() throws Exception {
        UserData user = new UserData("badUser", "rightPassword", "bad@mail.com");
        dao.addUser(user);

        assertThrows(ResponseException.class, () -> dao.login("badUser", "wrongPassword"));
    }

    @Test
    void logoutPositive() throws Exception {
        UserData user = new UserData("logoutUser", "pw123", "logout@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);

        assertDoesNotThrow(() -> dao.logout(token));
    }

    @Test
    void logoutNegativeInvalidToken() {
        String fakeToken = "nonexistent-token";
        ResponseException exception = assertThrows(ResponseException.class, () -> dao.logout(fakeToken));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void clearPositive() throws Exception {
        UserData user = new UserData("ivy", "pw", "ivy@mail.com");
        dao.addUser(user);
        dao.clear();
        assertNull(dao.getUser("ivy"));
    }
}
