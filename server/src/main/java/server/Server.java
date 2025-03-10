package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.ResponseException;
import dataaccess.UserResponse;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;
import service.ChessService;

import java.util.*;

public class Server {
    private DataAccess dataAccess = new MemoryDataAccess();
    private final ChessService service = new ChessService(dataAccess);



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

        Spark.post("/game", this::makeGame);
        Spark.get("/game", this::listGames);

        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);




        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request req, Response res) {
        try {
            // Extract auth token from header
            String authToken = req.headers("authorization");


            // Parse JSON body
            JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
            if (body.get("playerColor") == null) {
                System.out.println("----------------------------------help " + body.get("gameID") + " ");
                res.status(400);
                throw new IllegalArgumentException("bad request");
            }
            String playerColor = body.get("playerColor").getAsString();
            System.out.println("----------------------------------help " + body.get("gameID") + " ");

            if (body.get("gameID") == null) {
                System.out.println("----------------------------------help " + body.get("gameID") + " ");
                res.status(400);
                throw new IllegalArgumentException("bad request");
            }

            String gameID = body.get("gameID").getAsString();

            System.out.println("----------------------------------help " + gameID + " ");


            // Call the joinGame function
            boolean success = service.joinGame(authToken, gameID, playerColor);

            if (success) {
                res.status(200); // HTTP 200 OK
                return "{}"; // Empty JSON response
            } else {
                res.status(500); // HTTP 500 Internal Server Error
                return new Gson().toJson(Map.of("message", "Error: unknown error"));
            }

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            switch (message) {
                case "unauthorized":
                    res.status(401);
                    break;
                case "bad request":
                    res.status(400);
                    break;
                case "already taken":
                    res.status(403);
                    break;
                default:
                    res.status(500);
                    break;
            }
            return new Gson().toJson(Map.of("message", "Error: " + message));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }


    private Object clear(Request req, Response res) {
        try {
            service.clear();
            res.status(200); // HTTP 200 OK
            return "{}"; // Return empty JSON response
        } catch (Exception e) {
            res.status(500); // HTTP 500 Internal Server Error
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }


    private Object listGames(Request request, Response response) throws ResponseException {
        // Retrieve the authorization token from headers
        String authToken = request.headers("authorization");

        if (authToken == null || !service.validateAuthToken(authToken)) {
            response.status(401); // Unauthorized
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        }

        try {
            Collection<GameData> games = service.listGames(authToken);

            // Prepare response in the desired format
            String jsonResponse = new Gson().toJson(Map.of("games", games));
            response.status(200); // HTTP 200 OK
            return jsonResponse;

        } catch (Exception e) {
            // If there is an error, send a 500 error with a descriptive message
            response.status(500); // Internal Server Error
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object makeGame(Request req, Response res) {
        try {
            Map requestMap = new Gson().fromJson(req.body(), Map.class);
            String gameName = (String) requestMap.get("gameName");
            String authToken = req.headers("Authorization");
            System.out.println("Auth Token: " + authToken);
            System.out.println(authToken);

            System.out.println(gameName);
            String gameId = service.createGame(gameName, authToken); // This may throw exceptions

            res.status(200); // HTTP 200 OK
            Map<String, String> map = new HashMap<>();
            map.put("gameID", gameId);
            return new Gson().toJson(map);
        } catch (IllegalArgumentException e) {
            res.status(400); // HTTP 400 Bad Request
            return "{\"message\": \"Error: bad request\"}"; // Return raw JSON string
        } catch (IllegalStateException e) {
            res.status(401); // HTTP 401 Forbidden
            return "{\"message\": \"Error: unauthorized\"}"; // Return raw JSON string
        } catch (Exception e) {
            res.status(500); // HTTP 500 Internal Server Error
            return "{\"message\": \"Error: " + e.getMessage() + "\"}"; // Return raw JSON string
        }
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
        }
//        catch (Exception e) {
//            response.status(500); // HTTP 500 Internal Server Error
//            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage())); // Return general error message
//        }
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
    private Object getUser(Request req, Response res) throws ResponseException {
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
