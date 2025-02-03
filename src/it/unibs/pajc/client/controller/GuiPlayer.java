package it.unibs.pajc.client.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.PieceColor;


public class GuiPlayer extends Player {
    private final BoardController controller;
    private final boolean isSpectator;

    public GuiPlayer(PieceColor color, BoardController boardController) {
        super(color);
        this.controller = boardController;
        this.controller.setPlayerColor(color);
        this.isSpectator = false;
    }

    public GuiPlayer(BoardController boardController) {
        super();
        this.controller = boardController;
        this.isSpectator = false;
    }

    public GuiPlayer(PieceColor color, BoardController boardController, boolean isSpectator) {
        super(color);
        this.controller = boardController;
        this.isSpectator = isSpectator;
    }

    @Override
    public void setPosition(String fenString) {
        if (isSpectator) {
            return;
        }
        controller.setPosition(fenString);
    }

    @Override
    public void setLegalMoves(MoveMap legalMoves) {
        controller.setLegalMoves(legalMoves);
    }

    @Override
    public Move requireMove() throws InterruptedException {
        return controller.requireMove(getColor());
    }

    @Override
    public void setColor(PieceColor color) {
        super.setColor(color);
        controller.setPlayerColor(getColor());
    }

    @Override
    public void terminate() {
        controller.closeView();
    }

    @Override
    public void setRunningThread(Thread thread) {
        System.out.println("GUI runnning thread: " + thread.getName());
        //controller.setRunningThread(thread);
    }

}
