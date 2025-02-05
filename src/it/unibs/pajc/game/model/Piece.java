package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.game.model.pieces.*;

import java.util.List;

/**
 * Class representing a chessboard piece
 */
public abstract class Piece {
    //piece type, includes its color
    public final PieceType type;
    //piece current location
    private Location location;
    //piece move counter
    private int moveCounter;

    /**
     * Piece constructor
     * @param type piece type
     * @param location piece current location
     */
    public Piece(PieceType type, Location location) {
        this.type = type;
        this.location = location;
        this.moveCounter = 0;
    }

    /**
     * abstract method returning the possible UNVALIDATED moves of the current piece
     * @param board the current board
     * @return a list of possible UNVALIDATED moves
     */
    public abstract List<Move> getPossibleMoves(ChessBoard board);

    /**
     * abstract method returning the value table of the current piece
     * @return value table
     */
    public abstract int[][] getPieceTable();

    /**
     * calculates and returns the piece value based on
     * its type and location
     * @return piece current value
     */
    public int getValue() {
        if (getColor() == PieceColor.WHITE) {
            return type.value + getPieceTable()[location.getRow()][location.getCol()];
        } else {
            return -(type.value + getPieceTable()[7-location.getRow()][location.getCol()]);
        }
    }

    /**
     * getter for piece location
     * @return piece current location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * setter for piece location
     * @param location piece location to be set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * getter for piece type
     * @return piece current type
     */
    public PieceType getType() {
        return type;
    }

    /**
     * getter for piece color
     * @return piece current color
     */
    public PieceColor getColor() {
        return type.color;
    }

    /**
     * getter for the hasMoved flag
     * @return a boolean describing if the piece has already moved once
     */
    public boolean hasMoved() {
        return moveCounter > 0;
    }

    /**
     * signals that the current piece has moved
     */
    public void pieceMoved() {
        this.moveCounter += 1;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    /**
     * static method that returns a new piece of a given
     * type and location
     * @param type piece type
     * @param location piece location
     * @return piece of given type and location
     */
    public static Piece getPiece(PieceType type, Location location) {
        Piece piece = null;
        switch (type) {
            case KING_BLACK -> piece = new King(PieceColor.BLACK, location);
            case KING_WHITE -> piece = new King(PieceColor.WHITE, location);
            case QUEEN_BLACK -> piece = new Queen(PieceColor.BLACK, location);
            case QUEEN_WHITE -> piece = new Queen(PieceColor.WHITE, location);
            case ROOK_BLACK -> piece = new Rook(PieceColor.BLACK, location);
            case ROOK_WHITE -> piece = new Rook(PieceColor.WHITE, location);
            case PAWN_BLACK -> piece = new Pawn(PieceColor.BLACK, location);
            case PAWN_WHITE -> piece = new Pawn(PieceColor.WHITE, location);
            case KNIGHT_BLACK -> piece = new Knight(PieceColor.BLACK, location);
            case KNIGHT_WHITE -> piece = new Knight(PieceColor.WHITE, location);
            case BISHOP_BLACK -> piece = new Bishop(PieceColor.BLACK, location);
            case BISHOP_WHITE -> piece = new Bishop(PieceColor.WHITE, location);
        }
        return piece;
    }

    /**
     * returns a copy of the current piece
     * @return cloned piece
     */
    @Override
    protected Object clone() {
        Piece cloned = getPiece(getType(), getLocation());
        cloned.moveCounter = this.moveCounter;
        return cloned;
    }
}
