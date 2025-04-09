package dataaccess;

import model.UserData;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class juinitDBtests {

    private static UserDataBaseAccess dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new UserDataBaseAccess();
        dao.clear();
    }

    @Test
    void addUser_positive() throws Exception {
        UserData user = new UserData("alice", "password123", "alice@mail.com");
        assertDoesNotThrow(() -> dao.addUser(user));
    }

    @Test
    void addUser_negative_duplicateUsername() throws Exception {
        UserData user1 = new UserData("bob", "pass", "bob@mail.com");
        dao.addUser(user1);
        UserData user2 = new UserData("bob", "diffpass", "bob2@mail.com");
        assertThrows(ResponseException.class, () -> dao.addUser(user2));
    }

    @Test
    void getUser_positive() throws Exception {
        UserData user = new UserData("carol", "secret", "carol@mail.com");
        dao.addUser(user);
        UserData result = dao.getUser("carol");
        assertNotNull(result);
        assertEquals("carol", result.username());
    }

    @Test
    void getUser_negative_notFound() throws Exception {
        UserData result = dao.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    void createGame_positive() throws Exception {
        UserData user = new UserData("dave", "123", "dave@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Fun Game", token);
        assertNotNull(gameId);
    }

    @Test
    void createGame_negative_invalidToken() {
        assertThrows(ResponseException.class, () -> dao.createGame("Bad Game", "fake-token"));
    }

    @Test
    void joinGame_positive() throws Exception {
        UserData user = new UserData("ellen", "pw", "ellen@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Join Game", token);
        boolean success = dao.joinGame(token, gameId, "WHITE");
        assertTrue(success);
    }

    @Test
    void joinGame_negative_invalidColor() throws Exception {
        UserData user = new UserData("frank", "pw", "frank@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        String gameId = dao.createGame("Oops Game", token);
        assertThrows(IllegalArgumentException.class, () -> dao.joinGame(token, gameId, "PURPLE"));
    }

    @Test
    void validateAuthToken_positive() throws Exception {
        UserData user = new UserData("gina", "pw", "gina@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        assertTrue(dao.validateAuthToken(token));
    }

    @Test
    void validateAuthToken_negative() {
        assertFalse(dao.validateAuthToken("invalid-token"));
    }

    @Test
    void listGames_positive() throws Exception {
        UserData user = new UserData("harry", "pw", "harry@mail.com");
        dao.addUser(user);
        String token = dao.addAuthToken(user);
        dao.createGame("G1", token);
        dao.createGame("G2", token);
        Collection<GameData> games = dao.listGames(token);
        assertEquals(2, games.size());
    }

    @Test
    void listGames_negative_invalidToken() {
        assertThrows(ResponseException.class, () -> dao.listGames("fake-token"));
    }

    @Test
    void clear_positive() throws Exception {
        UserData user = new UserData("ivy", "pw", "ivy@mail.com");
        dao.addUser(user);
        dao.clear();
        assertNull(dao.getUser("ivy"));
    }
}
