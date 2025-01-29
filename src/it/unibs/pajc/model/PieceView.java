package it.unibs.pajc.model;

import it.unibs.pajc.main.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PieceView {
    //Position
    public int col, row;
    public int xPos, yPos;
    public boolean isWhite;
    public String name;
    public int value;
    public int sheetPosition;

    protected static BufferedImage sheet;
    {
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/pieces.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected int sheetScale = sheet.getWidth()/6;

    private Image pieceImage;


    public Piece(Board board, int col, int row, int sheetPosition, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.name = this.getClass().getSimpleName();
        this.isWhite = isWhite;
        this.sheetPosition = sheetPosition;


        this.pieceImage = sheet.getSubimage(sheetPosition * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale);
    }

    public void paint(Board board, Graphics2D g2d) {
    	int tileSize = board.getTileSize();
    	this.xPos = col * tileSize;
        this.yPos = row * tileSize;
        Image scaledPieceImage = pieceImage.getScaledInstance(tileSize, tileSize, BufferedImage.SCALE_SMOOTH);
        g2d.drawImage(scaledPieceImage, xPos, yPos, null);
    }
}
