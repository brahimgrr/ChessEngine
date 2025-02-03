package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.game.model.pieces.*;
import it.unibs.pajc.client.utils.Constants;

import java.util.*;

public class ChessBoard {
    private final Piece[][] tiles;

    private PieceColor turn = PieceColor.WHITE;

    private final Map<PieceColor, MoveMap> legalMoves;
    private final Map<PieceColor, King> kings;

    public ChessBoard() {
        this.tiles = new Piece[8][8];
        this.kings = new HashMap<>();
        setPosition(Constants.DEFAULT_POSITION);

        this.legalMoves = new HashMap<>();
        this.legalMoves.put(PieceColor.WHITE, new MoveMap(PieceColor.WHITE));
        this.legalMoves.put(PieceColor.BLACK, new MoveMap(PieceColor.BLACK));

        setValidatedLegalMoves(PieceColor.WHITE);
        setValidatedLegalMoves(PieceColor.BLACK);
        System.out.println("BOARD CREATED");
    }

    public ChessBoard(PieceColor turn) {
        this.turn = turn;
        this.tiles = new Piece[8][8];
        this.kings = new HashMap<>();

        this.legalMoves = new HashMap<>();
        this.legalMoves.put(PieceColor.WHITE, new MoveMap(PieceColor.WHITE));
        this.legalMoves.put(PieceColor.BLACK, new MoveMap(PieceColor.BLACK));
    }

    public void setValidatedLegalMoves(PieceColor color) {
        System.out.println("CHESSBOARD THREAD: " + Thread.currentThread().getName());
        setUnvalidatedMoves(color);
        legalMoves.get(color).validateMoveMap(this);
    }

    public MoveMap getLegalMoves(PieceColor color) {
        return legalMoves.get(color);
    }

    public void setUnvalidatedMoves(PieceColor color) {
        legalMoves.put(color, MoveMap.getUnvalidatedMoveMap(this, color));
    }

    public void removePiece(Location location) {
        tiles[location.getRow()][location.getCol()] = null;
    }

    public Piece getPiece(Location location) {
        return tiles[location.getRow()][location.getCol()];
    }

    public void addPiece(Piece piece, Location location) {
        tiles[location.getRow()][location.getCol()] = piece;
    }

    public boolean movePiece(Move move) {
        if (isValidMove(move)) {
            Piece selectedPiece = getPiece(move.getOldLocation());
            removePiece(move.getOldLocation());
            selectedPiece.setLocation(move.getNewLocation());
            selectedPiece.pieceMoved();
            if (selectedPiece instanceof Pawn && ((Pawn) selectedPiece).toBePromoted()) {
                Piece promotion = new Queen(selectedPiece.getColor(), move.getNewLocation());
                addPiece(promotion, move.getNewLocation());
            }
            else {
                addPiece(selectedPiece, move.getNewLocation());
            }
            //System.out.println("VALID MOVE");
            return true;
        }
        //System.out.println("NOT VALID MOVE");
        return false;
    }

    public boolean isValidMove(Move move) {
        Piece selectedPiece = getPiece(move.getOldLocation());
        Map<Location, Set<Move>> legalMoves = getLegalMoves(turn);
        if (selectedPiece == null) {
            return false;
        }
        else if (selectedPiece.getType().color != turn) {
            return false;
        }
        else {
            Set<Move> moves = legalMoves.get(selectedPiece.getLocation());
            if (moves == null) {
                System.out.println("NULL MOVES");
            }
            return  moves.contains(move);
        }
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void changeTurn() {
        turn = turn.getOpposite();
    }

    public Piece[][] getTiles() {
        return tiles;
    }

    public King getKing(PieceColor color) {
        return kings.get(color);
    }

    public void setPosition(String fenString) {
        //System.out.println(fenString);
        String[] rows = fenString.split("/");
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                Character c = rows[i].charAt(j);
                if (Character.isDigit(c)) {
                    int nullCounter = Integer.parseInt(c.toString());
                    for (int k = 0; k < nullCounter; k++) {
                        tiles[i][j+k] = null;
                    }
                }
                else {
                    PieceType type = PieceType.getPieceType(String.valueOf(c));
                    Location location = new Location(i, j);
                    Piece piece = Piece.getPiece(type, location);
                    tiles[i][j] = piece;
                    if (type == PieceType.KING_WHITE) {
                        kings.put(PieceColor.WHITE, (King) piece);
                    }
                    else if (type == PieceType.KING_BLACK) {
                        kings.put(PieceColor.BLACK, (King) piece);
                    }
                }
            }
        }
    }

    public String getPosition() {
        return this.toFenString();
    }

    public String toFenString() {
        StringBuilder sb = new StringBuilder();
        int nullCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tiles[i][j] != null) {
                    if (nullCounter != 0) {
                        sb.append(nullCounter);
                        nullCounter = 0;
                    }
                    sb.append(tiles[i][j].getType().shortName);
                }
                else {
                    nullCounter++;
                }
            }
            if (nullCounter != 0) {
                sb.append(nullCounter);
                nullCounter = 0;
            }
            sb.append("/");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    protected ChessBoard clone() {
        ChessBoard clone = new ChessBoard(getTurn());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tiles[i][j] != null) {
                    clone.tiles[i][j] = (Piece) tiles[i][j].clone();
                    Piece piece = clone.tiles[i][j];
                    if (piece instanceof King) {
                        clone.kings.put(piece.getColor(), (King) piece);
                    }
                }
                else {
                    clone.tiles[i][j] = null;
                }
            }
        }
        clone.setUnvalidatedMoves(PieceColor.WHITE);
        clone.setUnvalidatedMoves(PieceColor.BLACK);
        return clone;
    }
}



/*

private void initializeBoard() {
        PieceColor currentColor = turn;
        int pawnRow = 1;
        int pieceRow = 0;
        for (int i = 0; i < 2; i++) {
            tiles[pieceRow][0] = new Rook(currentColor, new Location(pieceRow,0));
            tiles[pieceRow][7] = new Rook(currentColor, new Location(pieceRow,7));

            tiles[pieceRow][1] = new Knight(currentColor, new Location(pieceRow,1));
            tiles[pieceRow][6] = new Knight(currentColor, new Location(pieceRow,6));

            tiles[pieceRow][2] = new Bishop(currentColor, new Location(pieceRow,2));
            tiles[pieceRow][5] = new Bishop(currentColor, new Location(pieceRow,5));

            tiles[pieceRow][3] = new Queen(currentColor, new Location(pieceRow,3));

            tiles[pieceRow][4] = new King(currentColor, new Location(pieceRow,4));

            for (int p = 0; p < 8; p++) {
                tiles[pawnRow][p] = new Pawn(currentColor, new Location(pawnRow,p));
            }

            currentColor = currentColor.getOpposite();
            pawnRow = 6;
            pieceRow = 7;
        }
    }
 */