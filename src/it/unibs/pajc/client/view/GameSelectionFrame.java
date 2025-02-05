package it.unibs.pajc.client.view;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.controller.GuiPlayer;
import it.unibs.pajc.client.controller.RemoteGameController;
import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.utils.NetworkConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Frame to handle game selection
 */
public class GameSelectionFrame extends JFrame {
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JRadioButton whiteBot;
    private JRadioButton blackBot;
    private JRadioButton whiteOnline;
    private JRadioButton blackOnline;

    private static int gameCounter = 0;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public GameSelectionFrame() {
        setTitle("Game Selection");
        setSize(400, 400 );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        setLocation(100,100);

        // Local Game Button
        JPanel localGamePanel = new JPanel();
        localGamePanel.setLayout(new BoxLayout(localGamePanel, BoxLayout.Y_AXIS)); // Vertical layout
        localGamePanel.setBorder(BorderFactory.createTitledBorder("Play Local Game")); // Add border
        localGamePanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment

        JButton localGameButton = new JButton("Play Local Game");
        localGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align button to center
        localGameButton.addActionListener(e -> startLocalGame());

        JButton localBotGameButton = new JButton("Watch Local Bot Game");
        localBotGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align button to center
        localBotGameButton.addActionListener(e -> startLocalBotGame());

        localGamePanel.add(Box.createVerticalStrut(5)); // Small gap at the top
        localGamePanel.add(localGameButton);
        localGamePanel.add(Box.createVerticalStrut(10)); // Space between buttons
        localGamePanel.add(localBotGameButton);
        localGamePanel.add(Box.createVerticalStrut(5)); // Small gap at the bottom

        add(localGamePanel);


        JPanel botGamePanel = new JPanel();
        botGamePanel.setLayout(new BoxLayout(botGamePanel, BoxLayout.Y_AXIS)); // Vertical layout
        botGamePanel.setBorder(BorderFactory.createTitledBorder("Play Against Bot"));

        whiteBot = new JRadioButton("White", true);
        blackBot = new JRadioButton("Black");
        ButtonGroup botGroup = new ButtonGroup();
        botGroup.add(whiteBot);
        botGroup.add(blackBot);

        JButton botGameButton = new JButton("Start Bot Game");
        botGameButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Ensures button doesn't expand
        botGameButton.addActionListener(e -> startOnlineGame(true));
        botGamePanel.add(whiteBot);
        botGamePanel.add(blackBot);
        botGamePanel.add(Box.createVerticalStrut(10)); // Space between radio buttons and button
        botGamePanel.add(botGameButton);

        add(botGamePanel);


        //Online
        JPanel onlineGamePanel = new JPanel();
        onlineGamePanel.setBorder(BorderFactory.createTitledBorder("Play Online"));
        onlineGamePanel.setLayout(new GridLayout(5, 2));
        onlineGamePanel.add(new JLabel("Server IP:"));
        serverIpField = new JTextField(NetworkConstants.SERVER_IP,10);
        onlineGamePanel.add(serverIpField);
        onlineGamePanel.add(new JLabel("Server Port:"));
        serverPortField = new JTextField(String.valueOf(NetworkConstants.SERVER_PORT),5);
        onlineGamePanel.add(serverPortField);
        whiteOnline = new JRadioButton("White", true);
        blackOnline = new JRadioButton("Black");
        ButtonGroup onlineGroup = new ButtonGroup();
        onlineGroup.add(whiteOnline);
        onlineGroup.add(blackOnline);
        onlineGamePanel.add(whiteOnline);
        onlineGamePanel.add(blackOnline);
        JButton onlineGameButton = new JButton("Start Online Game");
        onlineGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        onlineGameButton.addActionListener(e -> startOnlineGame(false));
        botGamePanel.add(Box.createVerticalStrut(10));
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

    private void startOnlineGame(boolean requireBot) {
        String ip = serverIpField.getText();
        String portString = serverPortField.getText();
        int port = Integer.parseInt(portString);
        String color;
        if (requireBot) {
            color = whiteBot.isSelected() ? "White" : "Black";
        }
        else {
            color = whiteOnline.isSelected() ? "White" : "Black";
        }
        JOptionPane.showMessageDialog(this, "Connecting to " + ip + ":" + port + " as " + color);
        PieceColor playerColor = color.equals("White") ? PieceColor.WHITE : PieceColor.BLACK;

        BoardController boardController = new BoardController();
        GuiPlayer guiPlayer = new GuiPlayer(playerColor, boardController);

        RemoteGameController remoteGameController = new RemoteGameController(gameCounter, guiPlayer, ip, port, requireBot);
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

    private void startLocalBotGame() {
        BoardController boardController = new BoardController();
        GuiPlayer guiPlayer = new GuiPlayer(PieceColor.WHITE, boardController);

        GameController gameController = new GameController(gameCounter, guiPlayer);
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

    private static void printExecutorState() {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        System.out.println();
        System.out.println(" - Executor pool state -");
        System.out.println("Active count: " + threadPoolExecutor.getActiveCount());
        System.out.println("Completed task: " + threadPoolExecutor.getCompletedTaskCount());
        System.out.println("Pool size: " + threadPoolExecutor.getPoolSize());
        System.out.println();
    }
}
