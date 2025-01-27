package it.unibs.pajc.pieces;

import it.unibs.pajc.main.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Piece {
    //Position
    public int col, row;
    public int xPos, yPos;

    public boolean isWhite;
    public String name;
    public int value;

    protected BufferedImage sheet;
    {
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("pieces.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected int sheetScale = sheet.getWidth()/6;

    private final Image pieceImage;

    private Board board;

    public Piece(Board board, int col, int row, int sheetPosition, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
        this.xPos = col * Board.tileSize;
        this.yPos = row * Board.tileSize;
        this.name = this.getClass().getSimpleName();
        this.pieceImage = sheet.getSubimage(sheetPosition * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(Board.tileSize, Board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    public void paint(Graphics2D g2d) {
        g2d.drawImage(pieceImage, xPos, yPos, null);
    }
}
