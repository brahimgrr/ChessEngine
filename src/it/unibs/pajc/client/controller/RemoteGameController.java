package it.unibs.pajc.client.controller;

import it.unibs.pajc.client.utils.Constants;
import it.unibs.pajc.engine.EnginePlayer;
import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.model.NetPacket;
import it.unibs.pajc.server.utils.NetworkConstants;

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
    //Client player
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
    // Executor for async move request
    private final ExecutorService moveExecutor = Executors.newSingleThreadExecutor();
    // packet handlers based on packet ID
    private final Map<String, Consumer<NetPacket>> packetHandlers = Map.of(
                NetPacket.SET_POSITION_FEN, this::handlePosition,
                NetPacket.SET_LEGAL_MOVES, this::handleLegalMoves,
                NetPacket.SET_LAST_MOVE, this::handleLastMove,
                NetPacket.REQUEST_MOVE, this::handleMoveRequestAsync,
                NetPacket.SET_PLAYER_COLOR, this::handleColor,
                NetPacket.REQUIRE_COLOR, this::handleColorResponse,
                NetPacket.SET_GAME_STATE, this::handleGameState,
                NetPacket.PING, this::handlePing
    );

    /**
     * RemoteGameController constructor
     * @param gameId game identifier
     * @param player player
     * @param ip server ip
     * @param port server port
     * @param requireBot flag to request game against a bot
     */
    public RemoteGameController(int gameId, Player player, String ip, int port, boolean requireBot) {
        this.gameId = gameId;
        this.player = player;
        this.ip = ip;
        this.port = port;
        this.requireBot = requireBot;
        this.isPlaying = true;
    }

    /**
     * RemoteGameController constructor with already initiated socket
     * @param gameId game identifier
     * @param player player
     * @param socket server connected socket
     */
    public RemoteGameController(int gameId, Player player, Socket socket) {
        this.gameId = gameId;
        this.player = player;
        this.socket = socket;
        this.ip = socket.getLocalAddress().getHostAddress();
        this.port = socket.getLocalPort();
        this.requireBot = false;
        this.isPlaying = true;
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        try {
            log("Launched!");

            if (socket == null) {
                socket = new Socket(ip, port);
            }

            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush(); // avoid serialization errors
            inStream = new ObjectInputStream(socket.getInputStream());

            while (isPlaying) {
                processPacket();
            }
        }
        catch (IOException | ClassNotFoundException e) {
            log("Remote Game interrupted!");

            terminatePlayer();

            Thread.currentThread().interrupt();
        }
        finally {
            moveExecutor.shutdownNow();

            closeSocket();

            log("Game loop terminated!");
        }
    }

    /**
     * Receive and process packets based on their type.
     * @throws IOException any communication error
     * @throws ClassNotFoundException non-compatible packet
     */
    private void processPacket() throws IOException, ClassNotFoundException {
        Object obj = inStream.readObject();

        if (obj instanceof NetPacket packet) {
            packetHandlers
                    .getOrDefault(packet.type, p -> log("INVALID PACKET"))
                    .accept(packet);
        }
    }

    /**
     * Async move request
     */
    private void handleMoveRequestAsync(NetPacket packet) {
        moveExecutor.submit(this::handleMoveRequest);
    }

    /**
     * Request a move from the Client player and send
     * the result move to the Server
     */
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
            //isPlaying = false; //TODO CHECK IF NECESSARY
            log("Error in move request: " + e.getMessage());
        }
    }

    /**
     * Informs Server of the player chosen color.
     * Flag to request matching with an AI player.
     */
    private void handleColorResponse(NetPacket packet) {
        try {
            NetPacket responsePacket = new NetPacket(NetPacket.RESPONSE_COLOR, player.getColor(), requireBot);
            outStream.writeObject(responsePacket);
        } catch (IOException e) {
            log("Failed to send color response: " + e.getMessage());
        }
    }

    /**
     * Ping/Pong to maintain the connection.
     */
    private void handlePing(NetPacket packet) {
        try {
            NetPacket pong = new NetPacket(NetPacket.PONG, null);
            outStream.writeObject(pong);
        } catch (IOException e) {
            log("Failed to pong request: " + e.getMessage());
        }
    }

    /**
     * Sets player's color assigned by Server.
     * @param packet networking packet
     */
    private void handleColor(NetPacket packet) {
        player.setColor((PieceColor) packet.data);
    }

    /**
     *Sets last move made in game
     * @param packet networking packet
     */
    private void handleLastMove(NetPacket packet) {
        try {
            player.setLastMove((Move) packet.data);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException");
        }
    }

    /**
     * Sets player's available legal moves
     * @param packet networking packet
     */
    private void handleLegalMoves(NetPacket packet) {
        try {
            player.setLegalMoves((MoveMap) packet.data);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException");
        }
    }

    /**
     * Sets current game position
     * @param packet networking packet
     */
    private void handlePosition(NetPacket packet) {
        try {
            player.setPosition((String) packet.data);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException");
        }
    }

    /**
     * Inform player of current game state
     * @param packet networking packet
     */
    private void handleGameState(NetPacket packet) {
        try {
            GameState state = (GameState) packet.data;
            player.setGameState(state);
            if (state != GameState.PLAYING) {
                player.terminate();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException");
        }
    }

    /**
     * Kills current player after connection closure
     */
    private void terminatePlayer() {
        try {
            player.setGameState(GameState.CONNECTION_LOST);

            player.terminate();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException");
        }
    }

    /**
     * Interrupt networking and Game thread
     */
    public void stopGame() {
        closeSocket();

        if (gameThread != null && gameThread != Thread.currentThread()) {
            gameThread.interrupt();
            log("Game thread interrupted: " + gameId);
        }
    }

    /**
     * Socket closure
     */
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
    private void log(String msg) {
        System.out.println("Game " + gameId + " - " + msg);
    }
}
