package server;

import chess.ChessMove;
import responsesandexceptions.DataAccessException;
import responsesandexceptions.ResponseException;
import responsesandexceptions.UserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.GameData;
import model.UserData;
import spark.*;
import service.ChessService;

import java.util.*;

public class Server {
    private ChessService service;
    public int port;
    private WebSocketHandler webSocketHandler;


    private final DataAccess dataAccess;

    public Server() {
        try {
            this.dataAccess = new UserDataBaseAccess();
            this.service = new ChessService(this.dataAccess);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize default DataAccess", e);
        }
    }


    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.service = new ChessService(this.dataAccess);
    }





    public int getPort(){
        return port;
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();

            if (body.get("gameID") == null || body.get("gameID").getAsString().isEmpty()) {
                res.status(400);
                throw new IllegalArgumentException("bad request");
            }
            String gameID = body.get("gameID").getAsString();
            if (body.get("playerColor") == null || body.get("playerColor").getAsString().isEmpty()) {
                res.status(400);
                throw new IllegalArgumentException("bad request");
            }
            String playerColor = body.get("playerColor").getAsString();

            if (!service.validateAuthToken(authToken)) {
                throw new ResponseException(401, "Invalid auth token");
            }

            var games = service.listGames(authToken);
            boolean gameExists = games.stream().anyMatch(g -> Integer.toString(g.gameID()).equals(gameID));
            if (!gameExists) {
                throw new ResponseException(400, "Game not found");
            }

            if (playerColor.equalsIgnoreCase("observer")) {
                System.out.println("Observer joined game " + gameID);
                res.status(200);
                return "{}";
            }

            if (!playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
                res.status(400);
                throw new IllegalArgumentException("bad request, invalid player color");
            }

            boolean success = service.joinGame(authToken, gameID, playerColor);
            if (success) {
                res.status(200);
                return "{}";
            } else {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Error: unknown error"));
            }

        } catch (IllegalArgumentException e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
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
            System.out.println(gameName);

            String gameId = service.createGame(gameName, authToken); // may throw ResponseException

            res.status(200);
            return new Gson().toJson(Map.of("gameID", gameId));

        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));

        } catch (IllegalArgumentException e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));

        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }


    private Object logout(Request req, Response res) {
        String authToken = req.headers("Authorization");
        System.out.println("Headers: " + req.headers());

        try {
            service.logout(authToken);
            res.status(200);
            return "{}";
        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Object session(Request request, Response response) {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        String username = userData.username();
        String password = userData.password();

        try {
            UserResponse authData = service.login(username, password);
            response.status(200);
            return new Gson().toJson(Map.of("username", username, "authToken", authData.getAuthToken()));
        } catch (ResponseException e) {
            response.status(e.getStatusCode()); // ← Now responds with correct status (like 401)
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }



    // make records user obj, service no websocket for now

    private Object addUser(Request req, Response res) {
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            UserResponse response = service.addUser(user);

            res.status(200); // HTTP 200 OK
            return new Gson().toJson(response);

        } catch (ResponseException e) {
            res.status(e.getStatusCode());
            return "{\"message\": \"Error: " + e.getMessage() + "\"}";

        } catch (IllegalArgumentException e) {
            res.status(400);
            return "{\"message\": \"Error: bad request\"}";

        } catch (Exception e) {
            res.status(500);
            return "{\"message\": \"Error: " + e.getMessage() + "\"}";
        }
    }


    private Object getUser(Request req, Response res) throws ResponseException, DataAccessException {
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

    public int run(int desiredPort) {
        this.port = desiredPort;

        Spark.port(desiredPort);

        // ⬅️ Add this line to register the WebSocket endpoint
        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.staticFiles.location("web");

        // Register routes
        runningAllEndpoints();

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object makeMove(Request req, Response res) {
        try {
            System.out.println("Request Body: " + req.body());

            String authToken = req.headers("Authorization");
            JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();

            if (body.get("gameID") == null || body.get("move") == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Missing required fields"));
            }

            int gameId = body.get("gameID").getAsInt();
            JsonObject moveJson = body.getAsJsonObject("move");

            if (!moveJson.has("startPosition") || !moveJson.has("endPosition")) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Missing move start/end fields"));
            }

            JsonObject fromJson = moveJson.getAsJsonObject("startPosition");
            JsonObject toJson = moveJson.getAsJsonObject("endPosition");


            if (!fromJson.has("row") || !fromJson.has("col") || !toJson.has("row") || !toJson.has("col")) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Missing move coordinates"));
            }

            int fromRow = fromJson.get("row").getAsInt();
            int fromCol = fromJson.get("col").getAsInt();
            int toRow = toJson.get("row").getAsInt();
            int toCol = toJson.get("col").getAsInt();

            String promo = moveJson.has("promotion") && !moveJson.get("promotion").isJsonNull()
                    ? moveJson.get("promotion").getAsString()
                    : null;

            var move = new ChessMove(
                    new chess.ChessPosition(fromRow, fromCol),
                    new chess.ChessPosition(toRow, toCol),
                    promo == null ? null : chess.ChessPiece.PieceType.valueOf(promo)
            );

            service.makeMove(authToken, gameId, move);

            res.status(200);
            return "{}";

        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }


    private void runningAllEndpoints() {
        Spark.post("/user", this::addUser);
        Spark.get("/user/:username", this::getUser);
        Spark.webSocket("/ws", webSocketHandler);
        Spark.post("/session", this::session);
        Spark.delete("/session", this::logout);

        Spark.post("/game", this::makeGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.put("/game/move", this::makeMove);

    }


}
