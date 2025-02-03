package it.unibs.pajc.client.controller;

import it.unibs.pajc.client.view.MainFrame;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.client.view.ChessBoardView;
import it.unibs.pajc.client.utils.Constants;

import java.awt.event.WindowAdapter;
import java.util.Map;
import java.util.Set;

public class BoardController {
    private final ChessBoardView view;
    private final MainFrame frame;

    public BoardController() {
        this.frame = new MainFrame(this);
        this.view = frame.getChessBoardView();
        this.frame.setVisible(true);
    }

    public void setPlayerColor(PieceColor color) {
        this.view.setInverted(color == PieceColor.BLACK);
    }

    public void setPosition(String fenPosition) {
        System.out.println("CONTROLLER SET POSITION THREAD: " + Thread.currentThread().getName());
        view.setPieces(fenPosition);
    }

    public void setLegalMoves(String fenLegalMoves) {
        view.setLegalMoves(null);
    }

    public void setLastMove(String fenLastMove) {
        view.setLastMove(null);
    }


    public Move requireMove(PieceColor color) throws InterruptedException {
        return view.requireMove(color);
    }

    public void restart() {
        view.resetView();
    }

    public ChessBoardView getView() {
        return view;
    }

    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        view.setLegalMoves(legalMoves);
    }

    public void setOnCloseOperation(WindowAdapter w) {
        frame.addWindowListener(w);
    }

    public void closeView() {
        frame.setVisible(false);
        frame.dispose();
    }
}
