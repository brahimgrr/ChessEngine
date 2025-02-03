package it.unibs.pajc.game.model.enums;

public enum PieceType {
    KING_BLACK("King", "K", Integer.MAX_VALUE, PieceColor.BLACK),
    KING_WHITE("King", "k", Integer.MAX_VALUE, PieceColor.WHITE),
    QUEEN_BLACK("Queen", "Q", 9, PieceColor.BLACK),
    QUEEN_WHITE("Queen", "q", 9, PieceColor.WHITE),
    ROOK_BLACK("Rook", "R", 5, PieceColor.BLACK),
    ROOK_WHITE("Rook", "r", 5, PieceColor.WHITE),
    KNIGHT_BLACK("Knight", "N", 3, PieceColor.BLACK),
    KNIGHT_WHITE("Knight", "n", 3, PieceColor.WHITE),
    BISHOP_BLACK("Bishop", "B", 3, PieceColor.BLACK),
    BISHOP_WHITE("Bishop", "b", 3, PieceColor.WHITE),
    PAWN_BLACK("Pawn", "P", 1, PieceColor.BLACK),
    PAWN_WHITE("Pawn", "p", 1, PieceColor.WHITE);

    public final String name;
    public final String shortName;
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
