package it.unibs.pajc.game.controller;

import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.PieceColor;

public abstract class Player {
    private PieceColor color;
    public Player() {}

    public Player(PieceColor color) {
        this.color = color;
    }

    public abstract void setPosition(String fenString);

    public abstract void setLegalMoves(MoveMap legalMoves);

    public abstract Move requireMove() throws InterruptedException;

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public abstract void terminate();

    public PieceColor getColor() {
        return color;
    }

    public abstract void setRunningThread(Thread thread);
}
