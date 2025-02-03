package it.unibs.pajc.server.controller;

import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT = 12345;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static int gameCounter = 0;

    private static final Stack<RemotePlayer> whitePlayers = new Stack<>();
    private static final Stack<RemotePlayer> blackPlayers = new Stack<>();

    private static final Map<PieceColor, Stack<RemotePlayer>> playersMap = new HashMap<>();
    static {
        playersMap.put(PieceColor.BLACK, blackPlayers);
        playersMap.put(PieceColor.WHITE, whitePlayers);
    }

    public static void main(String[] args) {
        System.out.println("Starting Game Server...");

        /*try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game Server running.");
            System.out.println("Port: " + PORT);

            while (true) {
                System.out.println("Waiting for clients...");
                System.out.println();

                Socket whitePlayerSocket = serverSocket.accept();
                System.out.println("White player connected: " + whitePlayerSocket.getInetAddress());

                Socket blackPlayerSocket = serverSocket.accept();
                System.out.println("Black player connected: " + blackPlayerSocket.getInetAddress());

                RemotePlayer whitePlayer = new RemotePlayer(PieceColor.WHITE, whitePlayerSocket);
                RemotePlayer blackPlayer = new RemotePlayer(PieceColor.BLACK, blackPlayerSocket);
                executor.execute(new GameController(gameCounter, whitePlayer, blackPlayer));
                gameCounter++;

                tryGameMatch();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }*/
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game Server running on port: " + PORT);

            while (true) {
                System.out.println("Waiting for clients...");
                Socket playerSocket = serverSocket.accept();
                System.out.println("Player connected: " + playerSocket.getInetAddress());

                RemotePlayer player = new RemotePlayer(playerSocket);
                executor.execute(() -> handlePlayerConnection(player));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void handlePlayerConnection(RemotePlayer player) {
        try {
            player.performHandshake(); // Ensures the player gets assigned a color
            PieceColor assignedColor = player.getColor();
            synchronized (playersMap) {
                playersMap.get(assignedColor).push(player);
                System.out.println("Player assigned color: " + assignedColor);
                tryGameMatch();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tryGameMatch() {
        synchronized (playersMap) {
            if (!whitePlayers.isEmpty() && !blackPlayers.isEmpty()) {
                RemotePlayer whitePlayer = whitePlayers.pop();
                RemotePlayer blackPlayer = blackPlayers.pop();
                System.out.println("Starting game " + gameCounter);
                executor.execute(new GameController(gameCounter++, whitePlayer, blackPlayer));
            }
        }
    }
}