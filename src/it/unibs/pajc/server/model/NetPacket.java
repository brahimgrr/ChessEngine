package it.unibs.pajc.server.model;

import java.io.Serializable;

/**
 * Packet used to handle networking between client and server
 * Implements Serializable to be used in an Object Stream
 */
public class NetPacket implements Serializable {
    public static final String SET_POSITION_FEN = "SET_POSITION_FEN";

    public static final String REQUEST_MOVE = "REQUEST_MOVE";
    public static final String RESPONSE_MOVE = "RESPONSE_MOVE";

    public static final String SET_LEGAL_MOVES = "SET_LEGAL_MOVES";
    public static final String SET_PLAYER_COLOR = "SET_PLAYER_COLOR";
    public static final String SET_LAST_MOVE = "SET_LAST_MOVE";

    public static final String REQUIRE_COLOR = "REQUIRE_PLAYER_COLOR";
    public static final String RESPONSE_COLOR = "RESPONSE_PLAYER_COLOR";

    public static final String RESPONSE_COLOR_BOT = "RESPONSE_COLOR_BOT";

    public static final String SET_GAME_STATE = "SET_GAME_STATE";

    public static final String KEEP_ALIVE = "KEEP_ALIVE";
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    //Packet type identification
    public final String type;
    //Data associated with the packet
    public final Object data;
    //Optional data to be associated with the packet
    public final Object options;

    /**
     * Default constructor, null options
     * @param type packet type
     * @param data packet data
     */
    public NetPacket(String type, Object data) {
        this.type = type;
        this.data = data;
        this.options = null;
    }

    /**
     * constructor to specify optional data
     * @param type packet type
     * @param data packet data
     * @param options optional data
     */
    public NetPacket(String type, Object data, Object options) {
        this.type = type;
        this.data = data;
        this.options = options;
    }
}
