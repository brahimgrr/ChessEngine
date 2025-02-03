package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.game.model.pieces.*;

import java.util.List;

public abstract class Piece {
    public final PieceType type;
    private Location location;
    private boolean hasMoved;

    public Piece(PieceType type, Location location) {
        this.type = type;
        this.location = location;
        this.hasMoved = false;
    }

    public abstract List<Move> getPossibleMoves(ChessBoard board);

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isWhite() {
        return type.color == PieceColor.WHITE;
    }


    public PieceType getType() {
        return type;
    }

    public PieceColor getColor() {
        return type.color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void pieceMoved() {
        this.hasMoved = true;
    }
    
    public static Piece getPiece(PieceType type, Location location) {
        Piece piece = null;
        switch (type) {
            case KING_BLACK -> piece = new King(PieceColor.BLACK, location);
            case KING_WHITE -> piece = new King(PieceColor.WHITE, location);
            case QUEEN_BLACK -> piece = new Queen(PieceColor.BLACK, location);
            case QUEEN_WHITE -> piece = new Queen(PieceColor.WHITE, location);
            case ROOK_BLACK -> piece = new Rook(PieceColor.BLACK, location);
            case ROOK_WHITE -> piece = new Rook(PieceColor.WHITE, location);
            case PAWN_BLACK -> piece = new Pawn(PieceColor.BLACK, location);
            case PAWN_WHITE -> piece = new Pawn(PieceColor.WHITE, location);
            case KNIGHT_BLACK -> piece = new Knight(PieceColor.BLACK, location);
            case KNIGHT_WHITE -> piece = new Knight(PieceColor.WHITE, location);
            case BISHOP_BLACK -> piece = new Bishop(PieceColor.BLACK, location);
            case BISHOP_WHITE -> piece = new Bishop(PieceColor.WHITE, location);
        }
        return piece;
    }

    @Override
    protected Object clone() {
        Piece cloned = getPiece(getType(), getLocation());
        cloned.hasMoved = this.hasMoved;
        return cloned;
    }
}
