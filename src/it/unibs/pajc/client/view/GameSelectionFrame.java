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

import static it.unibs.pajc.client.utils.Constants.MIN_HEIGHT;
import static it.unibs.pajc.client.utils.Constants.MIN_WIDTH;

/**
 * Frame to handle game selection
 */
public class GameSelectionFrame extends JFrame {
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JRadioButton whiteBot;
    private JRadioButton blackBot;
    private ButtonGroup onlineColorGroup;
    private JRadioButton whiteOnline;
    private JRadioButton blackOnline;

    private static int gameCounter = 0;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public GameSelectionFrame() {
        setTitle("Chess");
        setSize(420, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(100,100);

        ImageIcon icon = new ImageIcon("res/icon.png"); // Replace with your icon path
        setIconImage(icon.getImage());

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        contentPane.add(createLocalGamePanel());
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(createBotGamePanel());
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(createOnlineGamePanel());

        setContentPane(contentPane);
    }

    private JPanel createLocalGamePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🎮 Play Local Game"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Inner panel to center-align buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playButton = new JButton("Play Local Game");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> startLocalGame());

        JButton watchButton = new JButton("Watch Local Bot Game");
        watchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        watchButton.addActionListener(e -> startLocalBotGame());

        buttonPanel.add(playButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(watchButton);

        panel.add(Box.createVerticalGlue());
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }


    private JPanel createBotGamePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🤖 Play Against Bot"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        whiteBot = new JRadioButton("White", true);
        blackBot = new JRadioButton("Black");

        ButtonGroup group = new ButtonGroup();
        group.add(whiteBot);
        group.add(blackBot);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(whiteBot);
        radioPanel.add(blackBot);

        JButton startBotButton = new JButton("Start Bot Game");
        startBotButton.addActionListener(e -> startOnlineGame(true, null));

        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(startBotButton);

        return panel;
    }

    private JPanel createOnlineGamePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🌐 Play Online"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        serverIpField = new JTextField("localhost", 10);
        serverPortField = new JTextField("12345", 5);

        whiteOnline = new JRadioButton("White", true);
        blackOnline = new JRadioButton("Black");

        onlineColorGroup = new ButtonGroup();
        onlineColorGroup.add(whiteOnline);
        onlineColorGroup.add(blackOnline);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1;
        panel.add(serverIpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Server Port:"), gbc);
        gbc.gridx = 1;
        panel.add(serverPortField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(whiteOnline, gbc);
        gbc.gridx = 1;
        panel.add(blackOnline, gbc);

        JButton startOnline = new JButton("Start Online Game");
        startOnline.addActionListener(e -> startOnlineGame(false, null));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(startOnline, gbc);

        JButton startMultipleOnline = new JButton("Start Multiple Games");
        startMultipleOnline.addActionListener(e -> startMultipleOnlineGames());

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(startMultipleOnline, gbc);

        return panel;
    }

    private void startLocalGame() {
        //JOptionPane.showMessageDialog(this, "Starting Local Game...");

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

    private void startMultipleOnlineGames() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                onlineColorGroup.clearSelection();
                if (j == 0) {
                    whiteOnline.setSelected(true);
                }
                else {
                    blackOnline.setSelected(true);
                }

                Point screenLocation = new Point(i * MIN_WIDTH, j * MIN_HEIGHT);

                startOnlineGame(false, screenLocation);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void startOnlineGame(boolean requireBot, Point screenLocation) {
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
        //JOptionPane.showMessageDialog(this, "Connecting to " + ip + ":" + port + " as " + color);
        PieceColor playerColor = color.equals("White") ? PieceColor.WHITE : PieceColor.BLACK;

        BoardController boardController = new BoardController(screenLocation);
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
