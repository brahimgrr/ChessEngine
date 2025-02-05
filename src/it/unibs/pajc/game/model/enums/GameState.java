package it.unibs.pajc.game.model.enums;

public enum GameState {
    DRAW("No winner", null),
    WIN_WHITE("White player won the game", "white"),
    WIN_BLACK("Black player won the game", "black"),
    PLAYING("Playing", null),
    CONNECTION_FAILED("Connection failed", null),
    CONNECTION_LOST("Connection lost", null);

    private final String state;
    private final String winner;

    GameState(String s, String winner) {
        this.state = s;
        this.winner = winner;
    }

    public String getState() {
        return state;
    }

    public String getWinner() {
        return winner;
    }
}
