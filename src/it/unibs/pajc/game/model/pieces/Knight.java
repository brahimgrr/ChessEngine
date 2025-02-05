package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;

public class Knight extends Piece {
    public static final int[][] KNIGHT_TABLE = {
            {-50,-40,-30,-30,-30,-30,-40,-50 },
            {-40,-20,  0,  0,  0,  0,-20,-40 },
            {-30,  0, 10, 15, 15, 10,  0,-30 },
            {-30,  5, 15, 20, 20, 15,  5,-30 },
            {-30,  0, 15, 20, 20, 15,  0,-30 },
            {-30,  5, 10, 15, 15, 10,  5,-30 },
            {-40,-20,  0,  5,  5,  0,-20,-40 },
            {-50,-40,-30,-30,-30,-30,-40,-50 }
    };

    public Knight(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.KNIGHT_WHITE : PieceType.KNIGHT_BLACK, location);
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        int[] dCols = { -2, -1, 1, 2, 2, 1, -1, -2 };
        int[] dRows = { 1, 2, 2, 1, -1, -2, -2, -1 };

        return Move.getPossibleMovesFromArray(board, this, dCols, dRows);
    }

    @Override
    public int[][] getPieceTable() {
        return KNIGHT_TABLE;
    }
}
