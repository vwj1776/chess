package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;
    Collection<ChessMove> pieceMoves;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(getPieceType() == PieceType.BISHOP) {
            BishopMovesCalculator validMoves = new BishopMovesCalculator(board, myPosition);
            return validMoves.calculateValidMoves();
        }
        if(getPieceType() == PieceType.ROOK) {
            RookMovesCalculator validMoves = new RookMovesCalculator(board, myPosition);
            return validMoves.calculateValidMoves();
        }
        if(getPieceType() == PieceType.QUEEN) {
            QueenMovesCalculator validMoves = new QueenMovesCalculator(board, myPosition);
            return validMoves.calculateValidMoves();
        }
        if(getPieceType() == PieceType.KNIGHT) {
            KnightMovesCalculator validMoves = new KnightMovesCalculator(board, myPosition);
            return validMoves.calculateValidMoves();
        }
        if(getPieceType() == PieceType.PAWN) {
             PawnMovesCalculator validMoves = new PawnMovesCalculator(board, myPosition);
             return validMoves.calculateValidMoves();
        }
        if(getPieceType() == PieceType.KING) {
            KingMovesCalculator validMoves = new KingMovesCalculator(board, myPosition);
            return validMoves.calculateValidMoves();
        }
        return null;
    }
}
