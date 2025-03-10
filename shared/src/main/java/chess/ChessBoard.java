package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];

    private ChessMove lastMove;

    private ChessPiece pieceThatItWas;



    public ChessBoard() {

    }

    public ChessPiece[][] getBoard(){
        return board;
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    public void makeMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        pieceThatItWas = getPiece(endPosition);
        ChessPiece pieceToMove = getPiece(startPosition);
        addPiece(endPosition, pieceToMove);
        addPiece(startPosition, null);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null && pieceToMove.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessPiece promotedPiece = new ChessPiece(pieceToMove.getTeamColor(), promotionPiece);
            addPiece(endPosition, promotedPiece);
        }
        lastMove = move;
    }

    public void undoLastMove() {
        //TODO undo make move reverse
        if(lastMove != null) {
            ChessPosition startPosition = lastMove.getEndPosition();
            ChessPosition endPosition = lastMove.getStartPosition();
            ChessPiece pieceToMove = getPiece(startPosition);
            ChessPiece undoPromotionPiece = getPiece(lastMove.startPosition);
            if (undoPromotionPiece != null) {
                addPiece(endPosition, undoPromotionPiece);
            } else {
                addPiece(endPosition, pieceToMove);
            }
            addPiece(startPosition, pieceThatItWas); // cant make it null if it was captured, make it the peice it was
        }
        lastMove =null;
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow()-1;
        int col = position.getColumn()-1;
        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if(isValidPosition(position)){
            int row = position.getRow()-1;
            int col = position.getColumn()-1;
            return board[row][col];
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                ChessPosition position = new ChessPosition(i, j);
                if(isValidPosition(position)){
                    stringBuilder.append(getPiece(position));
                }
            }
        }
        return "" + stringBuilder;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    public void print(Object object){
        System.out.println(object);
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
