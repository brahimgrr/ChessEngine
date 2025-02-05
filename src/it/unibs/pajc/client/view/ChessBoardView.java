package it.unibs.pajc.client.view;

import it.unibs.pajc.client.utils.Constants;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.client.utils.SoundLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

import static it.unibs.pajc.client.utils.Constants.*;

/**
 * ChessBoard VIEW
 */
public class ChessBoardView extends JPanel implements MouseMotionListener, MouseListener {
    //current active player color
    private PieceColor turnColor;
    //flag for inverted board
    private boolean invertBoard = false;
    //selected piece location (NON-ABSOLUTE)
    private Location selectedLocation = null;
    //focused tile (NON-ABSOLUTE)
    private Location focusedLocation = null;
    //last move made
    private Move lastMove = null;
    //list of piece views
    private final List<PieceView> pieces;

    private PieceView selectedPiece = null;
    //map of legal moves to be made
    private Map<Location, Set<Move>> legalMoves;
    //move semaphore to handle locking
    private final Semaphore moveSemaphore = new Semaphore(0);

    /**
     * ChessBoardView default constructor
     */
    public ChessBoardView() {
        this.setPreferredSize(new Dimension(CHESSBOARD_SIZE, CHESSBOARD_SIZE));
        this.pieces = new ArrayList<>();
        this.legalMoves = new HashMap<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * sets the board inverted flag
     * @param invertBoard inverted board flag
     */
    public void setInverted(boolean invertBoard) {
        this.invertBoard = invertBoard;
    }

    /**
     * sets the board pieces based on a fen string
     * @param fenString fen string
     */
    public void setPieces(String fenString) {
        // avoid concurrent modification
        synchronized (pieces) {
            pieces.clear();
        }
        String[] rows = fenString.split("/");
        int row = 0;
        int col = 0;
        for (char c : fenString.toCharArray()) {
            if (c == '/') {
                row++;
                col = 0;
            }
            else {
                if (Character.isDigit(c)) {
                    int nullCounter = Integer.parseInt(Character.toString(c));
                    col += nullCounter;
                }
                else {
                    PieceType type = PieceType.getPieceType(String.valueOf(c));
                    PieceView pieceView = new PieceView(type, new Location(row, col), invertBoard);
                    synchronized (pieces) {
                        pieces.add(pieceView);
                    }
                    col += 1;
                }
            }
        }
        repaint();
    }

    /**
     * set current board legal moves
     * @param legalMoves legal moves map
     */
    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        this.legalMoves = new HashMap<>(legalMoves);
    }

