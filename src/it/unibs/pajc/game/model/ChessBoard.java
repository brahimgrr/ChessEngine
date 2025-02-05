package it.unibs.pajc.game.model;

import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.game.model.pieces.*;
import it.unibs.pajc.client.utils.Constants;

import java.util.*;

/**
 * Class representing a chessboard
 */
public class ChessBoard {
    //public static final String TEST_POSITION = "8/2k5/8/8/8/6r1/r7/3K4";
    public static final String DEFAULT_POSITION = "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr";
    //piece matrix
    private final Piece[][] tiles;
    //current turn, represented a color
    private PieceColor turn = PieceColor.WHITE;
    //map of legal moves for each player
    private final Map<PieceColor, MoveMap> legalMoves;
    //map of the board two kings
    private final Map<PieceColor, King> kings;
    //stack of captured pieces, useful for restoring board state once unmoved is called
    private final Stack<Piece> capturedPieceStack;
    //stack of moves made, useful for restoring board state once unmoved is called
    private final Stack<Move> moveStack;
    //map of board states, used for 3 repetition rule
    private final Map<String, Integer> positionMap;

    /**
     * ChessBoard constructor
     * initiate a board with the standard default position
     */
    public ChessBoard() {
        this.tiles = new Piece[8][8];
        this.kings = new HashMap<>();

        this.positionMap = new HashMap<>();

        this.setPosition(DEFAULT_POSITION);

        this.capturedPieceStack = new Stack<>();
        this.moveStack = new Stack<>();

        this.legalMoves = new HashMap<>();
        this.legalMoves.put(PieceColor.WHITE, new MoveMap(PieceColor.WHITE));
        this.legalMoves.put(PieceColor.BLACK, new MoveMap(PieceColor.BLACK));
    }

    /**
     * calculates and sets legal moves for the requested player
     * WITHOUT VALIDATING THEM
     * @param color player color
     */
    public void setUnvalidatedMoves(PieceColor color) {
        legalMoves.put(color, MoveMap.getUnvalidatedMoveMap(this, color));
    }

    /**
     * calculates and sets legal moves for the requested player
     * @param color player color
     */
    public void setValidatedLegalMoves(PieceColor color) {
        //set all possible moves for the color player
        setUnvalidatedMoves(color);
        //validates the moves for the color player
        legalMoves.get(color).validateMoveMap(this);
    }

    /**
     * retrieves the move map for the requested player
     * NO GUARANTEES OF VALIDATION OF MOVES
     * @param color player color
     * @return move map of legal moves for the player color
     */
    public MoveMap getLegalMoves(PieceColor color) {
        return legalMoves.get(color);
    }

    /**
     * getter for a piece a current location in the piece matrix
     * @param location piece location
     * @return piece retried, null if no piece is present
     */
    public Piece getPiece(Location location) {
        return tiles[location.getRow()][location.getCol()];
    }

    /**
     * sets a piece in a given location in the piece matrix
     * @param piece piece to be added
     * @param location location where piece will be added
     */
    private void addPiece(Piece piece, Location location) {
        tiles[location.getRow()][location.getCol()] = piece;
    }

    /**
     * removes any piece on the given location
     * @param location location to be emptied
     */
    private void removePiece(Location location) {
        tiles[location.getRow()][location.getCol()] = null;
    }

    /**
     * makes a move on the board, withoud checking the validity of the move
     * @param move move to be made
     */
    public void _movePiece(Move move) {
        Piece selectedPiece = getPiece(move.getOldLocation());
        moveStack.push(move);
        //remove piece from old location
        removePiece(move.getOldLocation());
        //remove acquired tile
        Piece capture = getPiece(move.getCaptureLocation());
        capturedPieceStack.push(capture);
        removePiece(move.getCaptureLocation());

        selectedPiece.setLocation(move.getNewLocation());
        selectedPiece.pieceMoved();
        //
        if (selectedPiece instanceof Pawn && ((Pawn) selectedPiece).toBePromoted()) {
            Piece promotion = new Queen(selectedPiece.getColor(), move.getNewLocation());
            addPiece(promotion, move.getNewLocation());
        }
        else {
            addPiece(selectedPiece, move.getNewLocation());
        }
    }

    /**
     * try to make a move on the board, returns the result
     * of the operation
     * @param move move to be made
     * @param savePosition save board position after move
     * @return a boolean representing if the given move is legal and correctly made
     */
    public boolean movePiece(Move move, boolean savePosition) {
        if (isValidMove(move)) {
            _movePiece(move);
            if (savePosition) {
                saveBoardPosition();
            }
            return true;
        }
        // move not valid, move not applied on board
        return false;
    }

