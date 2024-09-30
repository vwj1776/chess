package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = getPiece(new ChessPosition(i, j));
                if (piece != null) {
                    str.append(piece);
                    str.append(" ");
                } else {
                    str.append("| ");
                }
            }
            str.append("\n");
        }
        return str.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard chessBoard = (ChessBoard) o;
        return chessBoard.toString().equals(this.toString());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     * """
     *                 |r|n|b|q|k|b|n|r|
     *                 |p|p|p|p|p|p|p|p|
     *                 | | | | | | | | |
     *                 | | | | | | | | |
     *                 | | | | | | | | |
     *                 | | | | | | | | |
     *                 |P|P|P|P|P|P|P|P|
     *                 |R|N|B|Q|K|B|N|R|
     *                 """
     */
    public void resetBoard() {
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        for(int i = 1; i <= 8; i++) {
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.ROOK);
                addPiece(new ChessPosition(i, 1), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.KNIGHT);
                addPiece(new ChessPosition(i, 2), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.BISHOP);
                addPiece(new ChessPosition(i, 3), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.QUEEN);
                addPiece(new ChessPosition(i, 4), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.KING);
                addPiece(new ChessPosition(i, 5), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.BISHOP);
                addPiece(new ChessPosition(i, 6), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.KNIGHT);
                addPiece(new ChessPosition(i, 7), newPiece);
            }
            if(i == 8){
                ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.ROOK);
                addPiece(new ChessPosition(i, 8), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.ROOK);
                addPiece(new ChessPosition(i, 1), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.KNIGHT);
                addPiece(new ChessPosition(i, 2), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.BISHOP);
                addPiece(new ChessPosition(i, 3), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.QUEEN);
                addPiece(new ChessPosition(i, 4), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.KING);
                addPiece(new ChessPosition(i, 5), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.BISHOP);
                addPiece(new ChessPosition(i, 6), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.KNIGHT);
                addPiece(new ChessPosition(i, 7), newPiece);
            }
            if(i == 1){
                ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.ROOK);
                addPiece(new ChessPosition(i, 8), newPiece);
            }
            for(int j = 1; j <= 8; j++) {
                if(i == 2){
                    ChessPiece newPiece = new ChessPiece(white, ChessPiece.PieceType.PAWN);
                    addPiece(new ChessPosition(i, j), newPiece);
                }
                if(i == 7){
                    ChessPiece newPiece = new ChessPiece(black, ChessPiece.PieceType.PAWN);
                    addPiece(new ChessPosition(i, j), newPiece);
                }
            }
        }
    }

    public boolean isValidPosition(ChessPosition position) {
        if(position != null) {
            if(position.getRow()-1 <= 7 && position.getColumn()-1 <= 7 && position.getRow()-1 >= 0 && position.getColumn()-1 >= 0) {
                return true;
            }

        }
        return false;
    }
}
