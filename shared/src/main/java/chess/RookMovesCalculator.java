package chess;

import java.util.HashSet;
import java.util.Set;

public class RookMovesCalculator {

    private ChessBoard board;
    private ChessPosition position;
    private Set<ChessMove> validMoves;

    public RookMovesCalculator(ChessBoard board, ChessPosition position) {
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
        validMoves.clear(); // Clear any previous valid moves

        calculateNormalMoves();

        return validMoves;
    }

    private void calculateNormalMoves() {
        // Check up
        calculateNormalMovesInDirection(1, 0);

        // Check right
        calculateNormalMovesInDirection(0, 1);

        // Check left
        calculateNormalMovesInDirection(0, -1);

        // Check down
        calculateNormalMovesInDirection(-1, 0);
    }

    private void calculateNormalMovesInDirection(int rowDirection, int colDirection) {
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

            // Move to the next cell in the normal direction
            currentRow += rowDirection;
            currentCol += colDirection;
        }

    }
}
