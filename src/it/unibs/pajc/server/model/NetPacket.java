package it.unibs.pajc.server.model;

import java.io.Serializable;

public class NetPacket implements Serializable {
    public static String SET_POSITION_FEN = "SET_POSITION_FEN";

    public static String REQUEST_MOVE = "REQUEST_MOVE";
    public static String RESPONSE_MOVE = "RESPONSE_MOVE";

    public static String SET_LEGAL_MOVES = "SET_LEGAL_MOVES";
    public static String SET_PLAYER_COLOR = "SET_PLAYER_COLOR";

    public static String REQUIRE_COLOR = "REQUIRE_PLAYER_COLOR";
    public static String RESPONSE_COLOR = "RESPONSE_PLAYER_COLOR";

    public final String type;
    public final Object data;

    public NetPacket(String type, Object data) {
        this.type = type;
        this.data = data;
    }
}
