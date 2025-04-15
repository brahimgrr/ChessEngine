package it.unibs.pajc.game.model.enums;

public enum PieceType {
    KING_BLACK("King", "K", Integer.MAX_VALUE, PieceColor.BLACK),
    KING_WHITE("King", "k", Integer.MAX_VALUE, PieceColor.WHITE),
    QUEEN_BLACK("Queen", "Q", 1800, PieceColor.BLACK),
    QUEEN_WHITE("Queen", "q", 1800, PieceColor.WHITE),
    ROOK_BLACK("Rook", "R", 500, PieceColor.BLACK),
    ROOK_WHITE("Rook", "r", 500, PieceColor.WHITE),
    KNIGHT_BLACK("Knight", "N", 300, PieceColor.BLACK),
    KNIGHT_WHITE("Knight", "n", 300, PieceColor.WHITE),
    BISHOP_BLACK("Bishop", "B", 300, PieceColor.BLACK),
    BISHOP_WHITE("Bishop", "b", 300, PieceColor.WHITE),
    PAWN_BLACK("Pawn", "P", 100, PieceColor.BLACK),
    PAWN_WHITE("Pawn", "p", 100, PieceColor.WHITE);

    public final String name;
    public final String shortName;
    //piece standard value
    public final int value;
    public final PieceColor color;

    PieceType(String name, String shortName, int value, PieceColor color) {
        this.name = name;
        this.shortName = shortName;
        this.value = value;
        this.color = color;
    }

    public static PieceType getPieceType(String shortName) {
        return switch (shortName) {
            case "K" -> KING_BLACK;
            case "k" -> KING_WHITE;
            case "Q" -> QUEEN_BLACK;
            case "q" -> QUEEN_WHITE;
            case "N" -> KNIGHT_BLACK;
            case "n" -> KNIGHT_WHITE;
            case "P" -> PAWN_BLACK;
            case "p" -> PAWN_WHITE;
            case "R" -> ROOK_BLACK;
            case "r" -> ROOK_WHITE;
            case "B" -> BISHOP_BLACK;
            case "b" -> BISHOP_WHITE;
            default -> throw new IllegalArgumentException("Invalid piece short name: " + shortName);
        };
    }

}
