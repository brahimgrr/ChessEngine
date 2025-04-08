package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.game.model.enums.PieceColor;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StatusView extends JPanel {
    private final JTextArea movesHistoryArea;
    private final JPanel capturedPiecesPanel;
    private final BoardController controller;

    public StatusView(BoardController boardController) {
        super(new BorderLayout());
        this.controller = boardController;

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        this.movesHistoryArea = new JTextArea(5, 15);

        DefaultCaret caret = (DefaultCaret)this.movesHistoryArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.capturedPiecesPanel = new JPanel();

        // Game status panel (Right)
        this.setPreferredSize(new Dimension(200, 600));
        //this.setBackground(Color.GRAY);

        // Move History
        movesHistoryArea.setEditable(false);
        movesHistoryArea.setFocusable(false);

        JScrollPane movesScrollPane = new JScrollPane(
                movesHistoryArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        movesScrollPane.setBorder(BorderFactory.createTitledBorder("Game History"));
        this.add(movesScrollPane, BorderLayout.CENTER);


        // Captured Pieces Panel
        capturedPiecesPanel.setLayout(new GridLayout(4, 8, 5, 5));
        capturedPiecesPanel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));
        this.add(capturedPiecesPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        movesHistoryArea.setText(String.join("\n", controller.getMovesHistory()));

        capturedPiecesPanel.removeAll();
        int width = capturedPiecesPanel.getWidth() / 8;  // Adjust this divisor as needed
        int height = capturedPiecesPanel.getHeight() / 2; // Adjust based on your layout

        for (List<Image> imgs : controller.getCaptures().values()) {
            for (Image img : imgs) {
                int dim = Math.min(width, height);
                Image scaledPiece = img.getScaledInstance(dim, dim, Image.SCALE_SMOOTH);
                JLabel pieceLabel = new JLabel(new ImageIcon(scaledPiece));
                capturedPiecesPanel.add(pieceLabel);
            }
        }
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }


    public void addMove(String move) {
        controller.getMovesHistory().addLast(move);
        //if (movesHistory.size() > 10) movesHistory.removeLast();
        movesHistoryArea.setText(String.join("\n", controller.getMovesHistory()));
    }

    public void addCapturedPiece(Image piece) {
        int width = capturedPiecesPanel.getWidth() / 8;  // Adjust this divisor as needed
        int height = capturedPiecesPanel.getHeight() / 2; // Adjust based on your layout

        Image scaledPiece = piece.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel pieceLabel = new JLabel(new ImageIcon(scaledPiece));
        capturedPiecesPanel.add(pieceLabel);
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }
}
