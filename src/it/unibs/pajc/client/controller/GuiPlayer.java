package it.unibs.pajc.client.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;

import javax.swing.*;

/**
 * Class representing a GUI player
 */
public class GuiPlayer extends Player {
    //chessboard controller
    private final BoardController controller;
    //flag sets the player as spectator, interaction with the board is forbidden
    private final boolean isSpectator;

    /**
     * Constructor
     * @param color player color
     * @param boardController chessboard controller
     */
    public GuiPlayer(PieceColor color, BoardController boardController) {
        super(color);
        this.controller = boardController;
        this.controller.setPlayerColor(color);
        this.isSpectator = false;
    }

    /**
     * Constructor used to specify if player is a spectator
     * @param color player color
     * @param boardController chessboard controller
     * @param isSpectator boolean flag for spectator player
     */
    public GuiPlayer(PieceColor color, BoardController boardController, boolean isSpectator) {
        super(color);
        this.controller = boardController;
        this.isSpectator = isSpectator;
    }

    /**
     * visualize a message indicating the game state
     * @param gameState the current state of the game.
     */
    @Override
    public void setGameState(GameState gameState) {
        if (isSpectator) {
            return;
        }
        switch (gameState) {
            case DRAW -> JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Draw! No winner!");
            case WIN_BLACK -> JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Winner is black!");
            case WIN_WHITE -> JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Winner is white!");
            case PLAYING -> {}
            default -> JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Connection lost!");
        }
    }

    /**
     * sets the game position to be visualized in the gui
     * @param fenString the fen string representing the current game position.
     */
    @Override
    public void setPosition(String fenString) {
        if (isSpectator) {
            return;
        }
        controller.setPosition(fenString);
    }

    /**
     * sets the last move made in a game, to be visualized in the gui
     * @param move the last move made in the game.
     */
    @Override
    public void setLastMove(Move move) {
        if (isSpectator) {
            return;
        }
        controller.setLastMove(move);
    }

    /**
     * sets the legal moves available to the gui player to be made
     * @param legalMoves a move map containing the legal moves.
     */
    @Override
    public void setLegalMoves(MoveMap legalMoves) {
        controller.setLegalMoves(legalMoves);
    }

    /**
     * try to request and return a move from the gui player
     * @return move made by the gui player
     * @throws InterruptedException if thread is interrupted before a move is madde
     */
    @Override
    public Move requireMove() throws InterruptedException {
        return controller.requireMove(getColor());
    }

    /**
     * sets the player's color
     * @param color the color of the player's pieces.
     */
    @Override
    public void setColor(PieceColor color) {
        super.setColor(color);
        if (isSpectator) {
            return;
        }
        controller.setPlayerColor(getColor());
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    /**
     * closes the player view
     */
    @Override
    public void terminate() {
        if (isSpectator) {
            return;
        }
        controller.closeView();
    }
}
