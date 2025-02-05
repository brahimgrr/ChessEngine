package it.unibs.pajc.client.view;

import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.client.utils.Constants;
import it.unibs.pajc.client.utils.ImageLoader;

import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Class representing the view of a piece
 */
public class PieceView {
    private final PieceColor pieceColor;
    private final Image pieceImage;
    private Location location;
    private Point screenLocation;
    private final boolean inverted;

    /**
     * Piece constructor
     * @param type piece type
     * @param location piece location
     * @param inverted flag if board is inverted
     */
    public PieceView(PieceType type, Location location, boolean inverted) {
        //absolute location on board
        this.location = location;
        this.inverted = inverted;
        this.pieceColor = type.color;
        this.pieceImage = ImageLoader.pieceImages.get(type);
        //location on screen
        this.screenLocation = locationToPoint(location, inverted);
    }

    /**
     * converts a location to a screen location
     * @param location location
     * @return screen location
     */
    public static Point locationToPoint(Location location) {
        int xPos = location.getCol() * Constants.TILE_SIZE;
        int yPos = location.getRow() * Constants.TILE_SIZE;
        return new Point(xPos, yPos);
    }

    /**
     * converts a location to a screen location
     * @param location location
     * @param inverted flag for inverted board
     * @return screen location
     */
    public static Point locationToPoint(Location location, boolean inverted) {
        if (inverted) {
            return locationToPoint(location.invert());
        }
        else {
            return locationToPoint(location);
        }
    }

    /**
     * converts a muove event to a board location
     * @param e mouse event
     * @return absolute location on board
     */
    public static Location pointerToLocation(MouseEvent e) {
        int col = e.getX() / Constants.TILE_SIZE;
        int row = e.getY() / Constants.TILE_SIZE;

        if (Location.isOutOfBounds(col, row)) {
            return null;
        }
        else {
            return new Location(row, col);
        }
    }

    /**
     * converts a muove event to a board location
     * @param e mouse event
     * @param inverted flag for inverted board
     * @return absolute location on board
     */
    public static Location pointerToLocation(MouseEvent e, boolean inverted) {
        if (inverted) {
            Location location = pointerToLocation(e);
            if (location != null) {
                return location.invert();
            }
            else {
                return null;
            }
        }
        else {
            return pointerToLocation(e);
        }
    }

    /**
     * set the piece screen location
     * @param e mouse event
     */
    public void setScreenLocation(MouseEvent e) {
        int xPos = e.getX() - (Constants.TILE_SIZE / 2);
        int yPos = e.getY() - (Constants.TILE_SIZE / 2);
        this.screenLocation = new Point(xPos, yPos);
    }

    /**
     * restore the piece location
     */
    public void resetScreenLocation() {
        this.screenLocation = locationToPoint(location, inverted);
    }

    /**
     * updated the piece location
     * @param location location to be set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * return the piece location
     * @return location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * checks if the piece has same color as query
     * @param pieceColor color query
     * @return a boolean representing if the piece has same color as query
     */
    public boolean sameColor(PieceColor pieceColor) {
        return this.pieceColor.equals(pieceColor);
    }

    /**
     * Paints the current piece
     * @param g2d graphics
     */
    public void paint(Graphics2D g2d) {
        int xPos = (int) screenLocation.getX();
        int yPos = (int) screenLocation.getY();
        Image scaledPieceImage = pieceImage.getScaledInstance(Constants.TILE_SIZE, Constants.TILE_SIZE, BufferedImage.SCALE_SMOOTH);
        g2d.drawImage(scaledPieceImage, xPos, yPos, null);
    }
}
