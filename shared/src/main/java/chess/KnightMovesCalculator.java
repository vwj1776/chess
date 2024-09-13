package chess;

import java.util.HashSet;
import java.util.Set;

public class KnightMovesCalculator {

    private ChessBoard board;
    private ChessPosition position;
    private Set<ChessMove> validMoves;

    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
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
        // Check up left
        calculateMovesInDirection(-1, 2);

        // Check up right
        calculateMovesInDirection(1, 2);

        // Check down right
        calculateMovesInDirection(1, -2);

        // Check down left
        calculateMovesInDirection(-1, -2);

        // Check up left
        calculateMovesInDirection(-2, 1);

        // Check up right
        calculateMovesInDirection(2, 1);

        // Check down right
        calculateMovesInDirection(2, - 1);

        // Check down left
        calculateMovesInDirection(-2, -1);
    }

    private void calculateMovesInDirection(int row, int col) {
        ChessPosition move = new ChessPosition(position.getRow()+row, position.getColumn()+col);

        if(board.isValidPosition(move)){
            ChessPiece pieceAtCurrentPosition = board.getPiece(new ChessPosition(move.getRow(), move.getColumn()));
            if (pieceAtCurrentPosition == null) {
                // Empty cell, add the move
                validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), null));
            } else {
                // Occupied cell, check if the piece is an opponent's piece
                if (pieceAtCurrentPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), null));
                }

            }
        }

    }

    private void calculateMovesInDirection2(int row, int col) {
        ChessPosition move = new ChessPosition(position.getRow() + row, position.getColumn() + col);

        if (board.isValidPosition(move)) {
            ChessPiece pieceAtCurrentPosition = board.getPiece(move);
            if (pieceAtCurrentPosition == null) {
                // Empty cell, add the move
                validMoves.add(new ChessMove(position, move, null));
            } else {
                // Occupied cell, check if the piece is an opponent's piece
                if (pieceAtCurrentPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, move, null));
                }
            }
        }
    }

}
