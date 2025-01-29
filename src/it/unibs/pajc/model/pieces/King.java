package it.unibs.pajc.model.pieces;

import it.unibs.pajc.main.Board;
import it.unibs.pajc.model.Piece;

public class King extends Piece{
    public static int sheetPosition = 0;
    public King(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
