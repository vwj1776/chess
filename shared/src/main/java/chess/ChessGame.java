package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {



    //  private Set<ChessMove> validMoves = new HashSet<>();

    private TeamColor turn = TeamColor.WHITE;

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

        ChessPosition startPositionForMoveToTry = new ChessPosition(2, 6);
        ChessPosition endPositionForMoveToTry = new ChessPosition(8, 5);
        if(startPosition.equals(startPositionForMoveToTry)) {
            print("pawn moves");
        }

        ChessMove moveToTry = new ChessMove(startPositionForMoveToTry, endPositionForMoveToTry,null);
        ChessPiece pieceToTry = board.getPiece(startPosition);

        if(isInCheck(pieceToTry.getTeamColor())) {// if the king is in threat make teh potential move, if the king is no longer under threat add the move
            print("in check you cant move normally");
        }

        Set<ChessMove> validMoves = doGetValidMoves(startPosition);
        if(validMoves.contains(moveToTry)) {
            print("0------in movetoTry 1st");
            print(validMoves);
        }
        boolean checkmate = isInCheckmate(getTeamTurn());
        boolean stalemate = isInStalemate(getTeamTurn());
        if(checkmate || stalemate) {
            return new ArrayList<>();
        }
        Set<ChessMove> validMovesNew = removeBadMovesWithoutStalemate(validMoves);

        print("validMovesNew");

        return validMovesNew;
        //return validMoves;
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

    public Set<ChessMove> removeBadMoves(Set<ChessMove> potentialMoves) {

        Iterator<ChessMove> iterator = potentialMoves.iterator();
        // print(potentialMoves);
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();

            try {

                ChessPosition startPosition = move.getStartPosition();
                ChessPiece pieceToMove = board.getPiece(startPosition);

                ChessPosition startPositionForMoveToTry = new ChessPosition(7, 4);
                ChessPosition endPositionForMoveToTry = new ChessPosition(8, 5);
                // print(move);
                ChessMove moveToTry = new ChessMove(startPositionForMoveToTry, endPositionForMoveToTry,null);
                if(moveToTry.endPosition.equals(move.endPosition)) {

                    print("0------in movetoTry in try");
                    print(board);
                    print(board.getLastMove());
                }

//                if(!moveToTry.startPosition.equals(move.startPosition)) {
//                    print("0------in movetoTry in try");
//                }

                if(pieceToMove != null) {
                    TeamColor turn = pieceToMove.pieceColor;
                    makeMoveWithoutChangingTurn(move);
                    boolean check = isInCheck(turn);
                    boolean mate = isInCheckmate(turn);
                    boolean stalemate = isInStalemate(turn);

                    if (check) {
                        iterator.remove(); // Remove the move if it leads to check
                    }

                    if(mate) {
                        iterator.remove();
                    }

                    if(stalemate){
                        iterator.remove();
                    }
                    if(!check && !mate && !stalemate) { //
                        board.undoLastMove();
                    }
                }


            } catch (InvalidMoveException e) {
                //  print(e);
                iterator.remove(); // Remove the move if it's an invalid move
            }
        }
        return potentialMoves;
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



        Set<ChessMove> potentialMoves = doGetValidMoves(move.startPosition);
        if (pieceToMove == null) {
            throw new InvalidMoveException("No piece at the starting position");
        }

        if (pieceToMove.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not your turn dumb dumb");
        }



        if (!potentialMoves.contains(move)) {

            throw new InvalidMoveException("Invalid move");
        }


        board.makeMove(move);
        if(isInCheck(pieceToMove.getTeamColor())) {

            board.undoLastMove();
            potentialMoves.remove(move);
            throw new InvalidMoveException("In Check");

        }


        setTeamTurn(turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    public void makeMoveWithoutChangingTurn(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPosition startPositionForMoveToTry = new ChessPosition(7, 4);
        ChessPosition endPositionForMoveToTry = new ChessPosition(8, 5);
        // print(move);
        ChessMove moveToTry = new ChessMove(startPositionForMoveToTry, endPositionForMoveToTry,null);
        if(moveToTry.endPosition.equals(move.endPosition)) {
            print("0------in movetoTry without changing turn");
        }
        ChessPiece pieceToMove = board.getPiece(startPosition);


        Set<ChessMove> potentialMoves = doGetValidMoves(move.startPosition);
        if (pieceToMove == null) {
            throw new InvalidMoveException("No piece at the starting position");
        }


        if (!potentialMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        board.makeMove(move);
        if(isInCheck(pieceToMove.getTeamColor())) {
            board.undoLastMove();
            potentialMoves.remove(move);
            throw new InvalidMoveException("In Check");
        }
        if(isInCheckmate(pieceToMove.getTeamColor())) {
            board.undoLastMove();
            potentialMoves.remove(move);
            throw new InvalidMoveException("In CheckMate");
        }
        if( board.getLastMove() != null) {
            board.undoLastMove();
        }
//        if(isInStalemate(pieceToMove.getTeamColor())) {
//            board.undoLastMove();
//            potentialMoves.remove(move);
//            throw new InvalidMoveException("In StaleMate");
//        }

    }


    /**
     * Determines if the given team is in check.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the position of the team's king
        ChessPosition kingPosition = findKingPosition(teamColor);

        TeamColor oppositeTeamColor = TeamColor.WHITE;
        if(teamColor == TeamColor.WHITE) {
            oppositeTeamColor = TeamColor.BLACK;
        }


        return isUnderThreat(kingPosition, oppositeTeamColor);
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
                    if(movesOfAggressor != null){
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
        }

        return false;  // The position is not under threat
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // ChessPosition kingPosition = findKingPosition(teamColor);
        boolean check = false;
        if(isInCheck(teamColor)) {
            check = true;
        }
        boolean getOut = canGetOutOfCheckByDeath();
        boolean checkmate = check;
        if(getOut && check){
            checkmate = false;
        }
        return checkmate;
    }

    private boolean canGetOutOfCheckByDeath() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if(row+1 == 5 && col+1 == 4){
                    print("in problem");
                }
                ChessPiece piece = board.getBoard()[row][col];
                if (piece != null && piece.getTeamColor() == getTeamTurn()) {
                    ChessPosition piecePosition = new ChessPosition(row + 1, col + 1);
                    Collection<ChessMove> pieceMoves = getPotentialMoves(piecePosition);

                    // If any piece has valid moves, it's not
                    // make the baskic amove and then check for check
                    for(ChessMove move: pieceMoves){
                        board.makeMove(move);
                        boolean check = isInCheck(getTeamTurn());
                        if (!pieceMoves.isEmpty() && !check) {
                            board.undoLastMove();
                            return true;
                        }
                        board.undoLastMove();
                    }

                }
            }
        }
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



    /**
     * Sets this game's chessboard with a given board
     *
     * @param newBoard new board to use
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

    public void print(Object obj) {
        System.out.println(obj);
        System.out.println("\n");

    }

    /**
     * Creates a deep copy of the chessboard.
     *
     * @return A new ChessBoard instance with the same state as the current board.
     */
    public ChessBoard copy() {
        ChessBoard copyBoard = new ChessBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                copyBoard.getBoard()[row][col] = board.getBoard()[row][col];
            }
        }

        return copyBoard;
    }

    public Set<ChessMove> getAllMovesExceptKing( TeamColor oppositeTeamColor) {
        Set<ChessMove> movesOfAggressors = new HashSet<>();  // Use the piece's move calculator

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getBoard()[row][col];
                if (piece != null && piece.getPieceType() != ChessPiece.PieceType.KING && piece.getTeamColor() ==oppositeTeamColor) {
                    ChessPosition position = new ChessPosition(row+1, col+1);
                    movesOfAggressors.addAll(piece.pieceMoves(board, position));
                }
            }
        }
        return movesOfAggressors;
    }
}
