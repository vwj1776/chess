package chess;

import java.util.HashSet;
import java.util.Set;


public class KingMovesCalculator {
    private ChessPosition position;
    private ChessBoard  board;
    private Set<ChessMove> validMoves;

    public KingMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
    }

//    validateMoves("""
//                        |1,1| | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | |B| | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        |   | | | | | | | |
//                        """,
//                          new ChessPosition(5, 4),

    private void calculateValidMoves() {
        //down left
        calculateValidMovesInDirection(1, -1);
        //down right
        calculateValidMovesInDirection(1, 1);
        //up right
        calculateValidMovesInDirection(-1, 1);
        //up left
        calculateValidMovesInDirection(-1, -1);
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
