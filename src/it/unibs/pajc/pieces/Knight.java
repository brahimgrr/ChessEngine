package it.unibs.pajc.pieces;

import it.unibs.pajc.main.Board;

import java.awt.image.BufferedImage;

public class Knight extends Piece {
    public static int sheetPosition = 3;
    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, sheetPosition, isWhite);

    }
}
