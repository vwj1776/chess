package chess;

import java.util.HashSet;
import java.util.Set;


public class KnightMovesCalculator {
    private ChessPosition position;
    private ChessBoard  board;
    private Set<ChessMove> validMoves;

    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
    }

//    validateMoves("""
//                        |1,1| | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | |N| | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        """,
//                          new ChessPosition(5, 4),

    private void calculateValidMoves() {
        //down left long
        calculateValidMovesInDirection(2, -1);
        //down right
        calculateValidMovesInDirection(2, 1);
        //up right
        calculateValidMovesInDirection(-2, 1);
        //up left
        calculateValidMovesInDirection(-2, -1);

        //down left short
        calculateValidMovesInDirection(1, -2);
        //down right
        calculateValidMovesInDirection(1, 2);
        //up right
        calculateValidMovesInDirection(-1, 2);
        //up left
        calculateValidMovesInDirection(-1, -2);

    }


    private void calculateValidMovesInDirection(int row, int col) {
        int currentRow = position.getRow()+row;
        int currentCol = position.getColumn()+col;


        ChessPosition endPosition = new ChessPosition(currentRow, currentCol);
        if(!board.isValidPosition(endPosition)){
            return;
        }
        if(board.getPiece(endPosition) != null){
            ChessGame.TeamColor bishopColor = board.getPiece(position).getTeamColor();
            ChessGame.TeamColor endPositionColor = board.getPiece(endPosition).getTeamColor();
            if(bishopColor == endPositionColor){
                return;
            }
            else{
                ChessMove newMove = new ChessMove(position, endPosition, null);
                validMoves.add(newMove);
                return;
            }
        }

        ChessMove newMove = new ChessMove(position, endPosition, null);
        validMoves.add(newMove);
    }

    public Set<ChessMove> getValidMoves() {
        calculateValidMoves();
        return validMoves;
    }
}
