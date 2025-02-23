package it.unibs.pajc.client.controller;

import it.unibs.pajc.client.view.MainFrame;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.client.view.ChessBoardView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ChessBoardView controller
 */
public class BoardController {
    private final ChessBoardView view;
    private final JPanel statusPanel;
    private final MainFrame frame;

    private final Map<Location, Set<Move>> legalMoves;
    private String fenPosition;

    public BoardController() {
        this.legalMoves = new HashMap<>();

        this.frame = new MainFrame(this);
        this.view = frame.getChessBoardView();
        this.statusPanel = frame.getStatusPanel();
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
        this.legalMoves.clear();
        this.legalMoves.putAll(legalMoves);
    }

    public Map<Location, Set<Move>> getLegalMoves() {
        return legalMoves;
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