    /**
     * sets last move made on board
     * @param lastMove last move
     */
    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2d);
        drawTileWithFocus(g2d, selectedLocation);
        drawTileWithFocus(g2d, focusedLocation);
        drawMoveTiles(g2d, lastMove);
        drawLegalMoves(g2d);
        drawPieces(g2d);
    }

    /**
     * draws legal moves available to the selected iece
     * @param g2d graphics
     */
    private void drawLegalMoves(Graphics2D g2d) {
        if (this.selectedLocation == null) {
            return;
        }
        Location absoluteLocation = invertBoard ? this.selectedLocation.invert() : this.selectedLocation;
        if (absoluteLocation == null || legalMoves.get(absoluteLocation) == null) {
            return;
        }
        Set<Move> moves = legalMoves.get(absoluteLocation);
        for (Move move : moves) {
            Location targetLocation = move.getNewLocation().invert(invertBoard);

            int circleSize;
            if (move.isCapture()) {
                g2d.setColor(Color.RED);
                circleSize = Constants.CAPTURE_TILE;
            }
            else {
                g2d.setColor(Color.GRAY.brighter());
                circleSize = Constants.MOVE_TILE;
            }
            int r = targetLocation.getRow();
            int c = targetLocation.getCol();
            g2d.fillOval(c * TILE_SIZE + circleSize, r * TILE_SIZE + circleSize, TILE_SIZE - 2 * circleSize, TILE_SIZE - 2 * circleSize);
        }
    }

    /**
     * draw the last move tiles with focus
     * @param g2d graphics
     * @param lastMove last move
     */
    private void drawMoveTiles(Graphics2D g2d, Move lastMove) {
        if (lastMove == null) return;

        drawTileWithFocus(g2d, lastMove.getOldLocation(), invertBoard);
        drawTileWithFocus(g2d, lastMove.getNewLocation(), invertBoard);
    }

    /**
     * draws an empty board
     * @param g2d graphics
     */
    private void drawBoard(Graphics2D g2d) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                g2d.setColor((r+c) % 2 == 0 ? LIGHT_TILE : DARK_TILE);
                g2d.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * draw the current pieces on board
     * @param g2s graphics
     */
    private void drawPieces(Graphics2D g2s) {
        synchronized (pieces) {
            for (PieceView piece : pieces) {
                piece.paint(g2s);
            }
        }
        if (selectedPiece != null) {
            selectedPiece.paint(g2s);
        }
    }

    /**
     * draws a tile with focus
     * @param g2d graphics
     * @param location location of tile to be focused
     */
    private void drawTileWithFocus(Graphics2D g2d, Location location) {
        if (location == null) return;

        int r = location.getRow();
        int c = location.getCol();
        g2d.setColor((r+c) % 2 == 0 ? DARK_FOCUSED_TILE : LIGHT_FOCUSED_TILE);
        g2d.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
    /**
     * draws a tile with focus
     * @param g2d graphics
     * @param invertBoard flag for inverted board
     * @param location location of tile to be focused
     */
    private void drawTileWithFocus(Graphics2D g2d, Location location, boolean invertBoard) {
        if (invertBoard) {
            drawTileWithFocus(g2d, location.invert());
        }
        else {
            drawTileWithFocus(g2d, location);
        }
    }

    /**
     * try to retrieve a move from the view
     * calling thread is locked
     * @param turn player queried
     * @return move made
     * @throws InterruptedException
     */
    public Move requireMove(PieceColor turn) throws InterruptedException {
        this.turnColor = turn;

        try {
            moveSemaphore.acquire();
            this.turnColor = null;
            if (lastMove != null) {
                this.legalMoves.clear();
            }
            return lastMove;
        } catch (InterruptedException e) {
            moveSemaphore.release();
            throw new InterruptedException();
        }
    }

    /**
     * removes a piece from the piece list
     * @param currentLocation absolute location
     */
    private void removePieceAt(Location currentLocation) {
        synchronized (pieces) {
            PieceView pieceView = pieces.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().orElse(null);
            pieces.remove(pieceView);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * highlight the selected tile and grabs the piece if present
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Location currentLocation = PieceView.pointerToLocation(e, invertBoard);
        selectedLocation = PieceView.pointerToLocation(e);
        pieces.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().ifPresent(currentPiece -> this.selectedPiece = currentPiece);

        repaint();
    }

    /**
     * release the grabbed piece, if move is illegal
     * release the move semaphore if a move is made
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        Location currentLocation = PieceView.pointerToLocation(e, invertBoard);
        selectedLocation = null;
        if (currentLocation != null && selectedPiece != null) {

            Location lastLocation = selectedPiece.getLocation();
            Move move = new Move(lastLocation, currentLocation);
            // selected my pieces and valid position
            //add legalMoves.get(lastLocation).contains(currentLocation)
            if (selectedPiece.sameColor(turnColor) && legalMoves.get(lastLocation).contains(move)) {
                //removePieceAt(currentLocation);
                //selectedPiece.setLocation(currentLocation);
                for (Move tempMove : legalMoves.get(lastLocation)) {
                    if (tempMove.equals(move)) {
                        move = tempMove;
                        break;
                    }
                }
                lastMove = move;
                removePieceAt(move.getCaptureLocation());
                selectedPiece.setLocation(currentLocation);
                if (move.isCheck()) {
                    SoundLoader.MOVE_CHECK.play();
                }
                else if (move.isCapture()) {
                    SoundLoader.CAPTURE.play();
                }
                else {
                    SoundLoader.MOVE.play();
                }
                moveSemaphore.release();
            }
            else {
                System.out.println("INVALID MOVE");
                if (!selectedPiece.sameColor(turnColor)) {
                    System.out.println("INVALID TURN");
                }
            }

            selectedPiece.resetScreenLocation();
            selectedPiece = null;
        }
        else if (currentLocation == null && selectedPiece != null) {
            selectedPiece.resetScreenLocation();
            selectedPiece = null;
        }
        repaint();
    }

    /**
     * used to focus tiles
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        focusedLocation = PieceView.pointerToLocation(e);
        repaint();
    }

    /**
     * handle mouse out of board
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
        focusedLocation = null;
        if (selectedPiece != null) {
            selectedPiece.resetScreenLocation();
        }
        repaint();
    }

    /**
     * handle dragging of a selected peice
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        focusedLocation = PieceView.pointerToLocation(e);
        if (selectedPiece == null) return;
        selectedPiece.setScreenLocation(e);
        repaint();
    }

    /**
     * handle the focus of tiles
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectedPiece != null) return;
        focusedLocation = PieceView.pointerToLocation(e);
        repaint();
    }
}