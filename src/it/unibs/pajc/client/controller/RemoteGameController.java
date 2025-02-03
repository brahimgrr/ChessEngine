package it.unibs.pajc.client.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.model.NetPacket;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteGameController implements Runnable {
    private final int gameID;
    private final Player player;
    private final String ip;
    private final int port;
    private Socket socket;

    private Thread gameThread;
    private boolean isPlaying = false;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    public RemoteGameController(int gameId, GuiPlayer guiPlayer, String ip, int port) {
        this.gameID = gameId;
        this.player = guiPlayer;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        gameThread = Thread.currentThread();
        isPlaying = true;
        try {
            Socket socket = new Socket(ip, port);
            handleConnection(socket);
        } catch (IOException e) {
            player.terminate();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Unable to connect to server!", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection(Socket socket) throws IOException, ClassNotFoundException {
        this.outStream = new ObjectOutputStream(socket.getOutputStream());
        this.outStream.flush();  // Ensure stream header is written
        this.inStream = new ObjectInputStream(socket.getInputStream());

        while (true) {
            Object obj = this.inStream.readObject();
            System.out.println(player.getColor() + " " + "PACKET RECEIVED");
            if (obj instanceof NetPacket packet) {
                if (NetPacket.SET_POSITION_FEN.equals(packet.type)) {
                    System.out.println("Set position fen");
                    String fen = (String) packet.data;
                    player.setPosition(fen);
                }
                else if (NetPacket.SET_LEGAL_MOVES.equals(packet.type)) {
                    System.out.println("Set legal moves");
                    MoveMap moveMap = (MoveMap) packet.data;
                    player.setLegalMoves(moveMap);
                }
                else if (NetPacket.REQUEST_MOVE.equals(packet.type)) {
                    System.out.println("request move");
                    Move move = null;
                    try {
                        move = player.requireMove();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("move made");
                    NetPacket responsePacket = new NetPacket(NetPacket.RESPONSE_MOVE, move);
                    outStream.writeObject(responsePacket);
                    System.out.println("MOVE SENT");
                }
                else if (NetPacket.SET_PLAYER_COLOR.equals(packet.type)) {
                    System.out.println("Set player color");
                    PieceColor color = (PieceColor) packet.data;
                    player.setColor(color);
                }
                else if (NetPacket.REQUIRE_COLOR.equals(packet.type)) {
                    System.out.println("require color");
                    NetPacket responsePacket = new NetPacket(NetPacket.RESPONSE_COLOR, player.getColor());
                    outStream.writeObject(responsePacket);
                }
            }
        }
    }


    public void stopGame() {
        isPlaying = false;
        System.out.println("Interrupting game loop: " + gameThread.getName());
        if (gameThread != null) {
            gameThread.interrupt(); // Interrupt the game thread
        }
    }
}
