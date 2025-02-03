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

public class ChessBoardView extends JPanel implements MouseMotionListener, MouseListener {
    //private final CommunicationSemaphore<Move> moveSemaphore = new CommunicationSemaphore<>(0);
    private final Semaphore moveSemaphore = new Semaphore(0);
    private PieceColor turnColor;
    private boolean invertBoard = false;

    private Location selectedLocation = null;
    private Location focusedLocation = null;

    private Move lastMove = null;

    private List<PieceView> pieces;
    private PieceView selectedPiece = null;

    private Map<Location, Set<Move>> legalMoves;

    public ChessBoardView() {
        this.setPreferredSize(new Dimension(CHESSBOARD_SIZE, CHESSBOARD_SIZE));
        this.pieces = new ArrayList<>();
        this.legalMoves = new HashMap<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void setInverted(boolean invertBoard) {
        this.invertBoard = invertBoard;
    }

    public void setPieces(List<PieceView> pieces) {
        this.pieces = pieces;

        repaint();
    }

    public void setPieces(String fenString) {
        System.out.println("VIEW SET-PIECES THREAD: " + Thread.currentThread().getName());
        this.pieces.clear();
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
                    pieces.add(pieceView);
                    col += 1;
                }
            }
        }
        System.out.println("Board SET");
        repaint();
    }

    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        this.legalMoves = new HashMap<>(legalMoves);
    }

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

    private void drawMoveTiles(Graphics2D g2d, Move lastMove) {
        if (lastMove == null) return;

        drawTileWithFocus(g2d, lastMove.getOldLocation(), invertBoard);
        drawTileWithFocus(g2d, lastMove.getNewLocation(), invertBoard);
    }

    private void drawBoard(Graphics2D g2d) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                g2d.setColor((r+c) % 2 == 0 ? LIGHT_TILE : DARK_TILE);
                g2d.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public void check(boolean check) {
        if (check) {
            SoundLoader.MOVE_CHECK.play();
        }
    }

    private void drawPieces(Graphics2D g2s) {
        for (PieceView piece : pieces) {
            piece.paint(g2s);
        }
        if (selectedPiece != null) {
            selectedPiece.paint(g2s);
        }
    }

    private void drawTileWithFocus(Graphics2D g2d, Location location) {
        if (location == null) return;

        int r = location.getRow();
        int c = location.getCol();
        g2d.setColor((r+c) % 2 == 0 ? DARK_FOCUSED_TILE : LIGHT_FOCUSED_TILE);
        g2d.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private void drawTileWithFocus(Graphics2D g2d, Location location, boolean invertBoard) {
        if (invertBoard) {
            drawTileWithFocus(g2d, location.invert());
        }
        else {
            drawTileWithFocus(g2d, location);
        }
    }

    public Move requireMove(PieceColor turn) throws InterruptedException {
        this.turnColor = turn;

        try {
            System.out.println("Acquired Thread: " + Thread.currentThread().getName());
            //moveSemaphore.r_acquire();
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

    public void resetView() {
        selectedLocation = null;
        focusedLocation = null;

        lastMove = null;

        pieces.clear();
        selectedPiece = null;
        legalMoves.clear();

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Location currentLocation = PieceView.pointerToLocation(e, invertBoard);
        selectedLocation = PieceView.pointerToLocation(e);
        pieces.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().ifPresent(currentPiece -> this.selectedPiece = currentPiece);

        repaint();
    }

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
                lastMove = move;
                removePieceAt(currentLocation);
                selectedPiece.setLocation(currentLocation);
                for (Move tempMove : legalMoves.get(lastLocation)) {
                    if (tempMove.equals(move)) {
                        move = tempMove;
                        break;
                    }
                }
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

    private void removePieceAt(Location currentLocation) {
        PieceView pieceView = pieces.stream().filter(p -> p.getLocation().equals(currentLocation)).findFirst().orElse(null);
        pieces.remove(pieceView);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        focusedLocation = PieceView.pointerToLocation(e);
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        focusedLocation = null;
        if (selectedPiece != null) {
            selectedPiece.resetScreenLocation();
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        focusedLocation = PieceView.pointerToLocation(e);
        if (selectedPiece == null) return;
        selectedPiece.setScreenLocation(e);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectedPiece != null) return;
        focusedLocation = PieceView.pointerToLocation(e);
        repaint();
    }
}