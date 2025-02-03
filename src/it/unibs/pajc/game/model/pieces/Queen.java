package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;

public class Queen extends Piece{
    public Queen(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.QUEEN_WHITE : PieceType.QUEEN_BLACK, location);
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        return Move.getAllDirections(board, getLocation());
    }
}
