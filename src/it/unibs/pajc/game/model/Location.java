package it.unibs.pajc.game.model;

import java.io.Serializable;

public class Location implements Serializable {
    private final int row;
    private final int col;

    public Location(int row, int col) {
        if (isOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.col = col;
    }

    public Location invert() {
        return new Location(7 - row, 7- col);
    }

    public Location invert(boolean invert) {
        if (invert) {
            return this.invert();
        }
        else {
            return this;
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    public static boolean isOutOfBounds(int row, int col) {
        return row < 0 || col < 0 || row > 7 || col > 7;
    }

    @Override
    public int hashCode() {
        return 8 * row + col;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
                this.hashCode() == obj.hashCode();
    }
}
