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

public class MainFrame extends JFrame {
    private final ChessBoardView chessBoardView;

    public MainFrame(BoardController boardController) {
        this.chessBoardView = new ChessBoardView();
        initialize(boardController);
    }

    private void initialize(BoardController boardController) {
        this.setMinimumSize(new Dimension(Constants.CHESSBOARD_SIZE, Constants.CHESSBOARD_SIZE)); // Increased width to fit extra components
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle("Chess");
        this.setResizable(false);
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
        container.add(chessBoardView, BorderLayout.CENTER);

        this.getContentPane().add(container, BorderLayout.CENTER);
        this.pack();
    }

    public ChessBoardView getChessBoardView() {
        return chessBoardView;
    }
}



/* // Sidebar Panel (For extra components)
JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 800)); // Fixed width for sidebar
        sidebar.setBackground(Color.BLACK);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

// Example components in sidebar
JLabel label = new JLabel("Game Info");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

JButton restartButton = new JButton("Restart");
        restartButton.setForeground(new Color(255, 255, 255));
        restartButton.setBackground(new Color(118, 150, 86));
        restartButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        boardController.restart();
    }
});
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

JButton whiteButton = new JButton("White");
        whiteButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        boardController.setPlayerColor(PieceColor.WHITE);
    }
});
        whiteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

JButton blackButton = new JButton("Black");
        blackButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        boardController.setPlayerColor(PieceColor.BLACK);
    }
});
        blackButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(20)); // Add spacing at the top
        sidebar.add(label);
        sidebar.add(Box.createVerticalStrut(10)); // Spacing
        sidebar.add(restartButton);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(whiteButton);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(blackButton);

        container.add(sidebar, BorderLayout.EAST); // Add sidebar to the right */