package client;

import chess.*;
import com.google.gson.Gson;
import responsesandexceptions.ResponseException;
import server.WebSocketHandler;
import ui.BoardPrinter;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class GameplayClient implements UIClient {
    private final ServerFacade server;
    private final ChessClient mainClient;
    private final String authToken;
    private final int gameId;
    private final ChessGame.TeamColor teamColor;
    private NotificationHandler notificationHandler;
    Session session;

    private ChessGame currentGame;

    public GameplayClient(ServerFacade server, ChessClient mainClient, String authToken, int gameId, ChessGame.TeamColor teamColor, ChessGame game) throws Exception {
        this.server = server;
        this.mainClient = mainClient;
        this.authToken = authToken;
        this.gameId = gameId;
        this.teamColor = teamColor;
        redrawBoard();
    }

    @OnMessage
    public void onMessage(){

    }

    public void Web(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    String notification = new Gson().fromJson(message, String.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @Override
    public String eval(String input) {
        String command = input.trim().toLowerCase();
        try {
            return switch (command) {
                case "help" -> help();
                case "redraw" -> redrawBoard();
                case "leave" -> leave();
                case "makemove" -> makeMove();
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves();
                default -> "Unknown command. Type 'help' for available commands.";
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @Override
    public String help() {
        return """
                Commands:
                - help: Show this help message
                - redraw: Redraw the chess board
                - makemove: Make a move
                - resign: Resign the game
                - leave: Leave the game and return to lobby
                - highlight: Highlight legal moves for a piece
                """;
    }

    private String redrawBoard() throws Exception {
        currentGame = server.getGame(String.valueOf(gameId), authToken);
        BoardPrinter.draw(currentGame, teamColor);
        return "Board redrawn." + currentGame.getTeamTurn();
    }

    private String leave() throws Exception {
        server.leaveGame(authToken, gameId);
        mainClient.promoteToPostLogin();
        return "You have left the game.";
    }


    private String makeMove() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start row col (e.g. 2 a): ");
        int startRow = scanner.nextInt();
        String startColLetter = scanner.next().toLowerCase();

        System.out.print("Enter end row col (e.g. 4 a): ");
        int endRow = scanner.nextInt();
        String endColLetter = scanner.next().toLowerCase();
        scanner.nextLine();

        int startCol = letterToColumn(startColLetter);
        int endCol = letterToColumn(endColLetter);

        ChessPosition from = new ChessPosition(startRow, startCol);
        var piece = currentGame.getBoard().getPiece(from);
        if (piece == null) {
            return "No piece at that position.";
        }
        if (piece.getTeamColor() != teamColor) {
            return "not your piece";
        }


        ChessPosition to = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(from, to, null);

        server.makeMove(authToken, gameId, move);
        currentGame.makeMove(move);
        currentGame = server.getGame(String.valueOf(gameId), authToken);

        BoardPrinter.draw(currentGame, teamColor);
        return "Move made." + currentGame.getTeamTurn();
    }

    private int letterToColumn(String letter) throws IllegalArgumentException {
        if (letter.length() != 1 || letter.charAt(0) < 'a' || letter.charAt(0) > 'h') {
            throw new IllegalArgumentException("Invalid column letter: " + letter);
        }
        return letter.charAt(0) - 'a' + 1;
    }



    private String resign() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            server.resignGame(authToken, gameId);
            return "You have resigned.";
        }
        return "Resignation canceled.";
    }

    private String highlightLegalMoves() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter row and column of piece (e.g. 2 a): ");
        int row = scanner.nextInt();
        String colLetter = scanner.next().toLowerCase();
        scanner.nextLine();

        int col = letterToColumn(colLetter);

        ChessPosition position = new ChessPosition(row, col);
        currentGame = server.getGame(String.valueOf(gameId), authToken);
        if (currentGame == null){
            return "No game found";
        }

        var piece = currentGame.getBoard().getPiece(position);
        if (piece == null){
            return "No piece at that position.";
        }

        var legalMoves = piece.pieceMoves(currentGame.getBoard(), position);
        if (legalMoves == null || legalMoves.isEmpty()) {
            return "No legal moves for this piece.";
        }

        BoardPrinter.highlight(currentGame, teamColor, position, legalMoves);
        return "Highlighted legal moves.";
    }

}