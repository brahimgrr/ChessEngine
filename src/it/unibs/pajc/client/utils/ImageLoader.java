package it.unibs.pajc.client.utils;

import it.unibs.pajc.game.model.enums.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Util class to handle piece image loading
 */
public class ImageLoader {
    private static BufferedImage sheet;
    static {
        try {
            //Image containing all pieces
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/pieces.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //map containing all piece images
    public static final HashMap<PieceType, Image> pieceImages = new HashMap<>();
    static {
        int sheetScale = sheet.getWidth() / 6;

        pieceImages.put(PieceType.KING_BLACK, sheet.getSubimage(0 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.KING_WHITE, sheet.getSubimage(0 * sheetScale, 0, sheetScale, sheetScale));

        pieceImages.put(PieceType.QUEEN_BLACK, sheet.getSubimage(1 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.QUEEN_WHITE, sheet.getSubimage(1 * sheetScale, 0, sheetScale, sheetScale));

        pieceImages.put(PieceType.ROOK_BLACK, sheet.getSubimage(4 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.ROOK_WHITE, sheet.getSubimage(4 * sheetScale, 0, sheetScale, sheetScale));

        pieceImages.put(PieceType.KNIGHT_BLACK, sheet.getSubimage(3 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.KNIGHT_WHITE, sheet.getSubimage(3 * sheetScale, 0, sheetScale, sheetScale));

        pieceImages.put(PieceType.BISHOP_BLACK, sheet.getSubimage(2 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.BISHOP_WHITE, sheet.getSubimage(2 * sheetScale, 0, sheetScale, sheetScale));

        pieceImages.put(PieceType.PAWN_BLACK, sheet.getSubimage(5 * sheetScale, sheetScale, sheetScale, sheetScale));
        pieceImages.put(PieceType.PAWN_WHITE, sheet.getSubimage(5 * sheetScale, 0, sheetScale, sheetScale));
    }
}
