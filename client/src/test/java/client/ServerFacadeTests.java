package client;

import dataaccess.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Assertions.assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
            facade.register("player1", "wrong", "p1@email.com");
        });
    }


}
