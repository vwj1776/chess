package chess;
import java.util.HashSet;
import java.util.Set;

public class PawnMovesCalculator {
    private ChessBoard board;
    private ChessPosition position;
    private Set<ChessMove> validMoves;

    private Set<ChessPosition> startingWhitePawnPositions;

    private Set<ChessPosition> startingBlackPawnPositions;

    private Set<ChessPosition> promotionPositions;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.validMoves = new HashSet<>();
        this.promotionPositions = new HashSet<>();
        setPromotionPositions();
        this.startingWhitePawnPositions = new HashSet<>();
        setWhitePawnPositions();
        this.startingBlackPawnPositions = new HashSet<>();
        setBlackPawnPositions();

    }

    public void setPromotionPositions() {
        for (int col = 1; col <= 8; col++) {
            promotionPositions.add(new ChessPosition(1, col)); // For black pawns
            promotionPositions.add(new ChessPosition(8, col)); // For white pawns
        }
    }

    public void setWhitePawnPositions() {


        startingWhitePawnPositions.add(new ChessPosition(2, 1));
        startingWhitePawnPositions.add(new ChessPosition(2, 2));
        startingWhitePawnPositions.add(new ChessPosition(2, 3));
        startingWhitePawnPositions.add(new ChessPosition(2, 4));
        startingWhitePawnPositions.add(new ChessPosition(2, 5));
        startingWhitePawnPositions.add(new ChessPosition(2, 6));
        startingWhitePawnPositions.add(new ChessPosition(2, 7));
        startingWhitePawnPositions.add(new ChessPosition(2, 8));


    }

    public void setBlackPawnPositions() {
        startingBlackPawnPositions.add(new ChessPosition(7, 1));
        startingBlackPawnPositions.add(new ChessPosition(7, 2));
        startingBlackPawnPositions.add(new ChessPosition(7, 3));
        startingBlackPawnPositions.add(new ChessPosition(7, 4));
        startingBlackPawnPositions.add(new ChessPosition(7, 5));
        startingBlackPawnPositions.add(new ChessPosition(7, 6));
        startingBlackPawnPositions.add(new ChessPosition(7, 7));
        startingBlackPawnPositions.add(new ChessPosition(7, 8));
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
        ChessPiece currentPiece = board.getPiece(position);
        if(currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            // Check first
            calculateFirstMove(2, 0);

            // Check up
            calculateMovesInDirection(1, 0);

            // Check right diag for death, bwahahahahaha
            calculateDeathMove(1, 1);

            // Check left diag also for death
            calculateDeathMove(1, -1);
        } else {
            // Check first
            calculateFirstMove(-2, 0);

            // Check up
            calculateMovesInDirection(-1, 0);

            // Check right diag for death, bwahahahahaha
            calculateDeathMove(-1, -1);

            // Check left diag also for death
            calculateDeathMove(-1, 1);
        }




    }

    private void calculateDeathMove(int row, int col) {
        ChessPosition move = new ChessPosition(position.getRow()+row, position.getColumn()+col);
        ChessPiece.PieceType promotion = null;


        if(promotionPositions.contains(move)) {
            promotion = ChessPiece.PieceType.QUEEN;
        }
        ChessPiece currentPiece = board.getPiece(position);
        ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(move.getRow(), move.getColumn()));
        if(pieceAtNextPosition == null) {
            return;
        }
        if ((pieceAtNextPosition.getTeamColor() != board.getPiece(position).getTeamColor()) && promotion == null) {
            validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), null));
        } else if((pieceAtNextPosition.getTeamColor() != board.getPiece(position).getTeamColor()) && promotion != null) {
            addingMoves(move);

        }
    }

    private void addingMoves(ChessPosition move) {
        validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), ChessPiece.PieceType.KNIGHT));
        validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), ChessPiece.PieceType.BISHOP));
    }

    private void calculateFirstMove(int row, int col) {
        ChessPosition move = new ChessPosition(position.getRow()+row, position.getColumn()+col);

        ChessPiece currentPiece = board.getPiece(position);
        if((currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) && startingWhitePawnPositions.contains(position)){
            ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(move.getRow(), move.getColumn()));
            ChessPiece pieceAtDirectlyInFrontPosition = board.getPiece(new ChessPosition(move.getRow()-1, move.getColumn()));

            if (pieceAtNextPosition == null && pieceAtDirectlyInFrontPosition == null) {
                // Empty cell, add the move

                validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), null));
            }
        }
        if((currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) && startingBlackPawnPositions.contains(position)){
            ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(move.getRow(), move.getColumn()));
            ChessPiece pieceAtDirectlyInFrontPosition = board.getPiece(new ChessPosition(move.getRow()+1, move.getColumn()));

            if (pieceAtNextPosition == null && pieceAtDirectlyInFrontPosition == null) {
                // Empty cell, add the move
                validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), null));
            }
        }
    }

    private void calculateMovesInDirection(int row, int col) {
        ChessPosition move = new ChessPosition(position.getRow()+row, position.getColumn()+col);
        ChessPiece.PieceType promotion = null;
        ChessPiece currentPiece = board.getPiece(position);

        if(promotionPositions.contains(move)) {
            promotion = ChessPiece.PieceType.QUEEN;
        }



        ChessPiece pieceAtNextPosition = board.getPiece(new ChessPosition(move.getRow(), move.getColumn()));

        if (pieceAtNextPosition == null && promotion == null) {
            // Empty cell, add the move

            validMoves.add(new ChessMove(position, new ChessPosition(move.getRow(), move.getColumn()), promotion));
        } else if(pieceAtNextPosition == null && promotion != null) {
            addingMoves(move);

        }
    }
}
