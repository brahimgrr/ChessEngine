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

/**
 * ChessBoardView controller
 */
public class BoardController {
    private final ChessBoardView view;
    private final MainFrame frame;

    public BoardController() {
        this.frame = new MainFrame();
        this.view = frame.getChessBoardView();
        this.frame.setVisible(true);
    }

    public void setPlayerColor(PieceColor color) {
        this.view.setInverted(color == PieceColor.BLACK);
    }

    public void setPosition(String fenPosition) {
        view.setPieces(fenPosition);
    }

    public Move requireMove(PieceColor color) throws InterruptedException {
        return view.requireMove(color);
    }

    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        view.setLegalMoves(legalMoves);
    }

    public void setLastMove(Move move) {
        view.setLastMove(move);
    }

    public void setOnCloseOperation(WindowAdapter w) {
        frame.addWindowListener(w);
    }

    public void closeView() {
        frame.setVisible(false);
        frame.dispose();
    }
}
