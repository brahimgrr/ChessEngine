package it.unibs.pajc.client.utils;

import java.awt.*;

/**
 * GUI constants
 */
public class Constants {
    //public static final String DEFAULT_POSITION = "8/2k5/8/8/8/6r1/r7/3K4";
    public static int TILE_SIZE = 50;
    public static final int COLS = 8;
    public static final int ROWS = 8;
    public static final Color DARK_TILE  = new Color	(118,150,86);
    public static final Color LIGHT_TILE = new Color (238,238,210);
    public static final Color DARK_FOCUSED_TILE = DARK_TILE.brighter();
    public static final Color LIGHT_FOCUSED_TILE = LIGHT_TILE.darker();
}
