package client;

import responsesandexceptions.ResponseException;
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
    void clearData() throws ResponseException, IOException {
        facade.clear();
    }

    @Test
    void registerShouldSucceed() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void registerDuplicateUsernameShouldFail() {
        assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
            facade.register("player1", "wrong", "p1@email.com");
        });
    }

    @Test
    void loginPositive() throws Exception {
        facade.clear();
        var registered = facade.register("loginUser", "password", "login@email.com");
        var loggedIn = facade.login("loginUser", "password");

        assertNotNull(loggedIn.getAuthToken());
        assertEquals("loginUser", loggedIn.getUsername());
    }

    @Test
    void loginWrongPasswordShouldFail() throws Exception {
        facade.clear();
        facade.register("wrongPassUser", "correctPass", "user@email.com");

        var exception = assertThrows(ResponseException.class, () ->
                facade.login("wrongPassUser", "wrongPass")
        );
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void loginNonexistentUserShouldFail() {
        var exception = assertThrows(ResponseException.class, () ->
                facade.login("ghostUser", "noPass")
        );
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void createGameShouldSucceed() throws Exception {
        var auth = facade.register("gamer1", "password", "gamer1@email.com");
        String gameId = facade.createGame("EpicBattle", auth.getAuthToken());
        assertNotNull(gameId);
    }

    @Test
    void createGameInvalidAuthShouldFail() {
        var badToken = "not-a-real-token";
        var exception = assertThrows(ResponseException.class, () -> {
            facade.createGame("My Game", badToken);
        });
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void listGamesShouldReturnOne() throws Exception {
        var auth = facade.register("player2", "pass123", "player2@email.com");
        facade.createGame("A cool chess game", auth.getAuthToken());
        var games = facade.listGames(auth.getAuthToken());
        assertEquals(1, games.size());
    }

    @Test
    void listGamesShouldReturnMultiple() throws Exception {
        var auth = facade.register("player2", "pass123", "player2@email.com");
        facade.createGame("A cool chess game", auth.getAuthToken());
        facade.createGame("A cool chess game2", auth.getAuthToken());

        var games = facade.listGames(auth.getAuthToken());
        assertEquals(2, games.size());
    }

    @Test
    void listGamesInvalidTokenShouldFail() {
        assertThrows(ResponseException.class, () -> facade.listGames("bad-token"));
    }

    @Test
    void logoutInvalidTokenShouldFail() {
        var exception = assertThrows(ResponseException.class, () ->
                facade.logout("not-a-real-token")
        );
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void logoutShouldSucceed() throws Exception {
        facade.clear();
        var auth = facade.register("logoutUser", "password", "logout@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.getAuthToken()));
    }

    @Test
    void joinGameShouldSucceed() throws Exception {
        var auth = facade.register("joiner1", "password", "joiner1@email.com");
        var gameID = facade.createGame("Test Join Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "WHITE"));
    }

    @Test
    void joinGameInvalidAuthShouldFail() throws Exception {
        var auth = facade.register("joiner2", "password", "joiner2@email.com");
        var gameID = facade.createGame("Invalid Join Attempt", auth.getAuthToken());
        var ex = assertThrows(ResponseException.class, () ->
                facade.joinGame("bad-token", gameID, "BLACK")
        );
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    void observeGameShouldSucceed() throws Exception {
        var auth = facade.register("observer1", "password", "observer1@email.com");
        var gameID = facade.createGame("Watch Me Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.observeGame(gameID, auth.getAuthToken()));
    }

    @Test
    void observeGameInvalidTokenShouldFail() throws Exception {
        var auth = facade.register("observer2", "password", "observer2@email.com");
        var gameID = facade.createGame("Private Game", auth.getAuthToken());

        var badToken = "not-a-real-token";
        var exception = assertThrows(ResponseException.class, () -> facade.observeGame(badToken, gameID));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void registerMissingFieldsShouldFail() {
        assertThrows(Exception.class, () ->
                facade.register("userOnly", "", "email@email.com"));
    }

    @Test
    void registerNullEmailShouldFail() {
        assertThrows(Exception.class, () ->
                facade.register("userOnly", "password", null));
    }

    @Test
    void loginEmptyPasswordShouldFail() throws Exception {
        facade.register("emptypass", "realpass", "ep@email.com");
        var exception = assertThrows(ResponseException.class, () ->
                facade.login("emptypass", ""));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void createGameEmptyNameShouldSucceed() throws Exception {
        var auth = facade.register("emptyGameName", "pass", "empty@email.com");
        String gameId = facade.createGame("", auth.getAuthToken());
        assertNotNull(gameId);
    }

    @Test
    void joinGameWithoutListShouldSucceed() throws Exception {
        var auth = facade.register("noListUser", "password", "noList@email.com");
        var gameID = facade.createGame("No List Join Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "WHITE"));
    }

    @Test
    void joinGameColorCaseInsensitiveShouldSucceed() throws Exception {
        var auth = facade.register("caseUser", "pass", "case@email.com");
        var gameID = facade.createGame("Case Test Game", auth.getAuthToken());
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), gameID, "white"));
    }

    @Test
    void logoutTwiceShouldFailSecondTime() throws Exception {
        var auth = facade.register("logtwice", "password", "logtwice@email.com");
        facade.logout(auth.getAuthToken());

        var exception = assertThrows(ResponseException.class, () ->
                facade.logout(auth.getAuthToken()));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void observeGameNonexistentGameShouldFail() throws Exception {
        var auth = facade.register("obsFail", "pass", "obs@email.com");
        var exception = assertThrows(ResponseException.class, () ->
                facade.observeGame("9999", auth.getAuthToken()));
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void joinGameColorAlreadyTakenShouldFail() throws Exception {
        var auth1 = facade.register("joinerOne", "pass", "j1@email.com");
        var auth2 = facade.register("joinerTwo", "pass", "j2@email.com");

        var gameID = facade.createGame("Taken Color Game", auth1.getAuthToken());
        facade.joinGame(auth1.getAuthToken(), gameID, "WHITE");

        var exception = assertThrows(ResponseException.class, () ->
                facade.joinGame(auth2.getAuthToken(), gameID, "WHITE"));
        assertEquals(403, exception.getStatusCode());
    }
}
