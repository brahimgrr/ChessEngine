package it.unibs.pajc.model.pieces;

import it.unibs.pajc.main.Board;
import it.unibs.pajc.model.Piece;

public class Bishop extends Piece {
    public static int sheetPosition = 2;
    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
