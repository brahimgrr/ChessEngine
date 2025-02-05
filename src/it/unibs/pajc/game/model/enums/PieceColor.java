package it.unibs.pajc.game.model.enums;

import java.io.Serializable;

public enum PieceColor implements Serializable {
    WHITE("white"),
    BLACK("black");

    private String name;
    private int code;

    PieceColor(String name) {
        this.name = name;
    }

    public PieceColor getOpposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public String getName() {
        return name;
    }
}
