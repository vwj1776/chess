package client;

import chess.*;
import ui.BoardPrinter;

import java.util.Scanner;

public class GameplayClient implements UIClient {
    private final ServerFacade server;
    private final ChessClient mainClient;
    private final String authToken;
    private final int gameId;
    private final ChessGame.TeamColor teamColor;
    private ChessGame currentGame;

    public GameplayClient(ServerFacade server, ChessClient mainClient, String authToken, int gameId, ChessGame.TeamColor teamColor, ChessGame game) throws Exception {
        this.server = server;
        this.mainClient = mainClient;
        this.authToken = authToken;
        this.gameId = gameId;
        this.teamColor = teamColor;
        redrawBoard();
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
        return "Board redrawn.";
    }

    private String leave() {
        mainClient.promoteToPostLogin();
        return "You have left the game.";
    }

    private String makeMove() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start row col (e.g. 2 a): ");
        int startRow = scanner.nextInt();
        int startCol = scanner.nextInt();
        System.out.print("Enter end row col (e.g. 4 a): ");
        int endRow = scanner.nextInt();
        int endCol = scanner.nextInt();
        scanner.nextLine(); // consume newline



        ChessPosition from = new ChessPosition(startRow, startCol);
        System.out.println(from);

        var piece = currentGame.getBoard().getPiece(from);
        if (piece == null) {
            return "No piece at that position.";
        }
        ChessPosition to = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(from, to, null); // TODO: add promotion input if needed

        server.makeMove(authToken, gameId, move);
        currentGame = server.getGame(String.valueOf(gameId), authToken);
        BoardPrinter.draw(currentGame, teamColor);
        return "Move made.";
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
        System.out.print("Enter row and column of piece (e.g. h 1): ");
        int row = scanner.nextInt();
        int col = scanner.nextInt();
        scanner.nextLine();

        ChessPosition position = new ChessPosition(row, col);
        currentGame = server.getGame(String.valueOf(gameId), authToken);
        var piece = currentGame.getBoard().getPiece(position);
        if (piece == null){
            return "No piece at that position.";
        }

        var legalMoves = piece.pieceMoves(currentGame.getBoard(), position);
        BoardPrinter.highlight(currentGame, teamColor, position, legalMoves);
        return "Highlighted legal moves.";
    }
}