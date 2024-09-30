package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;

    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
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
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        if(this.pieceType.equals(PieceType.BISHOP)){
            BishopMovesCalculator bishopMovesCalculator = new BishopMovesCalculator(board, myPosition);
            moves = bishopMovesCalculator.getValidMoves();
        }
        if(this.pieceType.equals(PieceType.ROOK)){
            RookMovesCalculator rookMovesCalculator = new RookMovesCalculator(board, myPosition);
            moves = rookMovesCalculator.getValidMoves();
        }
        if(this.pieceType.equals(PieceType.QUEEN)){
            QueenMovesCalculator queenMovesCalculator = new QueenMovesCalculator(board, myPosition);
            moves = queenMovesCalculator.getValidMoves();
        }
        if(this.pieceType.equals(PieceType.KING)){
            KingMovesCalculator kingMovesCalculator = new KingMovesCalculator(board, myPosition);
            moves = kingMovesCalculator.getValidMoves();
        }
        if(this.pieceType.equals(PieceType.KNIGHT)){
            KnightMovesCalculator knightMovesCalculator = new KnightMovesCalculator(board, myPosition);
            moves = knightMovesCalculator.getValidMoves();
        }
        if(this.pieceType.equals(PieceType.PAWN)){
            PawnMovesCalculator pawnMovesCalculator = new PawnMovesCalculator(board, myPosition);
            moves = pawnMovesCalculator.getValidMoves();
        }
        return moves;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(this == null){
            str.append("null");
        }
        if(pieceColor == ChessGame.TeamColor.BLACK) {
            str.append(pieceType.toString().charAt(0));
            return str.toString().toLowerCase();

        }
        if(pieceColor == ChessGame.TeamColor.WHITE) {
            str.append(pieceType.toString().charAt(0));
            return str.toString().toUpperCase();
        }
        return "nothing";
    }
}
