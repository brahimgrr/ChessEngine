package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.controller.GuiPlayer;
import it.unibs.pajc.client.controller.RemoteGameController;
import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.model.enums.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameSelectionFrame extends JFrame {
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JRadioButton whiteBot;
    private JRadioButton blackBot;
    private JRadioButton whiteOnline;
    private JRadioButton blackOnline;

    private static int gameCounter = 0;

    private static PieceColor pieceColor = PieceColor.WHITE;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public GameSelectionFrame() {
        setTitle("Game Selection");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        setLocation(100,100);

        // Local Game Button
        JPanel localGamePanel = new JPanel();
        localGamePanel.setBorder(BorderFactory.createTitledBorder("Play local Game"));
        JButton localGameButton = new JButton("Play Local Game");
        localGameButton.addActionListener(e -> startLocalGame());
        localGamePanel.add(localGameButton);
        add(localGamePanel);

        // Bot Game Panel
        JPanel botGamePanel = new JPanel();
        botGamePanel.setBorder(BorderFactory.createTitledBorder("Play Against Bot"));
        whiteBot = new JRadioButton("White", true);
        blackBot = new JRadioButton("Black");
        ButtonGroup botGroup = new ButtonGroup();
        botGroup.add(whiteBot);
        botGroup.add(blackBot);
        JButton botGameButton = new JButton("Start Bot Game");
        botGameButton.addActionListener(e -> startBotGame());
        botGamePanel.add(whiteBot);
        botGamePanel.add(blackBot);
        botGamePanel.add(botGameButton);
        add(botGamePanel);
        JPanel onlineGamePanel = new JPanel();
        onlineGamePanel.setBorder(BorderFactory.createTitledBorder("Play Online"));
        onlineGamePanel.setLayout(new GridLayout(5, 2));
        onlineGamePanel.add(new JLabel("Server IP:"));
        serverIpField = new JTextField("localhost",10);
        onlineGamePanel.add(serverIpField);
        onlineGamePanel.add(new JLabel("Server Port:"));
        serverPortField = new JTextField("12345",5);
        onlineGamePanel.add(serverPortField);
        whiteOnline = new JRadioButton("White", true);
        blackOnline = new JRadioButton("Black");
        ButtonGroup onlineGroup = new ButtonGroup();
        onlineGroup.add(whiteOnline);
        onlineGroup.add(blackOnline);
        onlineGamePanel.add(whiteOnline);
        onlineGamePanel.add(blackOnline);
        JButton onlineGameButton = new JButton("Start Online Game");
        onlineGameButton.addActionListener(e -> startOnlineGame());
        onlineGamePanel.add(new JLabel()); // Placeholder for alignment
        onlineGamePanel.add(onlineGameButton);
        add(onlineGamePanel);
        //add(onlineGameButton);
    }

    private void startLocalGame() {
        JOptionPane.showMessageDialog(this, "Starting Local Game...");

        BoardController boardController = new BoardController();
        GuiPlayer white = new GuiPlayer(PieceColor.WHITE, boardController);
        GuiPlayer black = new GuiPlayer(PieceColor.BLACK, boardController, true);

        GameController gameController = new GameController(gameCounter, white, black);
        executor.execute(gameController);
        boardController.setOnCloseOperation(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                gameController.stopGame();
            }
        });

        printExecutorState();

        gameCounter++;
    }

    private void startBotGame() {
        String color = whiteBot.isSelected() ? "White" : "Black";
        JOptionPane.showMessageDialog(this, "Starting Bot Game as " + color);
        // Logic to start bot game
    }

    private void startOnlineGame() {
        String ip = serverIpField.getText();
        String portString = serverPortField.getText();
        int port = Integer.parseInt(portString);
        String color = whiteOnline.isSelected() ? "White" : "Black";
        JOptionPane.showMessageDialog(this, "Connecting to " + ip + ":" + port + " as " + color);
        PieceColor playerColor = color.equals("White") ? PieceColor.WHITE : PieceColor.BLACK;

        BoardController boardController = new BoardController();
        GuiPlayer guiPlayer = new GuiPlayer(playerColor, boardController);
        //pieceColor = pieceColor.getOpposite();

        RemoteGameController remoteGameController = new RemoteGameController(gameCounter, guiPlayer, ip, port);
        executor.execute(remoteGameController);
        boardController.setOnCloseOperation(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                remoteGameController.stopGame();
            }
        });

        printExecutorState();

        gameCounter++;
    }

    private static void printExecutorState() {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        System.out.println();
        System.out.println(" - Executor pool state -");
        System.out.println("Active count: " + threadPoolExecutor.getActiveCount());
        System.out.println("Completed task: " + threadPoolExecutor.getCompletedTaskCount());
        System.out.println("Pool size: " + threadPoolExecutor.getPoolSize());
        System.out.println();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSelectionFrame frame = new GameSelectionFrame();
            frame.setVisible(true);
        });
    }
}
