package it.unibs.pajc.client.controller;

import it.unibs.pajc.client.utils.ImageLoader;
import it.unibs.pajc.client.view.MainFrame;
import it.unibs.pajc.client.view.PieceView;
import it.unibs.pajc.client.view.StatusView;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.client.view.ChessBoardView;
import it.unibs.pajc.game.model.enums.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.*;
import java.util.List;

/**
 * ChessBoardView controller
 */
public class BoardController {
    private final ChessBoardView view;
    private final StatusView panel;
    private final MainFrame frame;
    private final Map<Location, Set<Move>> legalMoves;
    private final LinkedList<String> movesHistory;
    private final Map<PieceColor, List<Image>> captures;

    public BoardController() {
        this.legalMoves = new HashMap<>();
        this.movesHistory = new LinkedList<>();
        this.captures = new HashMap<>();
        this.captures.put(PieceColor.WHITE, new ArrayList<>());
        this.captures.put(PieceColor.BLACK, new ArrayList<>());

        this.frame = new MainFrame(this);

        this.view = frame.getChessBoardView();
        this.panel = frame.getStatusPanel();

        this.frame.setVisible(true);
    }

    public void setPlayerColor(PieceColor color) {
        this.view.setInverted(color == PieceColor.BLACK);
    }

    public void setPosition(String fenPosition) {
        view.setPieces(fenPosition);
    }

    public Move requireMove(PieceColor color) throws InterruptedException {
        Move move = view.requireMove(color);
        this.legalMoves.clear();
        return move;
    }

    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        this.legalMoves.clear();
        this.legalMoves.putAll(legalMoves);
    }

    public Map<Location, Set<Move>> getLegalMoves() {
        return legalMoves;
    }

    public LinkedList<String> getMovesHistory() {
        return movesHistory;
    }

    public Map<PieceColor, List<Image>> getCaptures() {
        return captures;
    }

    public void setLastMove(Move move) {
        if (move == null) {
            return;
        }
        movesHistory.add(move.toString());
        if (move.isCapture()) {
            PieceType type = move.getCaptureType();
            captures.get(type.color).add(ImageLoader.pieceImages.get(type));
        }
        panel.revalidate();
        panel.repaint();
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
