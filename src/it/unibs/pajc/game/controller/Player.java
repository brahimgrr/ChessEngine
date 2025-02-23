package it.unibs.pajc.game.controller;

import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.io.IOException;


/**
 * abstract class representing a player
 */
public abstract class Player {
    //player color
    private PieceColor color;

    /**
     * Default constructor
     */
    public Player() {}

    /**
     * Constructs a player with the specified color.
     *
     * @param color the color of the player's pieces.
     */
    public Player(PieceColor color) {
        this.color = color;
    }

    /**
     * Sets the current position of the game using fen string.
     *
     * @param fenString the fen string representing the current game position.
     */
    public abstract void setPosition(String fenString) throws IOException;

    /**
     * Sets the last move made in the game.
     *
     * @param move the last move made in the game.
     */
    public abstract void setLastMove(Move move) throws IOException;

    /**
     * Sets the legal moves available to the player.
     *
     * @param legalMoves a move map containing the legal moves.
     */
    public abstract void setLegalMoves(MoveMap legalMoves) throws IOException;

    /**
     * Requests the player to make a move. This method blocks until the player provides a move.
     *
     * @return the move chosen by the player.
     * @throws InterruptedException if the thread is interrupted while waiting for the player's move.
     */
    public abstract Move requireMove() throws InterruptedException, IOException;

    /**
     * Terminates the player's current operation or process.
     */
    public abstract void terminate();

    /**
     * Sets the current state of the game.
     *
     * @param gameState the current state of the game.
     */
    public abstract void setGameState(GameState gameState) throws IOException;

    /**
     * Sets the color of the player's pieces.
     *
     * @param color the color of the player's pieces.
     */
    public void setColor(PieceColor color) {
        this.color = color;
    }

    /**
     * Returns the color of the player's pieces.
     *
     * @return the color of the player's pieces.
     */
    public PieceColor getColor() {
        return color;
    }

    public abstract boolean isAlive();
}
