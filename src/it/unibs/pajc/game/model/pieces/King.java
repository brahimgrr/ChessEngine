package it.unibs.pajc.game.model.pieces;

import it.unibs.pajc.game.model.*;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class King extends Piece{
    public static final int[][] KING_TABLE = {
            {-30,-40,-40,-50,-50,-40,-40,-30 },
            {-30,-40,-40,-50,-50,-40,-40,-30 },
            {-30,-40,-40,-50,-50,-40,-40,-30 },
            {-30,-40,-40,-50,-50,-40,-40,-30 },
            {-20,-30,-30,-40,-40,-30,-30,-20 },
            {-10,-20,-20,-20,-20,-20,-20,-10 },
            { 20, 20,  0,  0,  0,  0, 20, 20 },
            { 20, 30, 10,  0,  0, 10, 30, 20 }
    };

    public King(PieceColor color, Location location) {
        super(color == PieceColor.WHITE ? PieceType.KING_WHITE : PieceType.KING_BLACK, location);
    }

    /**
     * checks and returns if the current king is under check
     * @param chessBoard current board
     * @return a boolean representing if the current king is under check
     */
    public boolean underCheck(ChessBoard chessBoard) {
        Map<Location, Set<Move>> opponentMoves = MoveMap.getUnvalidatedMoveMap(chessBoard, getColor().getOpposite());
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

        List<Move> possibleMoves = new ArrayList<>();

        //castling section
        if (!hasMoved()) {
            Piece leftRook = board.getPiece(new Location(getLocation().getRow(), 0));
            Piece rightRook = board.getPiece(new Location(getLocation().getRow(), 7));

            if (leftRook instanceof Rook && !leftRook.hasMoved()) {
                boolean leftCondition = true;

                for (int i = 1; i<4; i++) {
                    Piece leftPiece = board.getPiece(new Location(getLocation().getRow(), getLocation().getCol() - i));
                    if (leftPiece != null) {
                        leftCondition = false;
                        break;
                    }
                }

                if (leftCondition) {
                    Move rookMove = new Move(leftRook.getLocation(), new Location(getLocation().getRow(), getLocation().getCol()-1));
                    Move castleMove = new Move(getLocation(), new Location(getLocation().getRow(), getLocation().getCol()-2), rookMove);

                    possibleMoves.add(castleMove);
                }
            }
            if (rightRook instanceof Rook && !rightRook.hasMoved()) {
                boolean rightCondition = true;

                for (int i = 1; i<3; i++) {
                    Piece rightPiece = board.getPiece(new Location(getLocation().getRow(), getLocation().getCol() + i));
                    if (rightPiece != null) {
                        rightCondition = false;
                        break;
                    }
                }

                if (rightCondition) {
                    Move rookMove = new Move(rightRook.getLocation(), new Location(getLocation().getRow(), getLocation().getCol()+1));
                    Move castleMove = new Move(getLocation(), new Location(getLocation().getRow(), getLocation().getCol()+2), rookMove);

                    possibleMoves.add(castleMove);
                }
            }
        }

        possibleMoves.addAll(Move.getPossibleMovesFromArray(board, this, dCols, dRows));

        return possibleMoves;
    }

    @Override
    public int[][] getPieceTable() {
        return KING_TABLE;
    }
}
