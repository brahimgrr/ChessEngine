package it.unibs.pajc.client.model;

import it.unibs.pajc.client.view.PieceView;
import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardModel {
    private final List<Piece> pieces = new ArrayList<>();
    private final Map<Location, Set<Move>> legalMoves;
    private final List<Move> movesHistory;
    private final List<Piece> captures;
    private PieceColor mainPlayerColor; // WHITE DEFAULT COLOR

    public BoardModel() {
        this.legalMoves = new HashMap<>();
        this.movesHistory = new ArrayList<>();
        this.captures = new ArrayList<>();
        this.mainPlayerColor = PieceColor.WHITE;
    }

    /**
     * Sets the main player's color.
     *
     * @param mainPlayerColor mainPlayer color
     */
    public void setMainPlayerColor(PieceColor mainPlayerColor) {
        this.mainPlayerColor = mainPlayerColor;
    }

    /**
     * Adds a captured piece of the specified type to the capture list.
     *
     * @param type captured piece type
     */
    public void addCapture(PieceType type) {
        captures.add(new Piece(type, new Location(0,0)) {
            @Override
            public List<Move> getPossibleMoves(ChessBoard board) {
                return null;
            }

            @Override
            public int[][] getPieceTable() {
                return new int[0][];
            }
        });
    }

    /**
     * Records a move to the move history and updates the capture list if the move was a capture.
     *
     * @param move move to add
     */
    public void addMove(Move move) {
        if (move == null) return;

        movesHistory.add(move);

        if (move.isCapture()) {
            PieceType type = move.getCaptureType();
            addCapture(type);
        }
    }

    /**
     * Sets the board's pieces based on a DEN string.
     *
     * @param fen string representing the piece layout
     */
    public void setPieces(String fen) {
        pieces.clear();
        int row = 0, col = 0;
        for (char c : fen.toCharArray()) {
            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
            } else {
                PieceType type = PieceType.getPieceType(String.valueOf(c));
                pieces.add(new Piece(type, new Location(row, col)) {
                    @Override
                    public List<Move> getPossibleMoves(ChessBoard board) {
                        return null;
                    }

                    @Override
                    public int[][] getPieceTable() {
                        return new int[0][];
                    }
                });
                col++;
            }
        }
    }

    /**
     * Sets legal moves map replacing the current one.
     *
     * @param legalMoves new map of legal moves
     */
    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        this.legalMoves.clear();
        this.legalMoves.putAll(legalMoves);
    }

    /**
     * Returns the list of current pieces on the board.
     *
     * @return the list of pieces
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Returns the main player's color.
     *
     * @return mainPlayer color
     */
    public PieceColor getMainPlayerColor() {
        return mainPlayerColor;
    }

    /**
     * Returns the history of all moves made in the game.
     *
     * @return list of moves
     */
    public List<Move> getMovesHistory() {
        return movesHistory;
    }

    /**
     * Returns the list of captured pieces.
     *
     * @return list of captured pieces
     */
    public List<Piece> getCaptures() {
        return captures;
    }

    /**
     * Returns the map of legal moves for each piece location.
     *
     * @return location-moves map
     */
    public Map<Location, Set<Move>> getLegalMoves() {
        return legalMoves;
    }
}
