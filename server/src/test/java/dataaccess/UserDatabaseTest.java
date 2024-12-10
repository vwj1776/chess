package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserDatabaseTest {
    UserDataBaseAccess userDatabase;
    UserData testUser;
    String username = "user" + generateRandomString();
    String password = "pass" + generateRandomString();
    String email = "email" + generateRandomString() + "@yourmom.gov";

    @BeforeEach
    void settingUp() throws ResponseException, DataAccessException {
        userDatabase = new UserDataBaseAccess();
        testUser = new UserData(username, password, email);
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @AfterEach
    void tearDown(){

    }


    @Test
    void createUser() throws ResponseException, DataAccessException {
        userDatabase.createUser(testUser);

        var getUser = userDatabase.getUser(testUser.username());

        assertEquals(getUser, testUser);
    }

    @Test
    void getUser() throws ResponseException, DataAccessException {
        userDatabase.createUser(testUser);

        var getUser = userDatabase.getUser(testUser.username());

        assertNotNull(getUser);
    }

    @Test
    void login() throws ResponseException, DataAccessException {

    }

    @Test
    void logout() throws ResponseException, DataAccessException {

    }
}
