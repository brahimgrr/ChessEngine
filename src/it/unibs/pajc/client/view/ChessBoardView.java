package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
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
    private final BoardController controller;
    //current active player color
    private PieceColor turnColor;
    //selected piece location (NON-ABSOLUTE)
    private PieceView selectedPiece = null;
    private Location selectedLocation = null;
    //focused tile (NON-ABSOLUTE)
    private Location focusedLocation = null;
    //last move made
    private Move lastMove = null;
    //list of piece VIEWS
    private final List<PieceView> pieceViews;

    //move semaphore to handle locking
    private final Semaphore moveSemaphore = new Semaphore(0);

    /**
     * ChessBoardView default constructor
     */
    public ChessBoardView(BoardController boardController) {
        this.setBackground(Color.GRAY);
        this.controller = boardController;
        this.setPreferredSize(new Dimension(getTileSize() * 8, getTileSize() * 8));
        this.pieceViews = new ArrayList<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * checks if the board is inverted
     * @return boolean inverted board flag
     */
    public boolean isInverted() {
        return controller.getMainPlayerColor() == PieceColor.BLACK;
    }

    /**
     * Sets the list of views representing pieces on board
     * @param pieces pieces views
     */
    public void setPieceViews(List<PieceView> pieces) {
        synchronized (pieceViews) {
            pieceViews.clear();
            pieceViews.addAll(pieces);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2d);
        drawTileWithFocus(g2d, selectedLocation);
        drawTileWithFocus(g2d, focusedLocation);
        drawLastMove(g2d);
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
        Location absoluteLocation = isInverted() ? this.selectedLocation.invert() : this.selectedLocation;
        if (absoluteLocation == null || controller.getLegalMoves().get(absoluteLocation) == null) {
            return;
        }
        Set<Move> moves = controller.getLegalMoves().get(absoluteLocation);
        for (Move move : moves) {
            Location targetLocation = move.getNewLocation().invert(isInverted());

            int circleSize;
            if (move.isCapture()) {
                g2d.setColor(Color.RED);
                circleSize = (int) (getTileSize() * 0.1);
            }
            else {
                g2d.setColor(Color.GRAY.brighter());
                circleSize = (int) (getTileSize() * 0.3);
            }
            int r = targetLocation.getRow();
            int c = targetLocation.getCol();
            g2d.fillOval(c * getTileSize() + circleSize, r * getTileSize() + circleSize, getTileSize() - 2 * circleSize, getTileSize() - 2 * circleSize);
        }
    }

    /**
     * draw the last move tiles with focus
     * @param g2d graphics
     */
    private void drawLastMove(Graphics2D g2d) {
        Move lastMove = controller.getLastMove();

        if (lastMove == null) return;

        drawTileWithFocus(g2d, lastMove.getOldLocation(), isInverted());
        drawTileWithFocus(g2d, lastMove.getNewLocation(), isInverted());
    }

    /**
     * draws an empty board
     * @param g2d graphics
     */
    private void drawBoard(Graphics2D g2d) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                g2d.setColor((r+c) % 2 == 0 ? LIGHT_TILE : DARK_TILE);
                g2d.fillRect(c * getTileSize(), r * getTileSize(), getTileSize(), getTileSize());
            }
        }
    }

    /**
     * draw the current pieces on board
     * @param g2s graphics
     */
    private void drawPieces(Graphics2D g2s) {
        synchronized (pieceViews) {
            for (PieceView piece : pieceViews) {
                piece.paint(g2s, getTileSize());
            }
        }
        if (selectedPiece != null) {
            selectedPiece.paint(g2s, getTileSize());
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
        g2d.fillRect(c * getTileSize(), r * getTileSize(), getTileSize(), getTileSize());
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
     * @throws InterruptedException board interrupted while requesting move
     */
    public Move requireMove(PieceColor turn) throws InterruptedException {
        this.turnColor = turn;

        try {
            moveSemaphore.acquire();
            this.turnColor = null;
            //TODO CHECK IF NECESSARY
            /*
            if (lastMove != null) {
                this.legalMoves.clear();
            }*/
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
        synchronized (pieceViews) {
            PieceView pieceView = pieceViews.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().orElse(null);
            pieceViews.remove(pieceView);
        }
    }

    /**
     * Returns tile size calculated based on screen size
     * @return tile size
     */
    private int getTileSize() {
        if (getParent() != null) {
            Dimension parentDimension = getSize();
            int boardSize = Math.min(parentDimension.width, parentDimension.height);
            return boardSize / 8;
        }
        else {
            return TILE_SIZE;
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
        Location currentLocation = PieceView.pointerToLocation(e, getTileSize(), isInverted());
        selectedLocation = PieceView.pointerToLocation(e, getTileSize());
        pieceViews.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().ifPresent(currentPiece -> this.selectedPiece = currentPiece);

        repaint();
    }

    /**
     * release the grabbed piece, if move is illegal
     * release the move semaphore if a move is made
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        Location currentLocation = PieceView.pointerToLocation(e, getTileSize(), isInverted());
        selectedLocation = null;
        if (currentLocation != null && selectedPiece != null) {

            Location lastLocation = selectedPiece.getLocation();
            Move move = new Move(lastLocation, currentLocation);
            // selected my pieces and valid position
            //add legalMoves.get(lastLocation).contains(currentLocation)
            if (selectedPiece.sameColor(turnColor) && controller.getLegalMoves().get(lastLocation).contains(move)) {
                //removePieceAt(currentLocation);
                //selectedPiece.setLocation(currentLocation);
                for (Move tempMove : controller.getLegalMoves().get(lastLocation)) {
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

            selectedPiece.resetScreenLocation(isInverted());
            selectedPiece = null;
        }
        else if (currentLocation == null && selectedPiece != null) {
            selectedPiece.resetScreenLocation(isInverted());
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
        focusedLocation = PieceView.pointerToLocation(e, getTileSize());
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
            selectedPiece.resetScreenLocation(isInverted());
        }
        repaint();
    }

    /**
     * handle dragging of a selected peice
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        focusedLocation = PieceView.pointerToLocation(e, getTileSize());
        if (selectedPiece == null) return;
        selectedPiece.setScreenLocation(e, getTileSize());
        repaint();
    }

    /**
     * handle the focus of tiles
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectedPiece != null) return;
        focusedLocation = PieceView.pointerToLocation(e, getTileSize());
        repaint();
    }

}