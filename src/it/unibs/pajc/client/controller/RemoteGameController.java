package it.unibs.pajc.client.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.model.NetPacket;

import javax.swing.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Controller to handle a game with a remote opponent
 */
public class RemoteGameController implements Runnable {
    //game unique identifier
    private final int gameId;
    //gui board player
    private final GuiPlayer player;
    //server ip
    private final String ip;
    //server port
    private final int port;
    //connection port
    private Socket socket;
    //flag if user wants to play against a bot
    private final boolean requireBot;
    //thread on which the game is launched
    private Thread gameThread;
    //playing flag, used for threading
    private boolean isPlaying;
    //object data streams
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    /**
     * RemoteGameController constructor
     * @param gameId game identifier
     * @param guiPlayer gui board player
     * @param ip server ip
     * @param port server port
     * @param requireBot flag to request game against a bot
     */
    public RemoteGameController(int gameId, GuiPlayer guiPlayer, String ip, int port, boolean requireBot) {
        this.gameId = gameId;
        this.player = guiPlayer;
        this.ip = ip;
        this.port = port;
        this.requireBot = requireBot;
        this.isPlaying = true;
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        try {
            log("Launched!");
            socket = new Socket(ip, port);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();  // Ensure stream header is written
            inStream = new ObjectInputStream(socket.getInputStream());

            while (isPlaying) {
                gameLoop();
            }
        } catch (IOException | InterruptedException e) {
            log("Game interrupted!");
            player.setGameState(GameState.CONNECTION_LOST);
            player.terminate();
            Thread.currentThread().interrupt(); // Preserve the interrupt flag
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        log("Game loop terminated!");
    }

    /**
     * Main game loop
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void gameLoop() throws IOException, ClassNotFoundException, InterruptedException {
        Object obj = this.inStream.readObject();
        gameLog(player.getColor() + " " + "PACKET RECEIVED");
        if (! (obj instanceof NetPacket packet)) {
            return;
        }
        switch (packet.type) {
            case NetPacket.SET_POSITION_FEN -> {
                gameLog("Set position fen");
                String fen = (String) packet.data;
                player.setPosition(fen);
            }
            case NetPacket.SET_LEGAL_MOVES -> {
                gameLog("Set legal moves");
                MoveMap moveMap = (MoveMap) packet.data;
                player.setLegalMoves(moveMap);
            }
            case NetPacket.SET_LAST_MOVE -> {
                gameLog("Set last move");
                Move move = (Move) packet.data;
                player.setLastMove(move);
            }
            case NetPacket.REQUEST_MOVE -> {
                gameLog("request move");
                Move move = null;
                try {
                    move = player.requireMove();
                } catch (Exception e) {
                    throw new InterruptedException(e.getMessage());
                }

                if (move == null) {
                    log("No valid move received!");
                    isPlaying = false;
                    return; // Prevent an invalid move from breaking the game loop
                }
                gameLog("move made");
                NetPacket responsePacket = new NetPacket(NetPacket.RESPONSE_MOVE, move);
                outStream.writeObject(responsePacket);
                gameLog("MOVE SENT");
            }
            case NetPacket.SET_PLAYER_COLOR -> {
                gameLog("Set player color");
                PieceColor color = (PieceColor) packet.data;
                player.setColor(color);
            }
            case NetPacket.REQUIRE_COLOR -> {
                gameLog("require color");
                NetPacket responsePacket;
                if (requireBot) {
                    responsePacket = new NetPacket(NetPacket.RESPONSE_COLOR, player.getColor(), true);
                }
                else {
                    responsePacket = new NetPacket(NetPacket.RESPONSE_COLOR, player.getColor());
                }
                outStream.writeObject(responsePacket);
            }
            case NetPacket.SET_GAME_STATE -> {
                gameLog("Set game state");
                GameState state = (GameState) packet.data;
                player.setGameState(state);
                if (state != GameState.PLAYING) {
                    player.terminate();
                }
            }
            default -> {
                gameLog("INVALID PACKET");
            }
        }
    }

    /**
     * method used to stop the current game
     */
    public void stopGame() {
        // Close the socket to break the blocking readObject call
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                log("Socket closed");
            }
        } catch (IOException e) {
            log("Error closing socket: " + e.getMessage());
        }

        // Interrupt the thread if it's still running
        if (gameThread != null && gameThread != Thread.currentThread()) {
            gameThread.interrupt();
            log("Game thread interrupted: " + gameId);
        }
    }

    /**
     * prints a message indicating the current game
     * @param msg message to be visualized
     */
    private void log(String msg) {
        System.out.println("Game " + gameId + " - " + msg);
    }

    /**
     * prints a message indicating the current game state
     * @param msg message to be visualized
     */
    private void gameLog(String msg) {
        //gameLog("Game " + gameId + " - " + msg);
    }
}
