package it.unibs.pajc.server.controller;

import it.unibs.pajc.game.controller.Player;
import it.unibs.pajc.game.model.Move;
import it.unibs.pajc.game.model.MoveMap;
import it.unibs.pajc.game.model.enums.GameState;
import it.unibs.pajc.game.model.enums.PieceColor;
import it.unibs.pajc.server.model.NetPacket;

import java.io.*;
import java.net.Socket;

/**
 * Implementation of Player class, representing a remote player
 */
public class RemotePlayer extends Player {
    private static final boolean DEBUG = false;
    //current player socket connection
    private final Socket socket;
    //object streams
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    //flag if player requires to be matched against a bot
    private boolean requireBot = false;

    public RemotePlayer(Socket socket) throws IOException {
        super();
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        // make sure stream header is written
        this.output.flush();
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * sends current position to the remote player
     * @param fenString the fen string representing the current game position.
     * @throws IOException
     */
    @Override
    public void setPosition(String fenString) throws IOException {
        try {
            log("setPosition");
            NetPacket packet = new NetPacket(NetPacket.SET_POSITION_FEN, fenString);
            output.writeObject(packet);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void setLastMove(Move move) throws IOException {
        try {
            log("setLegalMoves");
            NetPacket packet = new NetPacket(NetPacket.SET_LAST_MOVE, move);
            output.writeObject(packet);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * send the remote player its current legal moves
     * @param legalMoves a move map containing the legal moves.
     * @throws IOException
     */
    @Override
    public void setLegalMoves(MoveMap legalMoves) throws IOException {
        try {
            log("setLegalMoves");
            NetPacket packet = new NetPacket(NetPacket.SET_LEGAL_MOVES, legalMoves);
            output.writeObject(packet);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * queries the remote player and waits for a move response
     * @return move if query successfully
     * @throws IOException
     */
    @Override
    public Move requireMove() throws IOException {
        try {
            NetPacket request = new NetPacket(NetPacket.REQUEST_MOVE, getColor());
            output.writeObject(request);
            log("SENT REQUEST");
            Object response = input.readObject();
            log("RECEIVE RESPONSE");
            if (response instanceof NetPacket && ((NetPacket) response).data instanceof Move) {
                return (Move) ((NetPacket) response).data;
            }
            else {
                throw new IOException("Invalid response");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new IOException(e);
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid response, class non found");
        }
    }

    /**
     * send the game state to the remote player
     * @param gameState the current state of the game.
     * @throws IOException
     */
    @Override
    public void setGameState(GameState gameState) throws IOException {
        try {
            log("setLegalMoves");
            NetPacket packet = new NetPacket(NetPacket.SET_GAME_STATE, gameState);
            output.writeObject(packet);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * performs a handshake to retrieve and return the remote player color
     * @return remote player's color
     * @throws IOException
     */
    public PieceColor requireColor() throws IOException {
        try {
            NetPacket request = new NetPacket(NetPacket.REQUIRE_COLOR, getColor());
            output.writeObject(request);
            log("SENT REQUEST");
            Object response = input.readObject();
            log("RECEIVE RESPONSE");
            if (response instanceof NetPacket && ((NetPacket) response).data instanceof PieceColor) {
                NetPacket responsePacket = (NetPacket) response;
                //requireBot flag is set
                if (responsePacket.options instanceof Boolean) {
                    //update current player with the requireBot flag
                    this.requireBot = (Boolean) responsePacket.options;
                }
                PieceColor color = (PieceColor) responsePacket.data;
                this.setColor(color);
                return color;
            }
            else {
                throw new IOException("Invalid response");
            }
        } catch (IOException e) {
            throw new IOException(e);
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid response, class non found");
        }
    }

    /**
     * update the current player instance with the remote player color
     * @param color the color of the player's pieces.
     */
    @Override
    public void setColor(PieceColor color) {
        super.setColor(color);
    }

    @Override
    public boolean isAlive() {
        return socket.isConnected() && !socket.isClosed();
    }

    /**
     * getter for requireBot flag
     * @return requireBot
     */
    public boolean requireBot() {
        return requireBot;
    }

    /**
     * terminates the current player connection
     */
    @Override
    public void terminate() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * utility to print debug messages
     * @param message message
     */
    private void log(String message) {
        if (!DEBUG) {
            return;
        }
        System.out.println("Remote player " + getColor() + ": " + message);
    }
}
