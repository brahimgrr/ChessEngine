package it.unibs.pajc.client.view;

import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.client.model.ScreenLocation;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.game.model.enums.PieceType;
import it.unibs.pajc.client.utils.Constants;
import it.unibs.pajc.client.utils.ImageLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PieceView {
    private final PieceColor pieceColor;
    private final Image pieceImage;
    private Location location;
    private ScreenLocation screenLocation;
    private final boolean inverted;

    public PieceView(PieceType type, Location location, boolean inverted) {
        this.location = location;
        this.inverted = inverted;
        this.pieceColor = type.color;
        this.pieceImage = ImageLoader.pieceImages.get(type);
        this.screenLocation = locationToScreenLocation(location, inverted);
    }

    public static ScreenLocation locationToScreenLocation(Location location) {
        int xPos = location.getCol() * Constants.TILE_SIZE;
        int yPos = location.getRow() * Constants.TILE_SIZE;
        return new ScreenLocation(xPos, yPos);
    }

    public static ScreenLocation locationToScreenLocation(Location location, boolean inverted) {
        if (inverted) {
            return locationToScreenLocation(location.invert());
        }
        else {
            return locationToScreenLocation(location);
        }
    }

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

    public void setScreenLocation(MouseEvent e) {
        int xPos = e.getX() - (Constants.TILE_SIZE / 2);
        int yPos = e.getY() - (Constants.TILE_SIZE / 2);
        this.screenLocation = new ScreenLocation(xPos, yPos);
    }

    public void resetScreenLocation() {
        this.screenLocation = locationToScreenLocation(location, inverted);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public boolean sameColor(PieceColor pieceColor) {
        return this.pieceColor.equals(pieceColor);
    }

    public void paint(Graphics2D g2d) {
        int xPos = (int) screenLocation.getX();
        int yPos = (int) screenLocation.getY();
        Image scaledPieceImage = pieceImage.getScaledInstance(Constants.TILE_SIZE, Constants.TILE_SIZE, BufferedImage.SCALE_SMOOTH);
        g2d.drawImage(scaledPieceImage, xPos, yPos, null);
    }
}
