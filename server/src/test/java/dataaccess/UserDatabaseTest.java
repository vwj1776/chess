package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserDatabaseTest {
    MySqlDataAccess userDatabase;
    UserData testUser;

    @BeforeEach
    void settingUp(){

    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void addUser() throws ResponseException {
        userDatabase.addUser(testUser);

        String username = "username";
        String password = "password";
        String email = "email";
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            String authToken = AuthData.generateToken(); //todo:figure out authtoken

            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    var userId = rs.getInt(1); // where the heck do we get the authtoken from here?
                    return new UserResponse(username, String.valueOf(authToken));
                }
            }

            throw new ResponseException(500, "User could not be added. No ID was generated.");
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to add user: %s", e.getMessage()));
        }

    }
}
