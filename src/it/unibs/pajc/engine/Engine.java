package it.unibs.pajc.engine;

import it.unibs.pajc.game.model.ChessBoard;
import it.unibs.pajc.game.model.Move;

public class Engine extends ChessBoard {
    public Engine() {
        super();
    }

    public Move getBestMove() {
        Move bestMove = null;

        return bestMove;
    }

    public Move makeMove() {
        Move bestMove = getBestMove();
        movePiece(bestMove);
        changeTurn();
        return bestMove;
    }

    public void opponentMove(Move move) {
        movePiece(move);
        changeTurn();
    }

}
