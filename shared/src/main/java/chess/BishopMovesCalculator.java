package chess;

import java.util.HashSet;
import java.util.Set;


public class BishopMovesCalculator {
    private ChessBoard board;
    private ChessPosition position;
    private Set<ChessMove> validMoves;

    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
    }

    /**
     * Calculate and return the valid moves for the bishop
     *
     * @return Set of valid moves
     */
    public Set<ChessMove> calculateValidMoves() {
        // validMoves.clear(); // Clear any previous valid moves

        // Calculate diagonal moves
        calculateDiagonalMoves();

        return validMoves;
    }

    private void calculateDiagonalMoves() {
        // Check upper right diagonal
        calculateDiagonalMovesInDirection(1, 1);

        // Check upper left diagonal
        calculateDiagonalMovesInDirection(1, -1);

        // Check lower right diagonal
        calculateDiagonalMovesInDirection(-1, 1);

        // Check lower left diagonal
        calculateDiagonalMovesInDirection(-1, -1);
    }

    private void calculateDiagonalMovesInDirection(int rowDirection, int colDirection) {
        int currentRow = position.getRow() + rowDirection;
        int currentCol = position.getColumn() + colDirection;

        while (board.isValidPosition(new ChessPosition(currentRow, currentCol))) {
            ChessPiece pieceAtCurrentPosition = board.getPiece(new ChessPosition(currentRow, currentCol));

            if (pieceAtCurrentPosition == null) {
                // Empty cell, add the move
                validMoves.add(new ChessMove(position, new ChessPosition(currentRow, currentCol), null));
            } else {
                // Occupied cell, check if the piece is an opponent's piece
                if (pieceAtCurrentPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, new ChessPosition(currentRow, currentCol), null));
                }
                break; // Stop checking in this direction after encountering the first piece
            }

            // Move to the next cell in the diagonal direction
            currentRow += rowDirection;
            currentCol += colDirection;
        }

    }
}
