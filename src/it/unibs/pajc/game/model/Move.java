package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.pieces.King;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class used to describe a chess move
 * implements serializable to use the current class for remote communication
 */
public class Move implements Serializable {
    private final Location oldLocation;
    private final Location newLocation;
    private final Location captureLocation;
    private final boolean isCapture;
    private final boolean isCheck;

    /**
     * Constructor used for VIRTUAL moves with no capture
     * @param oldLocation old location
     * @param newLocation new location
     */
    public Move(Location oldLocation, Location newLocation)     {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.captureLocation = newLocation;
        this.isCapture = false;
        this.isCheck = false;
    }

    /**
     * Constructor to specify if a move results in a capture or in a check
     * @param oldLocation old location
     * @param newLocation new location
     * @param isCapture boolean describing if the move results in a capture
     * @param isCheck boolean describing if the move results in a check
     */
    public Move(Location oldLocation, Location newLocation, boolean isCapture, boolean isCheck) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.captureLocation = newLocation;
        this.isCapture = isCapture;
        this.isCheck = isCheck;
    }

    /**
     * Constructor to specify if a move results in a capture
     * @param oldLocation old location
     * @param newLocation new location
     * @param isCapture boolean describing if the move is a capture
     */
    public Move(Location oldLocation, Location newLocation, boolean isCapture) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.captureLocation = newLocation;
        this.isCapture = isCapture;
        this.isCheck = false;
    }

    /**
     * Constructor used for en passant moves, gives the possibility
     * to set a capture location different from the new location
     * @param oldLocation old location
     * @param newLocation new location
     * @param captureLocation capture location
     */
    public Move(Location oldLocation, Location newLocation, Location captureLocation) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.captureLocation = captureLocation;
        this.isCapture = true;
        this.isCheck = false;
    }

    /**
     * calculates and returns a list of possible
     * moves horizontally starting from a given location
     * @param board the current board
     * @param currentLocation the starting piece location
     * @return list of possibles moves
     */
    public static List<Move> getHorizontal(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{1,-1};
        int[] dRow = new int[]{0,0};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    /**
     * calculates and returns a list of possible
     * moves vertically starting from a given location
     * @param board the current board
     * @param currentLocation the starting piece location
     * @return list of possibles moves
     */
    public static List<Move> getVertical(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{0,0};
        int[] dRow = new int[]{1,-1};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    /**
     * calculates and returns a list of possible
     * moves diagonally starting from a given location
     * @param board the current board
     * @param currentLocation the starting piece location
     * @return list of possibles moves
     */
    public static List<Move> getDiagonal(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{1,1,-1,-1};
        int[] dRow = new int[]{1,-1,1,-1};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    /**
     * calculates and returns a list of possible
     * moves horizontally and vertically starting from a given location
     * @param board the current board
     * @param currentLocation the starting piece location
     * @return list of possibles moves
     */
    public static List<Move> getCross(ChessBoard board, Location currentLocation) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getHorizontal(board, currentLocation));
        moves.addAll(getVertical(board, currentLocation));
        return moves;
    }

    /**
     * calculates and returns a list of possible
     * moves horizontally, vertically and diagonally starting from a given location
     * @param board the current board
     * @param currentLocation the starting piece location
     * @return list of possibles moves
     */
    public static List<Move> getAllDirections(ChessBoard board, Location currentLocation) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getDiagonal(board, currentLocation));
        moves.addAll(getCross(board, currentLocation));
        return moves;
    }

    /**
     * calculates and returns a list of possible moves
     * made by a given piece among a given set of deltas candidates
     * @param board the current board
     * @param originPiece piece to be moved
     * @param dCols int array of horizontal deltas candidates
     * @param dRows int array of vertical deltas candidates
     * @return list of possible moves
     */
    public static List<Move> getPossibleMovesFromArray(ChessBoard board, Piece originPiece, int[] dCols, int[] dRows) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < dCols.length; i++) {
            int newCol = originPiece.getLocation().getCol() + dCols[i];
            int newRow = originPiece.getLocation().getRow() + dRows[i];
            if (!Location.isOutOfBounds(newCol, newRow)) {
                Location targetLocation = new Location(newRow, newCol);
                Piece target = board.getPiece(targetLocation);
                if (target == null) {
                    moves.add(new Move(originPiece.getLocation(), targetLocation));
                }
                else if (target.getColor() != originPiece.getColor()) {
                    boolean check = (target instanceof King);
                    //TODO to check if move is check, calculate current move next iteration
                    moves.add(new Move(originPiece.getLocation(), targetLocation, true, check));
                    //moves.add(new Move(originPiece.getLocation(), targetLocation, true));
                }
            }
        }
        return moves;
    }

    /**
     * calculates and returns a list of possible moves
     * made by a given piece among a given set of deltas coordinates
     * to be iterated
     * @param board the current board
     * @param currentLocation current location of piece to be moved
     * @param dCols int array of horizontal deltas candidates
     * @param dRows int array of vertical deltas candidates
     * @return list of possible moves
     */
    private static List<Move> getMoveByIteration(ChessBoard board, Location currentLocation, int[] dCols, int[] dRows) {
        if (board.getPiece(currentLocation) == null) {
            return Collections.emptyList();
        }
        PieceColor currentColor = board.getPiece(currentLocation).getColor();
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < dCols.length; i++) {
            int newCol = currentLocation.getCol() + dCols[i];
            int newRow = currentLocation.getRow() + dRows[i];
            while (!Location.isOutOfBounds(newCol, newRow)) {
                Location targetLocation = new Location(newRow, newCol);
                if (checkCapture(board, currentLocation, currentColor, moves, targetLocation)) {
                    break;
                }
                else {
                    newCol = newCol + dCols[i];
                    newRow = newRow + dRows[i];
                }
            }
        }
        return moves;
    }

    /**
     * creates a move given an origin and target location, set the isCapture and isCheck
     * flags, and adds the created move to a list of moves
     * @param board the current board
     * @param currentLocation the current location of piece to be moved
     * @param currentColor the moving piece color
     * @param moves a move list where
     * @param targetLocation the target location
     * @return a boolean to describe if an iteration has to be stopped
     */
    private static boolean checkCapture(ChessBoard board, Location currentLocation, PieceColor currentColor, List<Move> moves, Location targetLocation) {
        Piece targetPiece = board.getPiece(targetLocation);
        //Move to an empty tile
        if (targetPiece == null) {
            Move move = new Move(currentLocation, targetLocation);
            moves.add(move);
            return false;
        }
        //Move resulting in a capture
        else if (targetPiece.getColor() != currentColor) {
            boolean check = (targetPiece instanceof King);
            //TODO to check if move is check, calculate current move next iteration
            Move move = new Move(currentLocation, targetLocation, true, check);
            //Move move = new Move(currentLocation, targetLocation, true);
            moves.add(move);
            return true;
        }
        //Invalid move, same team!
        else {
            return true;
        }
    }
    /**
     * returns the moving piece starting location
     * @return old location
     */
    public Location getOldLocation() {
        return oldLocation;
    }

    /**
     * returns the move's capture location
     * useful for en passant special move
     * @return location of captured piece
     */
    public Location getCaptureLocation() {
        return captureLocation;
    }

    /**
     * returns the moving piece final location
     * @return new location
     */
    public Location getNewLocation() {
        return newLocation;
    }

    /**
     * check if a move results in a capture
     * @return a boolean to describe if a move results in a capture
     */
    public boolean isCapture() {
        return isCapture;
    }
    /**
     * check if a move results in a check
     * @return a boolean to describe if a move results in a check
     */
    public boolean isCheck() {
        return isCheck;
    }

    /**
     * hashcode is calculated as function of the old
     * and new location hashcode
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return oldLocation.hashCode() * 100 + newLocation.hashCode();
    }

    /**
     * a string describing the current move
     * @return move string
     */
    @Override
    public String toString() {
        return oldLocation.toString() + " -> " + newLocation.toString();
    }

    /**
     * Identify to moves as same if old and new locations
     * are equal.
     * @param obj object to be checked
     * @return a boolean to describe if param is equal to this
     */
    @Override
    public boolean equals(Object obj) {
        //this will be useful to check if moves are valid in a MoveMap
        return this.getClass() == obj.getClass() &&
                this.getNewLocation().equals(((Move) obj).getNewLocation()) &&
                this.getOldLocation().equals(((Move) obj).getOldLocation());
    }
}
