package it.unibs.pajc.pieces;

import it.unibs.pajc.main.Board;

public class Queen extends Piece{
    public static int sheetPosition = 1;
    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
