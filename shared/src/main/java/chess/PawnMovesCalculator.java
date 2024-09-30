package chess;

import java.util.HashSet;
import java.util.Set;


public class PawnMovesCalculator {
    private ChessPosition position;
    private ChessBoard  board;
    private Set<ChessMove> validMoves;
    private boolean initMove = true;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
    }

//    validateMoves("""
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | |p| | | | |
//                        | | | |r| | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        | | | | | | | | |
//                        """,
//                          new ChessPosition(5, 4),

    private void calculateValidMoves() {
        //ifPawn is not blocked, can also move 2

        ChessGame.TeamColor pawnColor = board.getPiece(position).getTeamColor();
        if(pawnColor == ChessGame.TeamColor.WHITE){
            if(initMove && (position.getRow() == 2) && board.getPiece(new ChessPosition(position.getRow()+2, position.getColumn()))==null  && board.getPiece(new ChessPosition(position.getRow()+1, position.getColumn()))==null){
                ChessMove firstMove = new ChessMove(position, new ChessPosition(position.getRow()+2, position.getColumn()), null);
                validMoves.add(firstMove);
                initMove = false;
            }
            calculateValidMovesInDirection(1, 0);
        } else {
            if(initMove && (position.getRow() == 7) && board.getPiece(new ChessPosition(position.getRow()-2, position.getColumn()))==null&& board.getPiece(new ChessPosition(position.getRow()-1, position.getColumn()))==null){
                ChessMove firstMove = new ChessMove(position, new ChessPosition(position.getRow()-2, position.getColumn()), null);
                validMoves.add(firstMove);
                initMove = false;
            }
            calculateValidMovesInDirection(-1, 0);
        }

    }


    private void calculateValidMovesInDirection(int row, int col) {
        int currentRow = position.getRow()+row;
        int currentCol = position.getColumn()+col;
        int promotionRow8 = 8;
        int promotionRow1 = 1;

        ChessPosition endPosition = new ChessPosition(currentRow, currentCol);
        if(!board.isValidPosition(endPosition)){
            return;
        }


        pawnKilling(row, endPosition);
        if(endPosition.getRow() == promotionRow8 || endPosition.getRow() == promotionRow1){
            addPromotionPieces(endPosition);
            return;
        }
        if(board.getPiece(endPosition) != null){
            return;
        }

        ChessMove newMove = new ChessMove(position, endPosition, null);
        validMoves.add(newMove);


    }

    private void addPromotionPieces(ChessPosition endPosition) {
        validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add( new ChessMove(position, endPosition, ChessPiece.PieceType.KNIGHT));
        validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.ROOK));
    }

    public Set<ChessMove> getValidMoves() {
        calculateValidMoves();
        return validMoves;
    }


    private void pawnKilling(int row, ChessPosition endPosition) {
        if(canPawnKill(row)){
            System.out.println("pawn can kill");
            ChessPosition pawnLeftPosition = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
            ChessPosition pawnRightPosition = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
            if(row > 0) {
                pawnLeftPosition = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
                pawnRightPosition = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
            }
            if(pieceIsEnemy(pawnRightPosition)){
                if(endPosition.getRow() == 8 || endPosition.getRow() == 1){
                    addPromotionPieces(pawnRightPosition);
                    return;
                }
                validMoves.add(new ChessMove(position, pawnRightPosition, null));
            }
            if(pieceIsEnemy(pawnLeftPosition)){
                if(endPosition.getRow() == 8 || endPosition.getRow() == 1){
                    addPromotionPieces(pawnLeftPosition);
                    return;
                }
                validMoves.add(new ChessMove(position, pawnLeftPosition, null));
            }
        }
    }

    private boolean pieceIsEnemy(ChessPosition piecePosition) {
        if(board.getPiece(piecePosition) == null){
            return false;
        }
        if(board.getPiece(piecePosition).getTeamColor() == board.getPiece(position).getTeamColor()){
            return false;
        }
        return true;
    }

    private boolean canPawnKill(int row){
        ChessPosition pawnLeftPosition = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
        ChessPosition pawnRightPosition = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
        if(row > 0) {
            pawnLeftPosition = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
            pawnRightPosition = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
        }
        if(board.getPiece(pawnLeftPosition) != null){
            return true;
        }
        if(board.getPiece(pawnRightPosition) != null){
            return true;
        }
        return false;
    }



}
