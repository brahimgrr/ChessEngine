package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;

public class Knight extends Piece {

    public Knight(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.KNIGHT_WHITE : PieceType.KNIGHT_BLACK, location);
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        int[] dCols = { -2, -1, 1, 2, 2, 1, -1, -2 };
        int[] dRows = { 1, 2, 2, 1, -1, -2, -2, -1 };

        return Move.getPossibleMovesFromArray(board, this, dCols, dRows);
    }
}
