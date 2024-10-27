package model;
import java.util.UUID;

public record AuthData(String authToken, String username) {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static AuthData createWithGeneratedToken(String username) {
        String generatedToken = UUID.randomUUID().toString();
        return new AuthData(generatedToken, username);
    }

}
