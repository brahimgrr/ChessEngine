package it.unibs.pajc.server.controller;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.controller.GuiPlayer;
import it.unibs.pajc.engine.EnginePlayer;
import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.utils.NetworkConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Game server
 */
public class GameServer {
    //thread executor responsible for matching and launching games
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

    //debugging flag
    private static final boolean DEBUG = true;

    //initiated games counter
    private static int gameCounter = 0;

    //white players buffer
    private static final Queue<RemotePlayer> whitePlayers = new ArrayDeque<>();
    //black players buffer
    private static final Queue<RemotePlayer> blackPlayers = new ArrayDeque<>();
    //pending players buffer
    private static final Map<PieceColor, Queue<RemotePlayer>> playersMap = new HashMap<>();
    static {
        playersMap.put(PieceColor.BLACK, blackPlayers);
        playersMap.put(PieceColor.WHITE, whitePlayers);
    }

    public static void main(String[] args) {
        System.out.println("Starting Game Server...");

        try (ServerSocket serverSocket = new ServerSocket(NetworkConstants.SERVER_PORT)) {
            System.out.println("Game Server running on port: " + NetworkConstants.SERVER_PORT);

            while (true) {
                System.out.println("Waiting for clients...");
                Socket playerSocket = serverSocket.accept();
                playerSocket.setKeepAlive(true);
                System.out.println("Player connected: " + playerSocket.getInetAddress());

                executor.execute(() -> handlePlayerConnection(playerSocket));
                printExecutorState();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //executor termination
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }


    /**
     * performs a handshake with the remote player and adds it to the
     * correct buffer
     * tries to match the player with a valid opponent
     * @param playerSocket player socket
     */
    private static void handlePlayerConnection(Socket playerSocket) {
        try {
            RemotePlayer player = new RemotePlayer(playerSocket);
            // handshake, make sure the player gets assigned a color
            PieceColor assignedColor = player.requireColor();
            if (player.requireBot()) {
                botGameMatch(player);
            }
            else {
                synchronized (playersMap) {
                    playersMap.get(assignedColor).add(player);
                    System.out.println("Player assigned color: " + assignedColor);
                    tryGameMatch();
                }
            }
        } catch (IOException e) {
            try {
                if (playerSocket != null && playerSocket.isConnected()) {
                    playerSocket.close();
                }
            } catch (IOException es) {
                es.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * launch a game matching the remote player
     * with a BOT, assigning the correct color
     * @param player remote player
     */
    private static void botGameMatch(RemotePlayer player) throws IOException {
        Player whitePlayer;
        Player blackPlayer;
        //Game controller automatically assigns a BOT when a player is null
        //Socket engineSocket = new Socket(NetworkConstants.SERVER_IP, NetworkConstants.ENGINE_PORT);
        if (player.getColor() == PieceColor.WHITE) {
            whitePlayer = player;

            blackPlayer = null;

            //blackPlayer = new RemotePlayer(engineSocket);
        }
        else {
            whitePlayer = null;
            //whitePlayer = new RemotePlayer(engineSocket);
            blackPlayer = player;
        }
        System.out.println("Starting BOT game " + gameCounter);
        executor.execute(new GameController(gameCounter++, whitePlayer, blackPlayer));
        printExecutorState();
    }

    /**
     * try to launch a game matching a white remote
     * player against a black remote player
     */
    private static void tryGameMatch() {
        synchronized (playersMap) {
            if (!whitePlayers.isEmpty() && !blackPlayers.isEmpty()) {
                Player whitePlayer = whitePlayers.poll();
                Player blackPlayer = blackPlayers.poll();
                System.out.println("Starting game " + gameCounter);
                executor.execute(new GameController(gameCounter++, whitePlayer, blackPlayer));
                printExecutorState();
            }
        }
    }

    /**
     * prints the state of the current pool executor
     */
    private static void printExecutorState() {
        if (!DEBUG) {
            return;
        }
        System.out.println();
        System.out.println(" - Executor pool state -");
        System.out.println("Active count: " + threadPoolExecutor.getActiveCount());
        System.out.println("Completed task: " + threadPoolExecutor.getCompletedTaskCount());
        System.out.println("Pool size: " + threadPoolExecutor.getPoolSize());
        System.out.println();
    }
}