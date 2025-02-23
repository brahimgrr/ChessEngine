package it.unibs.pajc.engine;

import it.unibs.pajc.game.model.*;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*public class ChessAI {
    private static final int MAX_DEPTH = 3;

    private static int evaluatedCounter = 0;

    public static Move getBestMove(ChessBoard board) {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;
        //board.setValidatedLegalMoves(board.getTurn());
        MoveMap legalMoves = board.getLegalMoves(board.getTurn());

        for (Move move : getSortedMoves(board, legalMoves)) {
            board._movePiece(move);
            board.changeTurn();
            int moveValue = minimax(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove();
            board.changeTurn();
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        //board.flush();
        if (bestMove == null) {
            System.out.println("No best move found");
        }
        System.out.println("No. evaluated moves: " + evaluatedCounter);
        evaluatedCounter = 0;
        return bestMove;
    }

    private static int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        board.setValidatedLegalMoves(board.getTurn());
        GameState gameState = board.getGameState();

        evaluatedCounter += 1;

        if (depth == 0 || gameState != GameState.PLAYING) {
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            //board.setValidatedLegalMoves(board.getTurn());
            MoveMap legalMoves = board.getLegalMoves(board.getTurn());

            for (Move move : getSortedMoves(board, legalMoves)) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(board,depth - 1, alpha, beta, false);
                board.undoMove();
                board.changeTurn();
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            //board.setValidatedLegalMoves(board.getTurn());
            MoveMap legalMoves = board.getLegalMoves(board.getTurn());

            for (Move move : getSortedMoves(board, legalMoves)) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.undoMove();
                board.changeTurn();
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private static int evaluateBoard(ChessBoard board) {
        GameState gameState = board.getGameState();
        if (gameState == GameState.WIN_WHITE) {
            return Integer.MAX_VALUE; // White wins (checkmate)
        } else if (gameState == GameState.WIN_BLACK) {
            return Integer.MIN_VALUE; // Black wins (checkmate)
        } else if (gameState == GameState.DRAW) {
            return 0; // Stalemate is a draw
        }

        int score = 0;
        Piece[][] tiles = board.getTiles();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = tiles[i][j];
                if (piece != null) {
                    score += piece.getValue();
                }
            }
        }
        return board.getTurn() == PieceColor.WHITE ? score : -score;
    }

    public static List<Move> getSortedMoves(ChessBoard board, MoveMap moveMap) {
        List<Move> moves = new ArrayList<>(moveMap.getAllMoves());
        moves.sort(Comparator.comparingInt(value -> -evaluateMove(board, (Move) value)));
        return moves;
    }

    private static int evaluateMove(ChessBoard board, Move move) {
        Piece attacker = board.getTiles()[move.getOldLocation().getRow()][move.getOldLocation().getCol()];
        Piece victim = board.getTiles()[move.getCaptureLocation().getRow()][move.getCaptureLocation().getCol()];
        return (victim != null ? 10 * victim.getValue() - attacker.getValue() : 0);
    }
}*/

import java.util.*;
import java.util.concurrent.Callable;

public class ChessAI implements Callable<Move> {
    //Maximum depth search
    private static final int MAX_DEPTH = 3;

    //total evaluated positions
    private int evaluatedCounter = 0;

    private final ChessBoard board;

    public ChessAI(ChessBoard chessBoard) {
        this.board = chessBoard;
    }

    /**
     * calculates using min-max algorithm best move based on boards current position
     * @return best move found
     * @throws InterruptedException
     */
    public Move getBestMove() throws InterruptedException {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;
        board.setValidatedLegalMoves(board.getTurn());
        MoveMap legalMoves = board.getLegalMoves(board.getTurn());

        for (Move move : getSortedMoves(board, legalMoves)) {
            board._movePiece(move);
            board.changeTurn();
            int moveValue = minimax(board, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove();
            board.changeTurn();
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        if (bestMove == null) {
            System.out.println("No best move found");
        }
        System.out.println("No. evaluated moves: " + evaluatedCounter);
        evaluatedCounter = 0;
        return bestMove;
    }

    /**
     * MIN-MAX algorithm implementation
     * with alpha-beta pruning optimization
     * @param board board to be evaluated
     * @param depth current depth
     * @param alpha alpha parameter
     * @param beta beta parameter
     * @param maximizingPlayer true if node should maximize value
     * @return value of current node
     * @throws InterruptedException
     */
    private int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) throws InterruptedException {
        //check if current thread is being interrupted, is so
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        board.setValidatedLegalMoves(board.getTurn());
        GameState gameState = board.getGameState();

        evaluatedCounter += 1;

        if (depth == 0 || gameState != GameState.PLAYING) {
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            //board.setValidatedLegalMoves(board.getTurn());
            MoveMap legalMoves = board.getLegalMoves(board.getTurn());

            for (Move move : getSortedMoves(board, legalMoves)) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(board,depth - 1, alpha, beta, false);
                board.undoMove();
                board.changeTurn();
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            //board.setValidatedLegalMoves(board.getTurn());
            MoveMap legalMoves = board.getLegalMoves(board.getTurn());

            for (Move move : getSortedMoves(board, legalMoves)) {
                board._movePiece(move);
                board.changeTurn();
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.undoMove();
                board.changeTurn();
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    /**
     * calculates and returns the current position value
     * values pieces based on their position on board
     * @param board board to be evaluated
     * @return boards value
     */
    private int evaluateBoard(ChessBoard board) {
        GameState gameState = board.getGameState();
        if (gameState == GameState.WIN_WHITE) {
            return Integer.MAX_VALUE;
        } else if (gameState == GameState.WIN_BLACK) {
            return Integer.MIN_VALUE;
        } else if (gameState == GameState.DRAW) {
            return 0; // Stalemate is a draw
        }

        int score = 0;
        Piece[][] tiles = board.getTiles();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = tiles[i][j];
                if (piece != null) {
                    score += piece.getValue();
                }
            }
        }
        return board.getTurn() == PieceColor.WHITE ? score : -score;
    }

    /**
     * sort moves based on their capture outcome
     * @param board current board
     * @param moveMap current legal moves map
     * @return sorted move list
     */
    public List<Move> getSortedMoves(ChessBoard board, MoveMap moveMap) {
        List<Move> moves = new ArrayList<>(moveMap.getAllMoves());
        moves.sort(Comparator.comparingInt(value -> -evaluateMove(board, value)));

        return moves;
    }

    /**
     * calculates the move values based on the capture outcome
     * @param board current board
     * @param move move to be evaluated
     * @return move value
     */
    private int evaluateMove(ChessBoard board, Move move) {
        Piece targetPiece = board.getTiles()[move.getCaptureLocation().getRow()][move.getCaptureLocation().getCol()];
        return (targetPiece != null ? targetPiece.getValue() : 0);
    }

    /**
     * returns the best move for the current player's turn
     * @return best move calculated
     * @throws InterruptedException
     */
    @Override
    public Move call() throws InterruptedException {
        return getBestMove();
    }
}