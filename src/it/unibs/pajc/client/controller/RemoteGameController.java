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
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Controller to handle a game with a remote opponent
 */
public class RemoteGameController implements Runnable {
    //game unique identifier
    private final int gameId;
    //gui board player
    private final Player player;
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

    private final Map<String, Consumer<NetPacket>> packetHandlers;

    private final ExecutorService moveExecutor = Executors.newSingleThreadExecutor();


    private Map<String, Consumer<NetPacket>> generatePacketHandlers(Player player) {
        return Map.of(
                NetPacket.SET_POSITION_FEN, packet -> handlePosition(packet),
                NetPacket.SET_LEGAL_MOVES, packet -> handleLegalMoves(packet),
                NetPacket.SET_LAST_MOVE, packet -> handleLastMove(packet),
                NetPacket.REQUEST_MOVE, packet -> handleMoveRequestAsync(),
                NetPacket.SET_PLAYER_COLOR, packet -> handleColor(packet),
                NetPacket.REQUIRE_COLOR, packet -> handleColorResponse(),
                NetPacket.SET_GAME_STATE, packet -> handleGameState((GameState) packet.data),
                NetPacket.PING, packet -> handlePing()
        );
    }

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
        this.packetHandlers = generatePacketHandlers(player);
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        try {
            log("Launched!");
            socket = new Socket(ip, port);
            socket.setKeepAlive(true);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(socket.getInputStream());

            while (isPlaying) {
                processPacket();
            }
        } catch (IOException | ClassNotFoundException e) {
            log("Remote Game interrupted!");
            handleGameState(GameState.CONNECTION_LOST);
            Thread.currentThread().interrupt();
        } finally {
            moveExecutor.shutdownNow();
            closeSocket();
        }
        log("Game loop terminated!");
    }

    private void processPacket() throws IOException, ClassNotFoundException {
        Object obj = inStream.readObject();
        if (obj instanceof NetPacket packet) {
            packetHandlers
                    .getOrDefault(packet.type, p -> log("INVALID PACKET"))
                    .accept(packet);
        }
    }

    private void handleMoveRequestAsync() {
        moveExecutor.submit(() -> handleMoveRequest());
    }
    private void handleMoveRequest() {
        try {
            Move move = player.requireMove();
            if (move == null) {
                log("No valid move received!");
                isPlaying = false;
                return;
            }
            outStream.writeObject(new NetPacket(NetPacket.RESPONSE_MOVE, move));
        } catch (Exception e) {
            //isPlaying = false;
            log("Error in move request: " + e.getMessage());
        }
    }

    private void handleColorResponse() {
        try {
            NetPacket responsePacket = new NetPacket(NetPacket.RESPONSE_COLOR, player.getColor(), requireBot);
            outStream.writeObject(responsePacket);
        } catch (IOException e) {
            log("Failed to send color response: " + e.getMessage());
        }
    }

    private void handlePing() {
        try {
            NetPacket pong = new NetPacket(NetPacket.PONG, null);
            outStream.writeObject(pong);
        } catch (IOException e) {
            log("Failed to pong request: " + e.getMessage());
        }
    }

    private void handleColor(NetPacket packet) {
        player.setColor((PieceColor) packet.data);
    }

    private void handleLastMove(NetPacket packet) {
        try {
            player.setLastMove((Move) packet.data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLegalMoves(NetPacket packet) {
        try {
            player.setLegalMoves((MoveMap) packet.data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePosition(NetPacket packet) {
        try {
            player.setPosition((String) packet.data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGameState(GameState state) {
        try {
            player.setGameState(state);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (state != GameState.PLAYING) {
            player.terminate();
        }
    }

    public void stopGame() {
        closeSocket();
        if (gameThread != null && gameThread != Thread.currentThread()) {
            gameThread.interrupt();
            log("Game thread interrupted: " + gameId);
        }
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                log("Socket closed");
            }
        } catch (IOException e) {
            log("Error closing socket: " + e.getMessage());
        }
    }

    private static void safeExecute(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error executing packet handler: " + e.getMessage());
        }
    }

    private void log(String msg) {
        System.out.println("Game " + gameId + " - " + msg);
    }
}
