package it.unibs.pajc.game.controller;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.util.HashMap;
import java.util.Map;

public class GameController implements Runnable{
    private final int gameId;
    private final ChessBoard board;
    private final Map<PieceColor, Player> playerMap;
    private Thread gameThread;

    private boolean isPlaying;

    public GameController(int gameId, Player whitePlayer, Player blackPlayer) {
        this.gameId = gameId;
        this.board = new ChessBoard();
        this.playerMap = new HashMap<>();
        this.playerMap.put(PieceColor.WHITE, whitePlayer);
        this.playerMap.put(PieceColor.BLACK, blackPlayer);
        this.playerMap.forEach((color, player) -> {player.setColor(color);});
        this.playerMap.get(PieceColor.WHITE).setPosition(board.getPosition());
        this.playerMap.get(PieceColor.BLACK).setPosition(board.getPosition());
        this.isPlaying = true;
    }


    public boolean gameLoop() throws InterruptedException {
        System.out.println("GAME LOOP THREAD: " + Thread.currentThread().getName());
        log("Turn: " + board.getTurn());
        Player currentPlayer = playerMap.get(board.getTurn());
        board.setValidatedLegalMoves(board.getTurn());
        currentPlayer.setPosition(board.getPosition());
        currentPlayer.setLegalMoves(board.getLegalMoves(board.getTurn()));
        Move move = currentPlayer.requireMove();
        board.movePiece(move);
        board.changeTurn();
        Thread.sleep(100);
        return true;
    }

    private void log(String msg) {
        System.out.println("Game " + gameId + " - " + msg);
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        playerMap.forEach((color, player) -> {player.setRunningThread(Thread.currentThread());});
        while (isPlaying) {
            try {
                isPlaying = gameLoop();
            } catch (InterruptedException e) {
                log("Game interrupted!");
                Thread.currentThread().interrupt(); // Preserve the interrupt flag
                break;
            }
        }
    }

    public void stopGame() {
        isPlaying = false;
        System.out.println("Interrupting game loop: " + gameThread.getName());
        if (gameThread != null) {
            gameThread.interrupt(); // Interrupt the game thread
            //gameThread.join();
        }
    }
}
