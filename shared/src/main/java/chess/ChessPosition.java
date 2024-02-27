package chess;

import java.util.HashMap;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    int row;
    int col;

    @Override
    public String toString() {
        return "{" +
                 row + ", " +
                 col +
                '}';
    }

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }


    @Override
    public int hashCode() {
        int RANDOM_NUMBER = 17;
        return RANDOM_NUMBER * row + col;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        ChessPosition chessObj = (ChessPosition) obj;
        if(chessObj == null) {
            return false;
        }
        if(this.row == chessObj.row && this.col == chessObj.col) {
            return true;
        }
        return false;
    }
}
