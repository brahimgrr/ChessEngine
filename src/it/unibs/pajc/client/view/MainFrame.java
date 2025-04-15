package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static it.unibs.pajc.client.utils.Constants.MIN_HEIGHT;
import static it.unibs.pajc.client.utils.Constants.MIN_WIDTH;

/**
 * Frame containing the chessboard view
 */
public class MainFrame extends JFrame {
    private final ChessBoardView chessBoardView;
    private final StatusView statusPanelView;

    public MainFrame(BoardController boardController, Point location) {
        this.chessBoardView = new ChessBoardView(boardController);
        this.statusPanelView = new StatusView(boardController);

        initialize(location);
    }

    private void initialize(Point location) {

        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.getContentPane().setLayout(new BorderLayout());
        //this.getContentPane().setBackground(Color.gray);
        this.setTitle("Chess");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (location != null) {
            this.setLocation(location);
        }
        else {
            this.setLocationRelativeTo(null);
        }

        this.setSize(MIN_WIDTH, MIN_HEIGHT);

        ImageIcon icon = new ImageIcon("res/icon.png"); // Replace with your icon path
        setIconImage(icon.getImage());

        // Main container
        JPanel container = new JPanel(new BorderLayout());
        //container.setBackground(Color.gray);

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
        //chessBoardWrapper.setBackground(Color.gray);
        chessBoardWrapper.add(chessBoardView);

        // Resizing logic
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFrame(chessBoardWrapper, statusPanelView);
            }
        });

        // Adding components
        container.add(statusPanelView, BorderLayout.EAST);
        container.add(chessBoardWrapper, BorderLayout.CENTER);

        this.add(container);
    }

    private void resizeFrame(JPanel chessBoardWrapper, JPanel statusPanel) {
        int availableWidth = (int) (getContentPane().getWidth() * 0.70);
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

    public ChessBoardView getChessBoardView() {
        return chessBoardView;
    }

    public StatusView getStatusPanelView() {
        return statusPanelView;
    }
}