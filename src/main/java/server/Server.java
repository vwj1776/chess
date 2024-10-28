package server;

import com.google.gson.Gson;
import dataaccess.UserResponse;
import model.AuthData;
import model.UserData;
import spark.*;
import service.ChessService;

import java.util.Map;
import java.util.Optional;

public class Server {
    private final ChessService service;

    public Server(ChessService service) {
        this.service = service;
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.post("/user", this::addUser);
        Spark.get("/user/:username", this::getUser);

        Spark.post("/session", this::session);
        Spark.delete("/session", this::logout);

//        Spark.delete("/session", this::deleteSession);
//        Spark.post("/game", this::makeGame);
//        Spark.put("/game", this::joinGame);
//        Spark.delete("/db", this::clear);




        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object logout(Request request, Response response) {
        String authToken = request.headers("authorization");

        try {
            service.logout(authToken);
            response.status(200); // HTTP 200 OK
            return "{}"; // Return empty JSON object
        } catch (Exception e) {
            response.status(401); // HTTP 401 Unauthorized
            return new Gson().toJson(Map.of("message", "Error: unauthorized")); // Return unauthorized error message
        } catch (Exception e) {
            response.status(500); // HTTP 500 Internal Server Error
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage())); // Return general error message
        }
    }

    private Object session(Request request, Response response) {
        // Parse the request body to extract username and password
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        String username = userData.username();
        String password = userData.password();

        // Attempt to log in the user
        try {
            // Assume the service method returns an AuthData object upon successful login
            UserResponse authData = service.login(username, password);

            if (authData != null) {
                response.status(200); // HTTP 200 OK
                return new Gson().toJson(Map.of("username", username, "authToken", authData.getAuthToken())); // Return username and authToken as JSON
            } else {
                response.status(401); // HTTP 401 Unauthorized
                return new Gson().toJson(Map.of("message", "Error: unauthorized")); // Return unauthorized error message
            }
        } catch (Exception e) {
            response.status(500); // HTTP 500 Internal Server Error
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage())); // Return general error message
        }
    }


    // make records user obj, service no websocket for now

    private Object addUser(Request req, Response res) {
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            UserResponse response = service.addUser(user); // This may throw exceptions

            res.status(200); // HTTP 200 OK
            return new Gson().toJson(response); // Return user as JSON
        } catch (IllegalArgumentException e) {
            res.status(400); // HTTP 400 Bad Request
            return "{\"message\": \"Error: bad request\"}"; // Return raw JSON string
        } catch (IllegalStateException e) {
            res.status(403); // HTTP 403 Forbidden
            return "{\"message\": \"Error: already taken\"}"; // Return raw JSON string
        } catch (Exception e) {
            res.status(500); // HTTP 500 Internal Server Error
            return "{\"message\": \"Error: " + e.getMessage() + "\"}"; // Return raw JSON string
        }
    }



//    private Object getUser(Request req, Response res) {
//        String username = req.params(":username");
//        System.out.println("in get user");
//
//        System.out.println(username);
//        return service.getUser(username);
//    }
private Object getUser(Request req, Response res) {
    String username = req.params(":username");
    System.out.println("in get user");
    System.out.println("username " + username);
    UserData user = service.getUser(username);

    if (user != null) {
        res.status(200); 
        return new Gson().toJson(user);
    } else {
        res.status(404);
        return new Gson().toJson(("message" + "User not found, better luck next time, bwahahahaha"));
    }
}





    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
