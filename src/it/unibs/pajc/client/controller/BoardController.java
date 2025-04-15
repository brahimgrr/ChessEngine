package it.unibs.pajc.client.controller;

import it.unibs.pajc.client.model.BoardModel;
import it.unibs.pajc.client.view.MainFrame;
import it.unibs.pajc.client.view.PieceView;
import it.unibs.pajc.game.model.Location;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.Piece;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ChessBoardView controller
 */
public class BoardController {
    private final MainFrame view;
    private final BoardModel model;

    /**
     * Constructor for specifying board location
     * @param location screen location
     */
    public BoardController(Point location) {
        this.model = new BoardModel();

        this.view = new MainFrame(this, location);

        this.view.setVisible(true);
    }

    public BoardController() {
        this(null);
    }

    /**
     * Sets board main color
     * -> used for inverting board for black players
     * @param color main player color
     */
    public void setPlayerColor(PieceColor color) {
        model.setMainPlayerColor(color);
    }

    /**
     * Sets model's pieces and converts the model's
     * representation of pieces into views
     * @param fenPosition board position
     */
    public void setPosition(String fenPosition) {
        model.setPieces(fenPosition);

        List<PieceView> pieceViews = model.getPieces().stream()
                .map(p -> new PieceView(p.getType(), p.getLocation(), model.getMainPlayerColor() == PieceColor.BLACK))
                .collect(Collectors.toList());
        // PieceView injection
        view.getChessBoardView().setPieceViews(pieceViews);
    }

    /**
     * Requests and return move made on board
     * @param color player queried, used for multi-access board
     * @return move
     * @throws InterruptedException interruption while making move
     */
    public Move requireMove(PieceColor color) throws InterruptedException {
        Move move = view.getChessBoardView().requireMove(color);
        model.getLegalMoves().clear();

        return move;
    }

    /**
     * Sets available legal moves in the model
     * @param legalMoves legal moves
     */
    public void setLegalMoves(Map<Location, Set<Move>> legalMoves) {
        model.setLegalMoves(legalMoves);
        model.getLegalMoves().clear();
        model.getLegalMoves().putAll(legalMoves);
    }

    /**
     * Set's last move made on board
     * @param move last move
     */
    public void setLastMove(Move move) {
        model.addMove(move);

        view.getStatusPanelView().revalidate();
        view.getStatusPanelView().repaint();
    }

    /**
     * Returns last move saved in model
     * @return last move
     */
    public Move getLastMove() {
        if (!model.getMovesHistory().isEmpty()) {
            return model.getMovesHistory().get(model.getMovesHistory().size() - 1);
        }
        else {
            return null;
        }
    }

    /**
     * Legal moves getter
     * @return legal moves
     */
    public Map<Location, Set<Move>> getLegalMoves() {
        return model.getLegalMoves();
    }

    /**
     * Move history as text list
     * @return move history
     */
    public List<String> getTextMovesHistory() {
        List<String> history = new ArrayList<>();

        for (Move move : model.getMovesHistory()) {
            history.add(move.toString());
        }
        return history;
    }

    /**
     * Main player color getter
     * @return main player color
     */
    public PieceColor getMainPlayerColor() {
        return model.getMainPlayerColor();
    }

    /**
     * Converts model's saved capture and returns them
     * as View representation
     * @return capture views
     */
    public Map<PieceColor, List<PieceView>> getCaptures() {
        Map<PieceColor, List<PieceView>> captures = new HashMap<>();
        captures.put(PieceColor.WHITE, new ArrayList<>());
        captures.put(PieceColor.BLACK, new ArrayList<>());

        for (Piece capture : model.getCaptures()) {
            PieceView captureView = new PieceView(capture.type, new Location(0,0), false);
            captures.get(capture.getColor()).add(captureView);
        }

        return captures;
    }

    public void setOnCloseOperation(WindowAdapter w) {
        view.addWindowListener(w);
    }

    public void closeView() {
        view.setVisible(false);
        view.dispose();
    }
}
