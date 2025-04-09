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
        UserData user1 = new UserData("bob", "passBobIanNotAscam", "bob@mail.com");
        dao.addUser(user1);
        UserData user2 = new UserData("bob", "diffpass", "bob2@mail.com");
        assertThrows(DataAccessException.class, () -> dao.addUser(user2));
    }


}