    /**
     * using the move and piece stack, undo the last move made on the board
     */
    public void undoMove() {
        if (moveStack.isEmpty()) return;
        Move lastMove = moveStack.pop();
        Piece movedPiece = getPiece(lastMove.getNewLocation());
        if (movedPiece == null) {
            System.out.println("Move stack mismatch: no piece found");
        }
        else {
            removePiece(lastMove.getNewLocation());
            addPiece(movedPiece, lastMove.getOldLocation());
            movedPiece.setLocation(lastMove.getOldLocation());
            //restore last captured piece
            addPiece(capturedPieceStack.pop(), lastMove.getCaptureLocation());
        }
    }

    /**
     * checks and returns a boolean representing if the given move is valid
     * @param move move to be checked
     * @return a boolean representing if the given move is valid
     */
    public boolean isValidMove(Move move) {
        Piece selectedPiece = getPiece(move.getOldLocation());
        Map<Location, Set<Move>> legalMoves = getLegalMoves(turn);
        //No piece selected
        if (selectedPiece == null) {
            return false;
        }
        //Selected opponent piece
        else if (selectedPiece.getType().color != turn) {
            return false;
        }
        else {
            Set<Move> moves = legalMoves.get(selectedPiece.getLocation());
            if (moves == null) {
                System.out.println(turn + " -> Move validator : selected piece does not have legal moves available");
                return false;
            }
            else {
                // returns a boolean representing if the given move is present in the legalMoves map
                return moves.contains(move);
            }
        }
    }

    /**
     * getter for the current color turn
     * @return current turn color
     */
    public PieceColor getTurn() {
        return turn;
    }

    /**
     * inverts the current turn
     */
    public void changeTurn() {
        turn = turn.getOpposite();
    }

    /**
     * returns the board piece matrix
     * @return piece matrix
     */
    public Piece[][] getTiles() {
        return tiles;
    }

    /**
     * return the king of the given color
     * @param color player color
     * @return king of the given color
     */
    public King getKing(PieceColor color) {
        return kings.get(color);
    }

    //TODO CHECK GAME STATE EVALUATION
    /**
     * checks and returns che current board state based on player turn
     * @return the current game state
     */
    public GameState getGameState() {
        //current player move map
        MoveMap playerMap = legalMoves.get(turn);

        //player has no available moves, CHECKMATE
        if (!playerMap.movesAvailable()) {
            return turn == PieceColor.WHITE ? GameState.WIN_BLACK : GameState.WIN_WHITE;
        }
        MoveMap opponentMap = legalMoves.get(turn.getOpposite());

        String fenString = getPosition();
        if (positionMap.containsKey(fenString) && positionMap.containsValue(3)) {
            return GameState.DRAW;
        }
        if (playerMap.size() < 2 && opponentMap.size() < 2) {
            return GameState.DRAW;
        }
        return GameState.PLAYING;
    }

    /**
     * sets the board position from a fen string
     * @param fenString fen string representing a position
     */
    public void setPosition(String fenString) {
        String[] rows = fenString.split("/");
        for (int i = 0; i < rows.length; i++) {
            int col = 0;
            for (int j = 0; j < rows[i].length(); j++) {
                Character c = rows[i].charAt(j);
                //empty tiles
                if (Character.isDigit(c)) {
                    int nullCounter = Integer.parseInt(c.toString());
                    for (int k = 0; k < nullCounter; k++) {
                        tiles[i][col] = null;
                        col += 1;
                    }
                }
                //piece tiles
                else {
                    PieceType type = PieceType.getPieceType(String.valueOf(c));
                    Location location = new Location(i, col);
                    Piece piece = Piece.getPiece(type, location);
                    tiles[i][col] = piece;
                    col += 1;
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

    /**
     * calculates and return the fen string representation of the current board
     * @return current position as fen string
     */
    public String getPosition() {
        return this.toString();
    }

    /**
     * saves the current position in the positionMap
     * used for 3 repetition rule
     */
    private void saveBoardPosition() {
        String fenPosition = getPosition();
        if (positionMap.containsKey(fenPosition)) {
            positionMap.put(fenPosition, positionMap.get(fenPosition) + 1);
        }
        else {
            positionMap.put(fenPosition, 1);
        }
    }

    /**
     * FEN string builder
     * @return fen string representing current position
     */
    @Override
    public String toString() {
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

    /**
     * returns a clone of the current board
     * @return a clone of the current board
     */
    @Override
    public ChessBoard clone() {
        ChessBoard clone = new ChessBoard();
        clone.turn = turn;
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
        //TODO CHECK IF VALIDATION IS NECESSARY
        clone.setUnvalidatedMoves(PieceColor.WHITE);
        clone.setUnvalidatedMoves(PieceColor.BLACK);
        return clone;
    }

    public long getZobristKey() {
        return 0;
    }
}