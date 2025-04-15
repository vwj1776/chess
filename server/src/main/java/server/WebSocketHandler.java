package server;

import chess.ChessGame;
import com.google.gson.Gson;
import ResponsesAndExceptions.DataAccessException;
import ResponsesAndExceptions.ResponseException;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static final Map<Session, Connection> connections = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    private final ChessService service = new ChessService();

    public WebSocketHandler() throws ResponseException, DataAccessException {
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        connections.put(session, new Connection(session));
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
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
            case MAKE_MOVE -> handleMove(session, command);
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            // Validate the token and fetch game
            if (!service.validateAuthToken(command.getAuthToken())) {
                send(session, ServerMessage.error("Error: invalid auth token"));
                return;
            }

            ChessGame game = service.getGame(command.getGameID()); // Implement this in your service
            send(session, ServerMessage.loadGame(game));

            // Notify others (you'll expand this later)
            broadcastExcept(session, ServerMessage.notification("A user joined the game"));

        } catch (Exception e) {
            send(session, ServerMessage.error("Error: " + e.getMessage()));
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        send(session, ServerMessage.notification("You left the game"));
        connections.remove(session);
    }

    private void handleResign(Session session, UserGameCommand command) {
        send(session, ServerMessage.notification("You resigned the game"));
    }

    private void handleMove(Session session, UserGameCommand command) {
        // You’ll implement this when your MAKE_MOVE structure is done
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
            if (s != sender && s.isOpen()) {
                send(s, message);
            }
        }
    }

    private static class Connection {
        final Session session;

        public Connection(Session session) {
            this.session = session;
        }
    }
}
