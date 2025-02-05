package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.pieces.King;

import java.io.Serializable;
import java.util.*;


/**
 * Class used to describe possible moves starting from a certain location
 * implements serializable to use the current class for remote communication
 */
public class MoveMap extends HashMap<Location, Set<Move>> implements Serializable {
    //Color of pieces of the current map
    private final PieceColor pieceColor;

    /**
     * Constructor, specify the color of the pieces of this map
     * @param pieceColor piece color
     */
    public MoveMap(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    /**
     * checks if moves any move is available
     * @return a boolean describing if any move is available
     */
    public boolean movesAvailable() {
        for (Location loc : keySet()) {
            Set<Move> moves = get(loc);
            if (!moves.isEmpty()) return true;
        }
        return false;
    }

    /**
     * static method that calculates and returns a move map of UNVALIDATED moves.
     * doesn't discard moves in an illegal check state
     * @param board the current board
     * @param color the player color to be checked
     * @return a move map of UNVALIDATED moves for the player of the given player
     */
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

    /**
     * returns only valid and safe moves.
     * discards moves that doesn't protect a check state
     * @param board the current board
     * @return a move map of validated moves
     */
    public MoveMap validateMoveMap(ChessBoard board) {
        King king;
        //the opponent color that will attack my pieces
        PieceColor opponentColor = this.pieceColor.getOpposite();
        //clone the current board to avoid messing the current state
        ChessBoard clonedBoard = board.clone();
        //ChessBoard clonedBoard = board;
        for (Location location : this.keySet()) {
            Set<Move> moves = this.get(location);
            Iterator<Move> iterator = moves.iterator();

            //use of iterator to remove pieces (avoid concurrent modification)
            while (iterator.hasNext()) {
                Move move = iterator.next();
                //clonedBoard.movePiece(move, false);
                //TODO CHECK IF MOVE VALIDATION IS NECESSARY
                clonedBoard._movePiece(move);
                //calculate opponent moves, no need to validate them
                //since we are checking self check state
                clonedBoard.setUnvalidatedMoves(opponentColor);
                king = clonedBoard.getKing(this.pieceColor);
                //check if my king is under check
                if (king.underCheck(clonedBoard)) {
                    iterator.remove();
                }
                //restore the previous state
                clonedBoard.undoMove();
            }
        }
        return this;
    }

    /**
     * get the list of all moves from this move map
     * @return list of all moves from this move map
     */
    public List<Move> getAllMoves() {
        List<Move> moveList = new ArrayList<>();
        for (Location loc : keySet()) {
            Set<Move> moves = get(loc);
            moveList.addAll(moves);
        }
        return moveList;
    }
}
