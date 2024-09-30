package chess;

import java.util.HashSet;
import java.util.Set;


public class RookMovesCalculator {
    private ChessPosition position;
    private ChessBoard  board;
    private Set<ChessMove> validMoves;

    public RookMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
    }

//    validateMoves("""
//                        |1,1| | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | |R| | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        """,
//                          new ChessPosition(5, 4),

    private void calculateValidMoves() {
        //left
        calculateValidMovesInDirection(0, -1);
        //right
        calculateValidMovesInDirection(0, 1);
        //up
        calculateValidMovesInDirection(-1, 0);
        //down
        calculateValidMovesInDirection(1, 0);
    }


    private void calculateValidMovesInDirection(int row, int col) {
        int currentRow = position.getRow()+row;
        int currentCol = position.getColumn()+col;

        while (board.isValidPosition(new ChessPosition(currentRow, currentCol))){
            ChessPosition endPosition = new ChessPosition(currentRow, currentCol);
            if(board.getPiece(endPosition) != null){
                ChessGame.TeamColor bishopColor = board.getPiece(position).getTeamColor();
                ChessGame.TeamColor endPositionColor = board.getPiece(endPosition).getTeamColor();
                if(bishopColor == endPositionColor){
                    break;
                }
                else{
                    ChessMove newMove = new ChessMove(position, endPosition, null);
                    validMoves.add(newMove);
                    break;
                }
            }

            ChessMove newMove = new ChessMove(position, endPosition, null);
            validMoves.add(newMove);
            currentRow+=row;
            currentCol+=col;
        }
    }

    public Set<ChessMove> getValidMoves() {
        calculateValidMoves();
        return validMoves;
    }
}
