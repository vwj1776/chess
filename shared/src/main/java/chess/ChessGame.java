package chess;

import java.util.Collection;
import java.util.HashSet;
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
        throw new RuntimeException("Not implemented");
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
}
