package it.unibs.pajc.server.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.model.NetPacket;

import java.io.*;
import java.net.Socket;

public class RemotePlayer extends Player {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public RemotePlayer(Socket socket) throws IOException {
        super();
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush();  // Ensure stream header is written
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void setPosition(String fenString) {
        System.out.println("setPosition");
        System.out.println("REMOTE SET POSITION THREAD: " + Thread.currentThread().getName());
        NetPacket packet = new NetPacket(NetPacket.SET_POSITION_FEN, fenString);
        try {
            output.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLegalMoves(MoveMap legalMoves) {
        System.out.println("setLegalMoves");
        NetPacket packet = new NetPacket(NetPacket.SET_LEGAL_MOVES, legalMoves);
        try {
            output.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Move requireMove() throws InterruptedException {
        try {
            NetPacket request = new NetPacket(NetPacket.REQUEST_MOVE, getColor());
            output.writeObject(request);
            System.out.println("SENT REQUEST");
            Object response = input.readObject();
            System.out.println("RECEIVE RESPONSE");
            if (response instanceof NetPacket && ((NetPacket) response).data instanceof Move) {
                return (Move) ((NetPacket) response).data;
            }
            else {
                throw new RuntimeException("Invalid response");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public PieceColor requireColor() throws IOException {
        try {
            NetPacket request = new NetPacket(NetPacket.REQUIRE_COLOR, getColor());
            output.writeObject(request);
            System.out.println("SENT REQUEST");
            Object response = input.readObject();
            System.out.println("RECEIVE RESPONSE");
            if (response instanceof NetPacket && ((NetPacket) response).data instanceof PieceColor) {
                return (PieceColor) ((NetPacket) response).data;
            }
            else {
                throw new RuntimeException("Invalid response");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new IOException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setColor(PieceColor color) {
        /*try {
            NetPacket packet = new NetPacket(NetPacket.SET_PLAYER_COLOR, color);
            output.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

    }

    @Override
    public void terminate() {

    }

    @Override
    public void setRunningThread(Thread thread) {
        return;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void performHandshake() throws IOException {
        PieceColor pieceColor = requireColor();
        super.setColor(pieceColor);
    }
}
