package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Frame containing the chessboard view
 */
public class MainFrame extends JFrame {
    private final ChessBoardView chessBoardView;
    private final JPanel statusPanel;

    public MainFrame(BoardController boardController) {
        this.chessBoardView = new ChessBoardView(boardController);
        this.statusPanel = new JPanel();
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

        // Game status panel (Left)
        statusPanel.setPreferredSize(new Dimension(200, 600)); // Fixed width
        statusPanel.setBackground(Color.GRAY);
        statusPanel.add(new JLabel("Game Status"));

        // Chessboard Wrapper (Ensuring Square Shape)
        JPanel chessBoardWrapper = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                int availableWidth = (int) (getParent().getWidth() * 0.80);
                int availableHeight = getParent().getHeight();
                int size = Math.min(availableWidth, availableHeight); // Keep square
                return new Dimension(size, size);
            }

        };
        chessBoardWrapper.setBackground(Color.white);

        // Chessboard View (Placeholder)
        chessBoardWrapper.setBackground(Color.BLACK);

        chessBoardWrapper.add(chessBoardView);

        // Resizing logic to keep chessboard square
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFrame(chessBoardWrapper, statusPanel);
            }
        });

        // Adding components
        container.add(statusPanel, BorderLayout.EAST);  // Game status on the left
        container.add(chessBoardWrapper, BorderLayout.CENTER);  // Chessboard centered

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
    /**
     * returns a reference to the chessboard view
     * @return chessboard view
     */
    public ChessBoardView getChessBoardView() {
        return (ChessBoardView) chessBoardView;
    }

    public JPanel getStatusPanel() {
        return statusPanel;
    }
}
