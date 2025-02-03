package it.unibs.pajc.client;

import it.unibs.pajc.client.controller.BoardController;
import it.unibs.pajc.client.controller.GuiPlayer;
import it.unibs.pajc.game.controller.GameController;
import it.unibs.pajc.game.model.enums.PieceColor;

import java.awt.*;

public class EntryPoint {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    boolean remote = false;
                    if (remote) {
                        //Socket socket = new Socket("localhost", 12345);
                        //ClientPlayer player = new ClientPlayer(socket);
                        //Socket socket2 = new Socket("localhost", 12345);
                        //ClientPlayer player2 = new ClientPlayer(socket2);
                    }
                    else {
                        BoardController boardController = new BoardController();
                        GuiPlayer white = new GuiPlayer(PieceColor.WHITE, boardController);
                        GuiPlayer black = new GuiPlayer(PieceColor.BLACK, boardController);

                        GameController gameController = new GameController(0 ,white, black);
                        Thread t = new Thread(gameController);
                        t.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
