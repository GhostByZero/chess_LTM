package backend.state;

public enum TurnState {
    WHITE_TURN, BLACK_TURN;

    public TurnState nextTurn() {
        return this == WHITE_TURN ?
                BLACK_TURN : WHITE_TURN;
    }
}
