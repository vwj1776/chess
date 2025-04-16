package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import responsesandexceptions.DataAccessException;
import responsesandexceptions.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static final Map<Session, Connection> CONNECTIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private final ChessService service = new ChessService();

    public WebSocketHandler() throws ResponseException, DataAccessException {}




    @OnWebSocketConnect
    public void onConnect(Session session) {
        CONNECTIONS.put(session, new Connection(session, -1, null));
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        CONNECTIONS.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String messageJson) {
        try {
            UserGameCommand command = GSON.fromJson(messageJson, UserGameCommand.class);
            handleCommand(session, command);
        } catch (Exception e) {
            send(session, ServerMessage.error("Error: invalid command format"));
        }
    }

    private void handleCommand(Session session, UserGameCommand command) {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case LEAVE -> handleLeave(session);
            case RESIGN -> handleResign(session);
            case MAKE_MOVE -> handleMove(session, command);
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            if (!service.validateAuthToken(command.getAuthToken())) {
                send(session, ServerMessage.error("Error: invalid auth token"));
                return;
            }

            ChessGame game = service.getGame(command.getGameID());
            CONNECTIONS.put(session, new Connection(session, command.getGameID(), command.getAuthToken()));
            send(session, ServerMessage.loadGame(game));

            String username = service.getUsernameFromAuth(command.getAuthToken());
            broadcastMessageExcept(command.getGameID(), ServerMessage.notification(username + " connected to game."), session);

        } catch (Exception e) {
            send(session, ServerMessage.error("Error: " + e.getMessage()));
        }
    }

    private void handleLeave(Session session) {
        Connection connection = CONNECTIONS.get(session);

        if (connection == null) {
            sendError(session, "You are not in a game");
            return;
        }

        int gameId = connection.getGameId();
        CONNECTIONS.remove(session);

        try {
            String username = service.getUsernameFromAuth(connection.authToken);
            broadcastMessageExcept(gameId, ServerMessage.notification(username + " left the game"), session);
        } catch (Exception e) {
            sendError(session, "Error on leave: " + e.getMessage());
        }
    }


    private void handleResign(Session session) {
        try {
            Connection connection = CONNECTIONS.get(session);
            if (connection == null) {
                sendError(session, "Not connected to any game");
                return;
            }



            int gameId = connection.getGameId();

            if(service.isGameResigned(gameId)){
                sendError(session, "Game already resigned");
                return;
            }
            String username = service.getUsernameFromAuth(connection.authToken);

            GameData gameData = service.listGames(connection.authToken).stream()
                    .filter(g -> g.gameID() == gameId)
                    .findFirst()
                    .orElseThrow(() -> new ResponseException(400, "Game not found"));
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();

            if(!Objects.equals(username, whiteUsername) && !Objects.equals(username, blackUsername)){
                System.out.println("in if");
                sendError(session, "Observers cannot resign");
                return;
            }
            String authToken = connection.getAuthToken();
            service.resignGame(gameId);

            broadcastMessage(gameId, ServerMessage.notification(username + " resigned the game."));
           // send(session, ServerMessage.notification("You resigned the game."));

        } catch (Exception e) {
            sendError(session, "Failed to resign: " + e.getMessage());
        }
    }



    private void handleMove(Session session, UserGameCommand command) {
        try {
            String auth = command.getAuthToken();
            Integer gameId = command.getGameID();
            ChessMove move = command.getMove();

            if (!service.validateAuthToken(auth)) {
                sendError(session, "Invalid auth token");
                return;
            }

            ChessGame chessGame = service.getGame(gameId);

            if (service.isGameResigned(gameId)) {
                sendError(session, "Game is already over");
                return;
            }

            GameData gameData = service.listGames(auth).stream()
                    .filter(g -> g.gameID() == gameId)
                    .findFirst()
                    .orElseThrow(() -> new ResponseException(400, "Game not found"));
            ChessGame.TeamColor color = service.getPlayerColor(auth, gameData);

            if (chessGame.getTeamTurn() != color) {
                sendError(session, "Not your turn");
                return;
            }

            chessGame.makeMove(move);
            service.saveGame(gameId, chessGame);

            broadcastMessage(gameId, ServerMessage.loadGame(chessGame));

            String playerName = service.getUsernameFromAuth(auth);
            String description = String.format("%s moved %s to %s", playerName, move.getStartPosition(), move.getEndPosition());
            broadcastMessageExcept(gameId, ServerMessage.notification(description), session);

            if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                broadcastMessage(gameId, ServerMessage.notification(playerName + " is in checkmate"));
            } else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                broadcastMessage(gameId, ServerMessage.notification(playerName + " is in check"));
            }

        } catch (Exception e) {
            sendError(session, "Invalid move or internal error");
        }
    }

    private void broadcastMessage(Integer gameId, ServerMessage message) {
        String json = GSON.toJson(message);
        for (Session s : getSessionsForGame(gameId)) {
            try {
                s.getRemote().sendString(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessageExcept(Integer gameId, ServerMessage message, Session exceptSession) {
        String json = GSON.toJson(message);
        for (Session s : getSessionsForGame(gameId)) {
            if (!s.equals(exceptSession)) {
                try {
                    s.getRemote().sendString(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendError(Session session, String message) {
        send(session, ServerMessage.error(message));
    }

    private void send(Session session, ServerMessage message) {
        try {
            String json = GSON.toJson(message);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Session> getSessionsForGame(int gameId) {
        List<Session> result = new ArrayList<>();
        for (Map.Entry<Session, Connection> entry : CONNECTIONS.entrySet()) {
            if (entry.getValue().getGameId() == gameId) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public static boolean isUserStillConnected(int gameId, String username) {
        for (var conn : CONNECTIONS.values()) {
            if (conn.getGameId() == gameId) {
                try {
                    String connectedUsername = new ChessService().getUsernameFromAuth(conn.authToken);
                    if (username.equals(connectedUsername)) return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    private static class Connection {
        final Session session;
        final int gameId;
        final String authToken;

        public Connection(Session session, int gameId, String authToken) {
            this.session = session;
            this.gameId = gameId;
            this.authToken = authToken;
        }

        public int getGameId() {
            return gameId;
        }

        public String getAuthToken() {
            return authToken;
        }
    }

}