package backend.engine;

import backend.pieces.PieceColor;
import backend.state.GameState;
import backend.state.TurnState;

public class TurnManager {
    private final GameState gameState;
    public TurnManager(GameState gameState) {
        this.gameState = gameState;
    }
    public TurnState getCurrentTurn() {
        return gameState.getCurrentTurn();
    }

    public boolean isWhiteTurn() {
        return gameState.getCurrentTurn() == TurnState.WHITE_TURN;
    }

    public boolean isBlackTurn() {
        return gameState.getCurrentTurn() == TurnState.BLACK_TURN;
    }

    public boolean isCorrectTurn(PieceColor pieceColor) {
        if (pieceColor == PieceColor.WHITE) {
            return isWhiteTurn();
        }
        return isBlackTurn();
    }

    public void switchTurn() {
        gameState.switchTurn();
    }
}
