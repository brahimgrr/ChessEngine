package it.unibs.pajc.game.model.enums;

import java.io.Serializable;

public enum PieceColor implements Serializable {
    WHITE("white",0),
    BLACK("black", 1);

    private String name;
    private int code;

    PieceColor(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public PieceColor getOpposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
