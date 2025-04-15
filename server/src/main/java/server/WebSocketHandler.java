package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ResponsesAndExceptions.DataAccessException;
import ResponsesAndExceptions.ResponseException;
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

    private static final Map<Session, Connection> connections = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private final ChessService service = new ChessService();

    public WebSocketHandler() throws ResponseException, DataAccessException {}

    @OnWebSocketConnect
    public void onConnect(Session session) {
        connections.put(session, new Connection(session, -1));
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String messageJson) {
        try {
            UserGameCommand command = gson.fromJson(messageJson, UserGameCommand.class);
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
            connections.put(session, new Connection(session, command.getGameID()));
            send(session, ServerMessage.loadGame(game));

            String username = service.getUsernameFromAuth(command.getAuthToken());
            broadcastExcept(session, ServerMessage.notification(username + " connected to game."));

        } catch (Exception e) {
            send(session, ServerMessage.error("Error: " + e.getMessage()));
        }
    }

    private void handleLeave(Session session) {
        send(session, ServerMessage.notification("You left the game"));
        connections.remove(session);
    }

    private void handleResign(Session session) {
        send(session, ServerMessage.notification("You resigned the game"));
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
            GameData gameData = null;
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
        String json = gson.toJson(message);
        for (Session s : getSessionsForGame(gameId)) {
            try {
                s.getRemote().sendString(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessageExcept(Integer gameId, ServerMessage message, Session exceptSession) {
        String json = gson.toJson(message);
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
            String json = gson.toJson(message);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastExcept(Session sender, ServerMessage message) {
        for (Session s : connections.keySet()) {
            if (!s.equals(sender) && s.isOpen()) {
                send(s, message);
            }
        }
    }

    private List<Session> getSessionsForGame(int gameId) {
        List<Session> result = new ArrayList<>();
        for (Map.Entry<Session, Connection> entry : connections.entrySet()) {
            if (entry.getValue().getGameId() == gameId) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private static class Connection {
        final Session session;
        final int gameId;

        public Connection(Session session, int gameId) {
            this.session = session;
            this.gameId = gameId;
        }

        public int getGameId() {
            return gameId;
        }
    }
}