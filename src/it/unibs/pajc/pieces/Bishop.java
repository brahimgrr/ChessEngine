package it.unibs.pajc.pieces;

import it.unibs.pajc.main.Board;

public class Bishop extends Piece {
    public static int sheetPosition = 2;
    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
