package it.unibs.pajc.engine;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;

import java.util.concurrent.*;

/**
 * Class representing an AI player
 */
public class EnginePlayer extends Player {
    //reference to the game board
    private final ChessBoard board;
    //executor to launch the best move calculation
    private final ExecutorService executor;

    /**
     * Default constructor
     * @param board the board on which the game is played
     */
    public EnginePlayer(ChessBoard board) {
        this.board = board;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setPosition(String fenString) {
    }

    @Override
    public void setLastMove(Move move) {
    }

    @Override
    public void setLegalMoves(MoveMap legalMoves) {
    }

    @Override
    public void setGameState(GameState gameState) {
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    /**
     * calculates and returns the best calculated move
     * on the referenced board
     * @return best calculated move based
     */
    @Override
    public Move requireMove() throws InterruptedException {
        Future<Move> future = executor.submit(new ChessAI(board.clone()));

        try {
            // Wait for the result (blocking operation)
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            // If the calling thread is interrupted, cancel the task
            future.cancel(true); // Interrupt the worker thread
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new InterruptedException();
        }
    }

    /**
     * shutdown the engine player executor
     */
    @Override
    public void terminate() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(100, TimeUnit.MILLISECONDS))
                executor.shutdownNow();
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
