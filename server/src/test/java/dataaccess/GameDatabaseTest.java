package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameDatabaseTest {
    UserDataBaseAccess userDatabase;
    GameData testGame;
    String authtoken;


    @BeforeEach
    void settingUp() throws ResponseException, DataAccessException {
        Random random = new Random();
        userDatabase = new UserDataBaseAccess();
        authtoken = userDatabase.createAuthtoken();

        String gameName = "game" + generateRandomString();
        int gameID = random.nextInt(1_000_000);
        String whiteUsername = "whiteUsername" + generateRandomString();
        String blackUsername = "blackUsername" + generateRandomString();
        ChessGame game = new ChessGame();
        testGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void createGame() throws ResponseException, DataAccessException {
        userDatabase.addGame(testGame);

        var getGames = userDatabase.listGames(authtoken);


        List<Integer> ids = getGames.stream()
                .map(GameData::gameID)
                .collect(Collectors.toList());

        assertTrue(ids.contains(testGame.gameID()));
    }



//    @Test
//    void getGame() throws ResponseException, DataAccessException {
//        userDatabase.createUser(testUser);
//
//        var getUser = userDatabase.getUser(testUser.username());
//
//        assertNotNull(getUser);
//    }
}
