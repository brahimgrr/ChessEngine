package it.unibs.pajc.model.pieces;

import it.unibs.pajc.main.Board;
import it.unibs.pajc.model.Piece;

public class Rook extends Piece{
    public static int sheetPosition = 4;
    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
