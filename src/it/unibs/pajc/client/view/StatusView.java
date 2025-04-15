package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StatusView extends JPanel {
    private final BoardController controller;
    private final JTextArea movesHistoryArea;
    private final JPanel capturedPiecesPanel;

    public StatusView(BoardController boardController) {
        super(new BorderLayout());
        this.controller = boardController;

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        this.movesHistoryArea = new JTextArea(5, 15);

        DefaultCaret caret = (DefaultCaret)this.movesHistoryArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.capturedPiecesPanel = new JPanel();

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

        movesHistoryArea.setText(String.join("\n", controller.getTextMovesHistory()));

        capturedPiecesPanel.removeAll();

        int width = capturedPiecesPanel.getWidth() / 8;
        int height = capturedPiecesPanel.getHeight() / 2;

        for (List<PieceView> captureViewList : controller.getCaptures().values()) {
            for (PieceView captureView : captureViewList) {
                Image captureImg = captureView.getPieceImage();
                int dim = Math.min(width, height);
                Image scaledPiece = captureImg.getScaledInstance(dim, dim, Image.SCALE_SMOOTH);
                JLabel pieceLabel = new JLabel(new ImageIcon(scaledPiece));
                capturedPiecesPanel.add(pieceLabel);
            }
        }
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }
}
