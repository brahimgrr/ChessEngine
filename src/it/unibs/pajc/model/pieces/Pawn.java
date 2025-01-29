package it.unibs.pajc.model.pieces;

import it.unibs.pajc.main.Board;
import it.unibs.pajc.model.Piece;

public class Pawn extends Piece{
    public static int sheetPosition = 5;
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);
    }
}
