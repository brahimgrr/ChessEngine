package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;

public class Rook extends Piece{
    public Rook(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.ROOK_WHITE : PieceType.ROOK_BLACK, location);
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        return Move.getCross(board, getLocation());
    }
}
