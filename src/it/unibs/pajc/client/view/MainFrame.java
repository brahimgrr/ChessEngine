package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.utils.Constants;
import it.unibs.pajc.game.model.enums.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Frame containing the chessboard view
 */
public class MainFrame extends JFrame {
    private final ChessBoardView chessBoardView;

    public MainFrame() {
        this.chessBoardView = new ChessBoardView();
        initialize();
    }

    private void initialize() {
        this.setMinimumSize(new Dimension(Constants.CHESSBOARD_SIZE, Constants.CHESSBOARD_SIZE)); // Increased width to fit extra components
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle("Chess");
        this.setResizable(true);
        ImageIcon icon = new ImageIcon("icon.png");
        Image image = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        this.setIconImage(image);
        System.out.println(image);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Main container with black background
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.BLACK);

        // Chessboard
        container.add(chessBoardView, null);

        this.getContentPane().add(container, BorderLayout.CENTER);
        this.pack();
    }

    /**
     * returns a reference to the chessboard view
     * @return chessboard view
     */
    public ChessBoardView getChessBoardView() {
        return chessBoardView;
    }
}
