package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class King extends Piece{
    public King(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.KING_WHITE : PieceType.KING_BLACK, location);
    }

    public boolean underCheck(ChessBoard chessBoard) {
        Map<Location, Set<Move>> opponentMoves = chessBoard.getLegalMoves(getColor().getOpposite());
        for (Map.Entry<Location, Set<Move>> entry : opponentMoves.entrySet()) {
            Location opponentLocation = entry.getKey();
            Move checkMove = new Move(opponentLocation, getLocation());
            if (entry.getValue().contains(checkMove)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Move> getPossibleMoves(ChessBoard board) {
        int[] dCols = { -1, -1, -1,  0, 0,  1, 1, 1 };
        int[] dRows = { -1,  0,  1, -1, 1, -1, 0, 1 };

        return Move.getPossibleMovesFromArray(board, this, dCols, dRows);
    }
}
