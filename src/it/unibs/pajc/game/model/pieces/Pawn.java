package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
    public Pawn(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.PAWN_WHITE : PieceType.PAWN_BLACK, location);
    }

    public boolean toBePromoted() {
        if (getColor() == PieceColor.WHITE) {
            return getLocation().getRow() == 0;
        }
        else {
            return getLocation().getRow() == 7;
        }
    }


    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        List<Move> moves = new ArrayList<>();
        int direction = getColor() == PieceColor.WHITE ? -1 : 1;
        int row = getLocation().getRow();
        int col = getLocation().getCol();
        Move move;
        Location targetLocation = new Location(row + direction, col);
        if (board.getPiece(targetLocation) == null) {
            move = new Move(getLocation(), targetLocation);
            moves.add(move);
            if (!hasMoved() && board.getPiece(new Location(row + 2 * direction, col)) == null) {
                move = new Move(getLocation(), new Location(row + 2 * direction, col));
                moves.add(move);
            }
        }
        if (col < 7) {
            targetLocation = new Location(row + direction, col + 1);
            if (board.getPiece(targetLocation) != null && board.getPiece(targetLocation).getColor() != getColor()) {
                move = new Move(getLocation(), targetLocation, true);
                moves.add(move);
            }
        }
        if (col > 0) {
            targetLocation = new Location(row + direction, col - 1);
            if (board.getPiece(targetLocation) != null && board.getPiece(targetLocation).getColor() != getColor()) {
                move = new Move(getLocation(), targetLocation, true);
                moves.add(move);
            }
        }
        return moves;
    }
}
