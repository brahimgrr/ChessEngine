package it.unibs.pajc.game.model;

import java.io.Serializable;

/**
 * Class representing a location on a chessboard
 * implements serializable to use the current class for remote communication
 */
public class Location implements Serializable {
    private final int row;
    private final int col;

    /**
     * Location constructor
     * @param row location row
     * @param col location column
     */
    public Location(int row, int col) {
        if (isOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.col = col;
    }

    /**
     * returns a new inverted location
     * @return inverted location
     */
    public Location invert() {
        return new Location(7 - row, 7- col);
    }

    /**
     * returns current location inverted if flag is set, current location otherwise
     * @param invert boolean flag to get inverted location
     * @return location inverted if flag is set, current location otherwise
     */
    public Location invert(boolean invert) {
        if (invert) {
            return this.invert();
        }
        else {
            return this;
        }
    }

    /**
     * getter for location's row
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * getter for location's columns
     * @return columns
     */
    public int getCol() {
        return col;
    }

    /**
     * static method to check if a given row and column are out of
     * a chessboard constrains
     * @param row row
     * @param col column
     * @return a boolean representing if given coordinates are UNVALID
     */
    public static boolean isOutOfBounds(int row, int col) {
        return row < 0 || col < 0 || row > 7 || col > 7;
    }

    /**
     * location hashcode as a number between 0-63
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return 8 * row + col;
    }

    /**
     * String representation of the current location
     * @return location as string
     */
    @Override
    public String toString() {
        char colChar = (char) ('a' + col);
        return "(" + Character.toString(colChar).toUpperCase() + (row+1) + ")";
    }

    /**
     * checks if an object is a location an if its coordinates
     * equals this location
     * @param obj object to be checks
     * @return a boolean representing if the given object equals this location
     */
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
                this.hashCode() == obj.hashCode();
    }
}
