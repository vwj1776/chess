package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    public static void draw(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        boolean isLightSquare = true;
        String squareColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
        String pieceSymbol = BLACK_KNIGHT;

        // Print the square
        System.out.print(squareColor + pieceSymbol + RESET_BG_COLOR);
        System.out.println();

    }
}
