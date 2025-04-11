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





}
