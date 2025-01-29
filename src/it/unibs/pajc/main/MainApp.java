package it.unibs.pajc.main;

import it.unibs.pajc.model.Chess;
import utils.ChessSound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainApp {
    private JFrame frame;
    private Chess model;

    public MainApp() {
        model = new Chess();
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(1000, 800)); // Increased width to fit extra components
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setTitle("Chess");
        ImageIcon icon = new ImageIcon("icon.png");
        Image image = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        frame.setIconImage(image);
        System.out.println(image);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main container with black background
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.BLACK);

        // Chessboard
        Board board = new Board();
        container.add(board, BorderLayout.CENTER);

        // Sidebar Panel (For extra components)
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
        		board.showPieces(false);
        		ChessSound.MOVE.play();
        	}
        });
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(20)); // Add spacing at the top
        sidebar.add(label);
        sidebar.add(Box.createVerticalStrut(10)); // Spacing
        sidebar.add(restartButton);

        container.add(sidebar, BorderLayout.EAST); // Add sidebar to the right

        frame.getContentPane().add(container, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainApp window = new MainApp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
