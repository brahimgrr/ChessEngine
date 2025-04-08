package it.unibs.pajc.server.controller;

import it.unibs.pajc.client.controller.RemoteGameController;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.utils.NetworkConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EngineServer {
    //thread executor responsible for matching and launching games
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

    //debugging flag
    private static final boolean DEBUG = true;

    //initiated games counter
    private static int gameCounter = 0;

    public static void main(String[] args) {
        System.out.println("Starting Engine Server...");

        try (ServerSocket serverSocket = new ServerSocket(NetworkConstants.ENGINE_PORT)) {
            System.out.println("Engine Server running on port: " + NetworkConstants.ENGINE_PORT);

            while (true) {
                System.out.println("Waiting for clients...");
                Socket playerSocket = serverSocket.accept();
                playerSocket.setKeepAlive(true);
                System.out.println("Client connected: " + playerSocket.getInetAddress());

                executor.execute(() -> handleClientConnection(playerSocket));
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

    private static void handleClientConnection(Socket playerSocket) {
        //RemoteGameController remoteGameController = new RemoteGameController();
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