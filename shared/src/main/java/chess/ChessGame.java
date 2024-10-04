package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;

    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    private Set<ChessMove> doGetValidMoves(ChessPosition startPosition) {
        if(getTeamTurn() == null) {
            setTeamTurn(TeamColor.WHITE);
        }

        // validMoves.clear();
        Set<ChessMove> validMoves = new HashSet<>();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece != null) {
            validMoves.addAll(piece.pieceMoves(board, startPosition));
        }



        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        ChessPiece pieceToMove = board.getPiece(startPosition);
        if (pieceToMove == null) {
            throw new InvalidMoveException("No piece at the starting position");
        }
        if (pieceToMove.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not your turn dumb dumb");
        }
        Set<ChessMove> potentialMoves = doGetValidMoves(move.startPosition);
        if (!potentialMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.makeMove(move);
        //TODO: add check for check

        setTeamTurn(turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        Set<ChessMove> moves = getPotentialMoves(kingPosition);

        moves = removeBadMovesWithoutStalemate(moves);
        // Check if the king has no valid moves
        if (moves.size() == 0) {
            // TeamColor oppositeTeamColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

            // Check if any piece (other than the king) has valid moves
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPiece piece = board.getBoard()[row][col];
                    if (piece != null && piece.getTeamColor() ==teamColor && piece.getPieceType() != ChessPiece.PieceType.KING) {
                        ChessPosition piecePosition = new ChessPosition(row + 1, col + 1);
                        Collection<ChessMove> pieceMoves = getPotentialMoves(piecePosition);

                        // If any piece has valid moves, it's not stalemate
                        if (!pieceMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }

            // If no piece has valid moves (other than the king), it's stalemate
            return true;
        }

        // If the king has valid moves, it's not stalemate
        return false;
    }

    public Set<ChessMove> getPotentialMoves(ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        ChessPiece piece = board.getPiece(position);
        if(piece != null) {
            validMoves.addAll(piece.pieceMoves(board, position));
        }

        return validMoves;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param newBoard the new board to use
     */
    public void setBoard(ChessBoard newBoard) {
        ChessPiece[][] newBoardState = newBoard.getBoard();
        ChessPiece[][] oldBoardState = board.getBoard();




        // Make sure the dimensions match
        if (newBoardState.length != oldBoardState.length || newBoardState[0].length != oldBoardState[0].length) {
            throw new IllegalArgumentException("Dimensions of the new board do not match the current board");
        }

        // Copy the new board's state to the current board
        for (int row = 0; row < oldBoardState.length; row++) {
            for (int col = 0; col < oldBoardState[row].length; col++) {
                oldBoardState[row][col] = newBoardState[row][col];
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Finds the position of the king for the specified team.
     *
     * @param teamColor which team to find the king for
     * @return The position of the king
     */
    private ChessPosition findKingPosition(TeamColor teamColor) {
        // Iterate through the chessboard to find the king's position


        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getBoard()[row][col];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(row + 1, col + 1);
                }
            }
        }
        // King not found (this should not happen in a valid chess game)
        return null;
    }

    /**
     * Checks if a specific position on the chessboard is under threat from any opponent pieces.
     *
     * @param kingPosition  the position to check
     * @param opponentColor the color of the opponent team
     * @return True if the position is under threat
     */
    private boolean isUnderThreat(ChessPosition kingPosition, TeamColor opponentColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getBoard()[row][col];
                if (piece != null && piece.getTeamColor() == opponentColor) {
                    ChessPosition piecePosition = new ChessPosition(row+1, col+1);
                    ChessPosition pawnPosition = new ChessPosition(6, 5);

                    Set<ChessMove> movesOfAggressor = (Set<ChessMove>) piece.pieceMoves(board, piecePosition);  // Use the piece's move calculator
                    for (ChessMove move : movesOfAggressor) {
                        if(piecePosition.equals(pawnPosition)) {
                            print("found Pawn");
                        }
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;  // The position is under threat
                        }
                    }
                }
            }
        }

        return false;  // The position is not under threat
    }

    public void print(Object obj) {
        System.out.println(obj);
        System.out.println("\n");

    }

    public Set<ChessMove> removeBadMovesWithoutStalemate(Set<ChessMove> potentialMoves) {


        ChessPosition startPositionForMoveToTry = new ChessPosition(7, 4);
        ChessPosition endPositionForMoveToTry = new ChessPosition(6, 5);
        // print(move);
        ChessMove moveToTry = new ChessMove(startPositionForMoveToTry, endPositionForMoveToTry,null);


        Iterator<ChessMove> iterator = potentialMoves.iterator();

        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            if(moveToTry.endPosition.equals(move.endPosition)) {

                print("0------in movetoTry in try");
                print(board);
                print(board.getLastMove());
            }

            ChessPosition kingPosition = new ChessPosition(6, 5);
            if(kingPosition.equals(move.endPosition)) {
                print("found pawn that shouldn't move");
            }

            ChessPosition startPosition = move.getStartPosition();
            ChessPiece pieceToMove = board.getPiece(startPosition);
            if(pieceToMove != null) {
                TeamColor turn = pieceToMove.pieceColor;

//                    makeMoveWithoutChangingTurn(move);
                board.makeMove(move);
                if (isInCheck(turn)) {
                    iterator.remove(); // Remove the move if it leads to check
                }
                board.undoLastMove();
//                    if(isInCheckmate(turn)) {
//                        iterator.remove();
//                    }
            }


        }
        return potentialMoves;
    }
}
