package it.unibs.pajc.client;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.controller.GuiPlayer;
import it.unibs.pajc.client.view.GameSelectionFrame;
import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.awt.*;

/**
 * Client entry point
 */
public class EntryPoint {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                GameSelectionFrame frame = new GameSelectionFrame();
                frame.setVisible(true);
            }
        });
    }
}
