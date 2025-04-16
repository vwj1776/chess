package ui;

import chess.*;

import java.util.Collection;

public class BoardPrinter {



    public static void draw(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();

        int rowStart = (perspective == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int rowEnd = (perspective == ChessGame.TeamColor.WHITE) ? 0 : 9;
        int rowStep = (perspective == ChessGame.TeamColor.WHITE) ? -1 : 1;

        int colStart = (perspective == ChessGame.TeamColor.WHITE) ? 1 : 8;
        int colEnd = (perspective == ChessGame.TeamColor.WHITE) ? 9 : 0;
        int colStep = (perspective == ChessGame.TeamColor.WHITE) ? 1 : -1;

        String header = (perspective == ChessGame.TeamColor.WHITE)
                ? "   a  b  c  d  e  f  g  h"
                : "   h  g  f  e  d  c  b  a";
        System.out.println(header);


        for (int row = rowStart; row != rowEnd; row += rowStep) {
            System.out.print(row + " ");
            for (int col = colStart; col != colEnd; col += colStep) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                boolean isLightSquare = (row + col) % 2 != 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String pieceSymbol = getSymbol(piece);

                System.out.print(bgColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(" " + row);
        }

        System.out.println(header); // Print column letters again at the bottom
    }


    private static String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        };
    }

    public static void highlight(ChessGame currentGame, ChessGame.TeamColor teamColor, ChessPosition position, Collection<ChessMove> legalMoves) {
        
    }
}
