package it.unibs.pajc.model.pieces;

import it.unibs.pajc.main.Board;
import it.unibs.pajc.model.Piece;

public class Knight extends Piece {
    public static int sheetPosition = 3;
    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);

    }
}
