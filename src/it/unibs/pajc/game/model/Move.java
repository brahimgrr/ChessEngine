package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Move implements Serializable {
    private final Location oldLocation;
    private final Location newLocation;
    private final boolean isCapture;
    //private final boolean isCheck;

    public Move(Location oldLocation, Location newLocation) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.isCapture = false;
        //this.isCheck = false;
    }

    /*public Move(Location oldLocation, Location newLocation, boolean isCapture, boolean isCheck) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.isCapture = isCapture;
        this.isCheck = isCheck;
    }*/
    public Move(Location oldLocation, Location newLocation, boolean isCapture) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
        this.isCapture = isCapture;
        //this.isCheck = false;
    }

    public Move(String moveData) {
        this.oldLocation = null;
        this.newLocation = null;
        this.isCapture = false;
    }

    public static List<Move> getHorizontal(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{1,-1};
        int[] dRow = new int[]{0,0};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    public static List<Move> getVertical(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{0,0};
        int[] dRow = new int[]{1,-1};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    public static List<Move> getDiagonal(ChessBoard board, Location currentLocation) {
        int[] dCol = new int[]{1,1,-1,-1};
        int[] dRow = new int[]{1,-1,1,-1};
        return getMoveByIteration(board, currentLocation, dCol, dRow);
    }

    public static List<Move> getCross(ChessBoard board, Location currentLocation) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getHorizontal(board, currentLocation));
        moves.addAll(getVertical(board, currentLocation));
        return moves;
    }

    public static List<Move> getAllDirections(ChessBoard board, Location currentLocation) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getDiagonal(board, currentLocation));
        moves.addAll(getCross(board, currentLocation));
        return moves;
    }

    private static boolean checkCapture(ChessBoard board, Location currentLocation, PieceColor currentColor, List<Move> moves, Location targetLocation) {
        Piece targetPiece = board.getPiece(targetLocation);
        if (targetPiece == null) {
            Move move = new Move(currentLocation, targetLocation);
            moves.add(move);
            return false;
        }
        else if (targetPiece.getColor() != currentColor) {
            //boolean check = (targetPiece instanceof King);
            //Move move = new Move(currentLocation, targetLocation, true, check);
            Move move = new Move(currentLocation, targetLocation, true);
            moves.add(move);
            return true;
        }
        else {
            return true;
        }
    }
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
                    //boolean check = (target instanceof King);
                    //moves.add(new Move(originPiece.getLocation(), targetLocation, true, check));
                    moves.add(new Move(originPiece.getLocation(), targetLocation, true));
                }
            }
        }
        return moves;
    }

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



    public Location getOldLocation() {
        return oldLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public boolean isCheck() {
        //return isCheck;
        return false;
    }

    @Override
    public int hashCode() {
        return oldLocation.hashCode() * 100 + newLocation.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() &&
                this.getNewLocation().equals(((Move) obj).getNewLocation()) &&
                this.getOldLocation().equals(((Move) obj).getOldLocation());
    }
}
