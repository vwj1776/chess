package chess;

import java.util.HashSet;
import java.util.Set;

public class KingMovesCalculator {
    private ChessBoard board;
    private ChessPosition position;
    private Set<ChessMove> validMoves;
    private Set<ChessMove> movesGoingToCheck;




    // just a test
    public KingMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
        this.movesGoingToCheck = new HashSet<>();
    }

    /**
     * Calculate and return the valid moves for the bishop
     *
     * @return Set of valid moves
     */
    public Set<ChessMove> calculateValidMoves() {
        validMoves.clear(); // Clear any previous valid moves

        calculateMovesGoingToCheck();

        calculateMoves();

        return validMoves;
    }

    private void calculateMovesGoingToCheck() {
        BishopMovesCalculator bishopMovesCalculator = new BishopMovesCalculator(board, position);
    }

    private void calculateMoves() {
        // Check up
        calculateMovesInDirection(1, 0);

        // Check right
        calculateMovesInDirection(0, 1);

        // Check left
        calculateMovesInDirection(0, -1);

        // Check down
        calculateMovesInDirection(-1, 0);

        // Check upper right diagonal
        calculateMovesInDirection(1, 1);

        // Check upper left diagonal
        calculateMovesInDirection(1, -1);

        // Check lower right diagonal
        calculateMovesInDirection(-1, 1);

        // Check lower left diagonal
        calculateMovesInDirection(-1, -1);
    }

    private void calculateMovesInDirection(int rowDirection, int colDirection) {
        int currentRow = position.getRow() + rowDirection;
        int currentCol = position.getColumn() + colDirection;

        ChessPosition currentPosition = new ChessPosition(currentRow, currentCol);

        if (board.isValidPosition(currentPosition)) {
            ChessPiece pieceAtCurrentPosition = board.getPiece(currentPosition);

            if (pieceAtCurrentPosition == null && !movesGoingToCheck.contains(currentPosition)) {
                // Empty cell, add the move
                validMoves.add(new ChessMove(position, currentPosition, null));
            } else if (pieceAtCurrentPosition != null) {
                // Occupied cell, check if the piece is an opponent's piece
                if (pieceAtCurrentPosition.getTeamColor()
                        != board.getPiece(position).getTeamColor()
                        && !movesGoingToCheck.contains(currentPosition)) {
                    validMoves.add(new ChessMove(position, currentPosition, null));
                }
            }
        }
    }

}
