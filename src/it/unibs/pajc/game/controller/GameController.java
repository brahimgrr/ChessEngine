package it.unibs.pajc.game.controller;

import it.unibs.pajc.engine.EnginePlayer;
import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class representing a Chess game
 * provides logic to handle the game flow
 */
public class GameController implements Runnable{
    private final int gameId;
    // game chess board
    private final ChessBoard board;
    //player map, base on color
    private final Map<PieceColor, Player> playerMap;
    //thread on which the game is running
    private Thread gameThread;
    //game spectator, will only receive board positions and states
    private Player spectatorPlayer;
    //state of the GameController
    private boolean isPlaying;
    // Executor for async move request
    ExecutorService moveExecutor = Executors.newSingleThreadExecutor();
    //last move made
    private Move lastMove;

    /**
     * Constructor of GameController
     * if a player is not provided, constructor will use
     * the default player (AI ENGINE PLAYER)
     * @param gameId game number
     * @param whitePlayer white player
     * @param blackPlayer black player
     */
    public GameController(int gameId, Player whitePlayer, Player blackPlayer) {
        this.gameId = gameId;
        this.board = new ChessBoard();
        this.playerMap = new HashMap<>();
        if (blackPlayer == null) {
            blackPlayer = new EnginePlayer();
        }
        if (whitePlayer == null) {
            whitePlayer = new EnginePlayer();
        }
        this.playerMap.put(PieceColor.WHITE, whitePlayer);
        this.playerMap.put(PieceColor.BLACK, blackPlayer);
        this.playerMap.forEach((color, player) -> {player.setColor(color);});
        this.isPlaying = true;
    }

    /**
     * Constructor used to watch a game between two bots
     * @param gameId game number
     * @param spectatorPlayer spectator player
     */
    public GameController(int gameId, Player spectatorPlayer) {
        this(gameId, null, null);
        this.spectatorPlayer = spectatorPlayer;
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
        //log("Game " + gameId + " - " + msg);
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        try {
            playerMap.get(PieceColor.WHITE).setPosition(board.getPosition());
            playerMap.get(PieceColor.BLACK).setPosition(board.getPosition());

            while (isPlaying) {
                isPlaying = gameLoop();
            }
        } catch (InterruptedException | IOException e) {
            log("Game interrupted!");
            playerMap.forEach( (color, player) -> {player.terminate();});
            Thread.currentThread().interrupt(); // Preserve the interrupt flag
        }
        finally {
            moveExecutor.shutdownNow();
            log("Game loop terminated!");
        }
    }

    /**
     * game loop function, returns true
     * if game should continue
     * @return a boolean representing wether the game should continue
     * @throws InterruptedException if the current game is interrupted
     */
    public boolean gameLoop() throws InterruptedException, IOException {
        PieceColor turn = board.getTurn();

        // Set validated legal moves for the current player
        board.setValidatedLegalMoves(turn);

        // Check current game state
        GameState gameState = board.getGameState();

        // Update spectator view if present
        if (spectatorPlayer != null) {
            if (lastMove != null) {
                spectatorPlayer.setLastMove(lastMove);
            }
            spectatorPlayer.setPosition(board.getPosition());
        }

        // Inform players of the current position and last move
        for (Player player : playerMap.values()) {
            player.setLastMove(lastMove);
            player.setPosition(board.getPosition());
        }

        if (gameState == GameState.PLAYING) {
            gameLog("Turn: " + turn);

            Player currentPlayer = playerMap.get(turn);

            currentPlayer.setLegalMoves(board.getLegalMoves(turn));

            Future<Move> moveFuture = moveExecutor.submit(() -> {
                try {
                    // Wait for the player's move
                    lastMove = currentPlayer.requireMove();

                    return lastMove;
                } catch (Exception e) {
                    log("Error receiving move: " + e.getMessage());
                    return null;
                }
            });

            while (true) {
                if (!isOpponentConnected(currentPlayer)) {
                    log("Opponent disconnected!");

                    throw new InterruptedException("Opponent disconnected");
                }

                if (moveFuture.isDone()) {
                    if (moveFuture.resultNow() == null) {
                        log("moveExecutor: No valid move received!");
                        throw new InterruptedException("Player disconnected");
                    }
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }

            board.movePiece(lastMove, true);
            gameLog("MOVE " + turn.getName() + " : " + lastMove);
            board.changeTurn();
            //Thread.sleep(100);
            return true;
        }

        // Inform players of the final game state before termination
        playerMap.get(PieceColor.WHITE).setGameState(gameState);
        playerMap.get(PieceColor.BLACK).setGameState(gameState);
        if (spectatorPlayer != null) {
            spectatorPlayer.setGameState(gameState);
        }

        // Terminate all players
        playerMap.forEach((color, player) -> player.terminate());

        return false;
    }

    /**
     * Ping to check if opponent player is still connected
     * @param currentPlayer current player
     * @return boolean representing opponent connection state
     */
    private boolean isOpponentConnected(Player currentPlayer) {
        PieceColor opponentColor = currentPlayer.getColor().getOpposite();
        Player opponent = playerMap.get(opponentColor);

        // Check if the opponent's connection is active (this could be a socket check or a specific method)
        return opponent.isAlive(); // Assuming you have an isConnected() method or similar
    }
    /**
     * method used to stop the current game
     */
    public void stopGame() {
        // Prevent redundant calls
        if (!isPlaying) return;
        isPlaying = false;
        log("Stopping game...");

        // only external threads can stop the current game
        if (gameThread != null && gameThread != Thread.currentThread()) {
            gameThread.interrupt(); // Interrupt only if it's not the current thread
            log(Thread.currentThread().getName() + ": interrupted Game " + gameId);
        }
    }
}
