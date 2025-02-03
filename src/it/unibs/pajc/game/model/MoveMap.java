package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.pieces.King;

import java.io.Serializable;
import java.util.*;

public class MoveMap extends HashMap<Location, Set<Move>> implements Serializable {
    private final PieceColor pieceColor;

    public MoveMap(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }


    public static MoveMap getUnvalidatedMoveMap(ChessBoard board, PieceColor color) {
        MoveMap moveMap = new MoveMap(color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Location currentLocation = new Location(i, j);
                Piece piece = board.getPiece(currentLocation);
                if (piece == null || piece.getType().color != moveMap.pieceColor) {
                    continue;
                }
                Set<Move> moves = new HashSet<>(piece.getPossibleMoves(board));
                moveMap.put(currentLocation, moves);
            }
        }
        return moveMap;
    }

    public MoveMap validateMoveMap(ChessBoard board) {
        King king;
        PieceColor opponentColor = this.pieceColor.getOpposite();
        for (Location location : this.keySet()) {
            Set<Move> moves = this.get(location);
            Iterator<Move> iterator = moves.iterator();

            while (iterator.hasNext()) {
                Move move = iterator.next();
                ChessBoard clonedBoard = board.clone();
                clonedBoard.movePiece(move);
                clonedBoard.setUnvalidatedMoves(opponentColor);
                king = clonedBoard.getKing(this.pieceColor);
                if (king.underCheck(clonedBoard)) {
                    iterator.remove();  // Safe removal using iterator
                }
            }
        }
        return this;
    }


}
