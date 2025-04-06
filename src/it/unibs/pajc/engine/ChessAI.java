package it.unibs.pajc.engine;

import it.unibs.pajc.game.model.*;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.util.*;
import java.util.concurrent.Callable;

public class ChessAI implements Callable<Move> {
    private static final int MAX_DEPTH = 3;
    private int evaluatedCounter = 0;
    private final ChessBoard board;

    public ChessAI(ChessBoard chessBoard) {
        this.board = chessBoard;
    }

    public Move getBestMove() throws InterruptedException {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;
        board.setValidatedLegalMoves(board.getTurn());
        MoveMap legalMoves = board.getLegalMoves(board.getTurn());

        List<Move> sortedMoves = getSortedMoves(board, legalMoves);
        for (Move move : sortedMoves) {
            board._movePiece(move);
            board.changeTurn();
            int moveValue = minimax(MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove();
            board.changeTurn();
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        System.out.println("No. evaluated moves: " + evaluatedCounter);
        evaluatedCounter = 0;
        return bestMove;
    }

    private int minimax(int depth, int alpha, int beta, boolean maximizingPlayer) throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        GameState gameState = board.getGameState();
        evaluatedCounter++;

        if (depth == 0 || gameState != GameState.PLAYING) {
            return evaluateBoard();
        }

        board.setValidatedLegalMoves(board.getTurn());
        MoveMap legalMoves = board.getLegalMoves(board.getTurn());
        List<Move> sortedMoves = getSortedMoves(board, legalMoves);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : sortedMoves) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(depth - 1, alpha, beta, false);
                board.undoMove();
                board.changeTurn();
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : sortedMoves) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(depth - 1, alpha, beta, true);
                board.undoMove();
                board.changeTurn();
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluateBoard() {
        GameState gameState = board.getGameState();
        if (gameState == GameState.WIN_WHITE) {
            return Integer.MAX_VALUE;
        } else if (gameState == GameState.WIN_BLACK) {
            return Integer.MIN_VALUE;
        } else if (gameState == GameState.DRAW) {
            return 0;
        }

        int score = 0;
        Piece[][] tiles = board.getTiles();
        for (Piece[] row : tiles) {
            for (Piece piece : row) {
                if (piece != null) {
                    score += piece.getValue();
                }
            }
        }
        return board.getTurn() == PieceColor.WHITE ? score : -score;
    }

    public List<Move> getSortedMoves(ChessBoard board, MoveMap moveMap) {
        List<Move> moves = new ArrayList<>(moveMap.getAllMoves());
        moves.sort(Comparator.comparingInt(move -> -evaluateMove(move)));
        return moves;
    }

    private int evaluateMove(Move move) {
        Piece targetPiece = board.getTiles()[move.getCaptureLocation().getRow()][move.getCaptureLocation().getCol()];
        return (targetPiece != null ? targetPiece.getValue() : 0);
    }

    @Override
    public Move call() throws InterruptedException {
        return getBestMove();
    }
}
