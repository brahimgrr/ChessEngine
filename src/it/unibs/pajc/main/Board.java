package it.unibs.pajc.main;

import it.unibs.pajc.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Board extends JPanel {
    public static final int tileSize = 85;
    public static final int cols = 8;
    public static final int rows = 8;

    public final ArrayList<Piece> pieces;

    public Board() {
        this.setPreferredSize(new Dimension(tileSize * cols, tileSize * rows));
        this.pieces = new ArrayList<>();
        addPieces();
    }

    private void addPieces() {
        boolean isWhite = true;
        int pawnRow = 1;
        int pieceRow = 0;

        for (int i = 0; i < 2; i++) {
            pieces.add(new Knight(this, 1, pieceRow,isWhite));
            pieces.add(new Knight(this, 6, pieceRow,isWhite));

            pieces.add(new Rook(this, 0, pieceRow,isWhite));
            pieces.add(new Rook(this, 7, pieceRow,isWhite));

            pieces.add(new Bishop(this, 2, pieceRow,isWhite));
            pieces.add(new Bishop(this, 5, pieceRow,isWhite));

            pieces.add(new Queen(this, 3, pieceRow,isWhite));
            pieces.add(new King(this, 4, pieceRow,isWhite));

            for (int p = 0; p < 8; p++) {
                pieces.add(new Pawn(this, p, pawnRow,isWhite));
            }

            pieceRow = 7;
            pawnRow = 6;
            isWhite = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((r+c) % 2 == 0 ? new Color	(118,150,86) : new Color	(238,238,210));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        for (Piece p : pieces) {
            p.paint(g2d);
        }
    }
}
