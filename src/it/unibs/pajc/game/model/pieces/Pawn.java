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
    public static final int[][] PAWN_TABLE = {
            { 500, 500,  500,  500,  500,  500,  500,  500},
            {50, 50, 50, 50, 50, 50, 50, 50 },
            {10, 10, 20, 30, 30, 20, 10, 10 },
            { 5,  5, 10, 25, 25, 10,  5,  5 },
            { 0,  0,  0, 20, 20,  0,  0,  0 },
            { 5, -5,-10,  0,  0,-10, -5,  5 },
            { 5, 10, 10,-20,-20, 10, 10,  5 },
            { 0,  0,  0,  0,  0,  0,  0,  0 }
    };

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

    /**
     * list of possible moves including en passant move
     * @param board the current board
     * @return list of possible moves
     */
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
            Location passantLocation = new Location(row , col + 1);
            checkEnPassant(board, moves, targetLocation, passantLocation);
        }
        if (col > 0) {
            targetLocation = new Location(row + direction, col - 1);
            if (board.getPiece(targetLocation) != null && board.getPiece(targetLocation).getColor() != getColor()) {
                move = new Move(getLocation(), targetLocation, true);
                moves.add(move);
            }
            Location passantLocation = new Location(row , col - 1);
            checkEnPassant(board, moves, targetLocation, passantLocation);
        }
        return moves;
    }

    /**
     * checks if move is en passant, then adds it to the move list
     * @param board current board
     * @param moves current piece move list
     * @param targetLocation target location
     * @param passantLocation en passant location
     */
    private void checkEnPassant(ChessBoard board, List<Move> moves, Location targetLocation, Location passantLocation) {
        Move move;
        if (getColor() == PieceColor.WHITE && passantLocation.getRow() != 3) {
            return;
        }
        else if (getColor() == PieceColor.BLACK && passantLocation.getRow() != 4) {
            return;
        }
        if (board.getPiece(targetLocation) == null && board.getPiece(passantLocation) != null &&
                board.getPiece(passantLocation).getColor() != getColor() &&
                board.getPiece(passantLocation) instanceof Pawn &&
                board.getPiece(passantLocation).getMoveCounter() == 1) {
            move = new Move(getLocation(), targetLocation, passantLocation);
            moves.add(move);
        }
    }

    @Override
    public int[][] getPieceTable() {
        return PAWN_TABLE;
    }
}
