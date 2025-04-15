package server;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketHandler {

    private static final Gson gson = new Gson();

    private static final Map<Session, Integer> sessionGameMap = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Received WS: " + message);

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessionGameMap.remove(session);
        System.out.println("WebSocket closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        int gameId = command.getGameID();
        sessionGameMap.put(session, gameId);

        // TODO: Load game from DB and send it to client
        // ServerMessage loadGame = ServerMessage.loadGame(game);
        // session.getRemote().sendString(gson.toJson(loadGame));

        // TODO: Notify other users in game
    }

    private void handleMove(Session session, UserGameCommand command) throws IOException {
        // TODO: Validate + make move
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        // TODO: Remove user from session list
        sessionGameMap.remove(session);

        // Broadcast leave notification
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        // TODO: Mark game over
        // Broadcast resign notification
    }
}
