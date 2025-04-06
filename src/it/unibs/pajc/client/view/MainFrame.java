package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Frame containing the chessboard view
 */
public class MainFrame extends JFrame {
    private final ChessBoardView chessBoardView;
    private final JPanel statusPanel;
    private final JLabel currentTurnLabel;
    private final JTextArea movesHistoryArea;
    private final JPanel capturedPiecesPanel;
    private final LinkedList<String> movesHistory;

    public MainFrame(BoardController boardController) {
        this.chessBoardView = new ChessBoardView(boardController);
        this.statusPanel = new JPanel(new BorderLayout());
        this.currentTurnLabel = new JLabel("Current Turn: White", SwingConstants.CENTER);
        this.movesHistoryArea = new JTextArea(10, 15);
        this.capturedPiecesPanel = new JPanel();
        this.movesHistory = new LinkedList<>();
        initialize();
    }

    private void initialize() {
        this.setMinimumSize(new Dimension(800, 600));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle("Chess");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(800, 600);

        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.BLACK);

        // Game status panel (Right)
        statusPanel.setPreferredSize(new Dimension(200, 600));
        statusPanel.setBackground(Color.GRAY);

        // Current Turn Indicator
        currentTurnLabel.setOpaque(true);
        currentTurnLabel.setBackground(Color.WHITE);
        currentTurnLabel.setForeground(Color.BLACK);
        statusPanel.add(currentTurnLabel, BorderLayout.NORTH);

        // Move History
        movesHistoryArea.setEditable(false);
        JScrollPane movesScrollPane = new JScrollPane(movesHistoryArea);
        movesScrollPane.setBorder(BorderFactory.createTitledBorder("Moves History"));
        statusPanel.add(movesScrollPane, BorderLayout.CENTER);

        // Captured Pieces Panel
        capturedPiecesPanel.setLayout(new FlowLayout());
        capturedPiecesPanel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));
        statusPanel.add(capturedPiecesPanel, BorderLayout.SOUTH);

        // Chessboard Wrapper
        JPanel chessBoardWrapper = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                int availableWidth = (int) (getParent().getWidth() * 0.80);
                int availableHeight = getParent().getHeight();
                int size = Math.min(availableWidth, availableHeight);
                return new Dimension(size, size);
            }
        };
        chessBoardWrapper.setBackground(Color.BLACK);
        chessBoardWrapper.add(chessBoardView);

        // Resizing logic
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFrame(chessBoardWrapper, statusPanel);
            }
        });

        // Adding components
        container.add(statusPanel, BorderLayout.EAST);
        container.add(chessBoardWrapper, BorderLayout.CENTER);

        this.add(container);
    }

    private void resizeFrame(JPanel chessBoardWrapper, JPanel statusPanel) {
        int availableWidth = (int) (getContentPane().getWidth() * 0.80);
        int complementWidth = getContentPane().getWidth() - availableWidth;
        int availableHeight = getContentPane().getHeight();
        int size = Math.min(availableWidth, availableHeight);

        chessBoardWrapper.setPreferredSize(new Dimension(availableWidth, availableHeight));
        int chessboardSize = (int) (size * 0.90) / 8;
        chessBoardView.setPreferredSize(new Dimension(chessboardSize * 8, chessboardSize * 8));
        statusPanel.setPreferredSize(new Dimension(complementWidth, availableHeight));
        statusPanel.revalidate();
        chessBoardWrapper.revalidate();
        statusPanel.repaint();
        chessBoardWrapper.repaint();
    }

    public void updateTurn(boolean isWhiteTurn) {
        currentTurnLabel.setText("Current Turn: " + (isWhiteTurn ? "White" : "Black"));
        currentTurnLabel.setBackground(isWhiteTurn ? Color.WHITE : Color.BLACK);
        currentTurnLabel.setForeground(isWhiteTurn ? Color.BLACK : Color.WHITE);
    }

    public void addMove(String move) {
        movesHistory.addFirst(move);
        if (movesHistory.size() > 10) movesHistory.removeLast();
        movesHistoryArea.setText(String.join("\n", movesHistory));
    }

    public void addCapturedPiece(ImageIcon piece) {
        JLabel pieceLabel = new JLabel(piece);
        capturedPiecesPanel.add(pieceLabel);
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }

    public ChessBoardView getChessBoardView() {
        return chessBoardView;
    }

    public JPanel getStatusPanel() {
        return statusPanel;
    }
}