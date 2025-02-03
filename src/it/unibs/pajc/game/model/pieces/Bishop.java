package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;

public class Bishop extends Piece {
    public Bishop(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.BISHOP_WHITE : PieceType.BISHOP_BLACK, location);
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        return Move.getDiagonal(board, getLocation());
    }
}
