package it.unibs.pajc.client.utils;

import java.awt.*;

public class Constants {
    public static final String DEFAULT_POSITION = "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr";
    public static int TILE_SIZE = 50;
    public static final int CAPTURE_TILE = (int) (TILE_SIZE * 0.1);
    public static final int MOVE_TILE = (int) (TILE_SIZE * 0.3);
    public static final int COLS = 8;
    public static final int ROWS = 8;
    public static final int CHESSBOARD_SIZE = TILE_SIZE * COLS;
    public static final int GAME_PANEL_SIZE = 200;
    public static final Color DARK_TILE  = new Color	(118,150,86);
    public static final Color LIGHT_TILE = new Color (238,238,210);
    public static final Color DARK_FOCUSED_TILE = DARK_TILE.brighter();
    public static final Color LIGHT_FOCUSED_TILE = LIGHT_TILE.darker();
}
