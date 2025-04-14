package client;

import dataaccess.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws ResponseException, IOException {
        facade.clear();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void register_duplicateUsername_shouldFail() {
        assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
            facade.register("player1", "wrong", "p1@email.com");
        });
    }

    @Test
    void login_positive() throws Exception {
        facade.clear();
        var registered = facade.register("loginUser", "password", "login@email.com");
        var loggedIn = facade.login("loginUser", "password");

        assertNotNull(loggedIn.getAuthToken());
        assertEquals("loginUser", loggedIn.getUsername());
    }

    @Test
    void login_negative_wrongPassword() throws Exception {
        facade.clear();
        facade.register("wrongPassUser", "correctPass", "user@email.com");

        var exception = assertThrows(ResponseException.class, () ->
                facade.login("wrongPassUser", "wrongPass")
        );
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void login_negative_nonexistentUser() {
        var exception = assertThrows(ResponseException.class, () ->
                facade.login("ghostUser", "noPass")
        );
        assertEquals(401, exception.getStatusCode());
    }


    @Test
    void createGame_positive() throws Exception {
        var auth = facade.register("gamer1", "password", "gamer1@email.com");
        String gameId = facade.createGame("EpicBattle", auth.getAuthToken());
        assertNotNull(gameId);
    }

    @Test
    void createGame_invalidAuth() {
        var badToken = "not-a-real-token";

        var exception = assertThrows(ResponseException.class, () -> {
            facade.createGame("My Game", badToken);
        });

        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void listGames_positive() throws Exception {
        var auth = facade.register("player2", "pass123", "player2@email.com");
        facade.createGame("A cool chess game", auth.getAuthToken());
        var games = facade.listGames(auth.getAuthToken());
        assertEquals(1, games.size());
    }

    @Test
    void listGamesMultiple_positive() throws Exception {
        var auth = facade.register("player2", "pass123", "player2@email.com");
        facade.createGame("A cool chess game", auth.getAuthToken());
        facade.createGame("A cool chess game2", auth.getAuthToken());

        var games = facade.listGames(auth.getAuthToken());
        assertEquals(2, games.size());
    }

    @Test
    void listGames_negative_invalidToken() {
        assertThrows(ResponseException.class, () -> facade.listGames("bad-token"));
    }

    @Test
    void logout_negative_invalidToken() {
        var exception = assertThrows(ResponseException.class, () ->
                facade.logout("not-a-real-token")
        );
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void logout_positive() throws Exception {
        facade.clear();
        var auth = facade.register("logoutUser", "password", "logout@email.com");

        assertDoesNotThrow(() -> facade.logout(auth.getAuthToken()));
    }

    @Test
    void joinGame_positive() throws Exception {
        var auth = facade.register("joiner1", "password", "joiner1@email.com");
        var gameID = facade.createGame("Test Join Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "WHITE"));
    }

    @Test
    void joinGame_negative_invalidAuth() throws Exception {
        var auth = facade.register("joiner2", "password", "joiner2@email.com");
        var gameID = facade.createGame("Invalid Join Attempt", auth.getAuthToken());
        var ex = assertThrows(ResponseException.class, () ->
                facade.joinGame("bad-token", gameID, "BLACK")
        );
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    void observeGame_positive() throws Exception {
        var auth = facade.register("observer1", "password", "observer1@email.com");
        var gameID = facade.createGame("Watch Me Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.observeGame(auth.getAuthToken(), gameID));
    }

    @Test
    void observeGame_negative_invalidToken() throws Exception {
        var auth = facade.register("observer2", "password", "observer2@email.com");
        var gameID = facade.createGame("Private Game", auth.getAuthToken());

        var badToken = "not-a-real-token";
        var exception = assertThrows(ResponseException.class, () -> facade.observeGame(badToken, gameID));
        assertEquals(401, exception.getStatusCode());
    }


    @Test
    void register_missingFields_shouldFail() {
        assertThrows(Exception.class, () ->
                facade.register("userOnly", "", "email@email.com"));
    }

    @Test
    void register_nullEmail_shouldFail() {
        assertThrows(Exception.class, () ->
                facade.register("userOnly", "password", null));
    }

    @Test
    void login_emptyPassword_shouldFail() throws Exception {
        facade.register("emptypass", "realpass", "ep@email.com");
        var exception = assertThrows(ResponseException.class, () ->
                facade.login("emptypass", ""));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void createGame_emptyName_shouldSucceed() throws Exception {
        var auth = facade.register("emptyGameName", "pass", "empty@email.com");
        String gameId = facade.createGame("", auth.getAuthToken());
        assertNotNull(gameId);
    }

    @Test
    void joinGame_withoutListGames_shouldStillWork() throws Exception {
        var auth = facade.register("noListUser", "password", "noList@email.com");
        var gameID = facade.createGame("No List Join Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "WHITE"));
    }

    @Test
    void joinGame_colorCaseInsensitive_shouldSucceed() throws Exception {
        var auth = facade.register("caseUser", "pass", "case@email.com");
        var gameID = facade.createGame("Case Test Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "white")); // lowercase
    }

    @Test
    void logout_twice_shouldFailSecondTime() throws Exception {
        var auth = facade.register("logtwice", "password", "logtwice@email.com");
        facade.logout(auth.getAuthToken());

        var exception = assertThrows(ResponseException.class, () ->
                facade.logout(auth.getAuthToken()));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void observeGame_nonexistentGame_shouldFail() throws Exception {
        var auth = facade.register("obsFail", "pass", "obs@email.com");
        var exception = assertThrows(ResponseException.class, () ->
                facade.observeGame(auth.getAuthToken(), "9999"));
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void joinGame_alreadyTakenColor_shouldFail() throws Exception {
        var auth1 = facade.register("joinerOne", "pass", "j1@email.com");
        var auth2 = facade.register("joinerTwo", "pass", "j2@email.com");

        var gameID = facade.createGame("Taken Color Game", auth1.getAuthToken());
        facade.joinGame(auth1.getAuthToken(), gameID, "WHITE");

        var exception = assertThrows(ResponseException.class, () ->
                facade.joinGame(auth2.getAuthToken(), gameID, "WHITE"));
        assertEquals(403, exception.getStatusCode());
    }

}
