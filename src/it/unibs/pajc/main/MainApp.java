package it.unibs.pajc.main;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private final Board board;

    public MainApp() {
        board = new Board();
        initiateFrame();
    }

    private void initiateFrame() {
        setMinimumSize(new Dimension(800,800));
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.black);
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(board);

    }

    public static void main(String[] args) {
        MainApp frame = new MainApp();
        frame.setVisible(true);
    }
}
