package backend.state;

import backend.pieces.PieceColor;

public class GameState {
    private TurnState currentTurn;
    private MatchState matchState;
    private PlayerState whitePlayerState;
    private PlayerState blackPlayerState;
    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private boolean gameOver;

    private PieceColor winner;

    public GameState() {
        this.currentTurn = TurnState.WHITE_TURN;

        this.matchState = MatchState.WAITING;

        this.whitePlayerState = PlayerState.WAITING;
        this.blackPlayerState = PlayerState.WAITING;

        this.check = false;
        this.checkmate = false;
        this.stalemate = false;
        this.gameOver = false;

        this.winner = null;
    }
    /*
     * =========================
     * Turn
     * =========================
     */

    public TurnState getCurrentTurn() {
        return currentTurn;
    }

    public void switchTurn() {
        currentTurn = currentTurn.nextTurn();
    }

    public void setCurrentTurn(TurnState currentTurn) {
        this.currentTurn = currentTurn;
    }

    /*
     * =========================
     * Match State
     * =========================
     */

    public MatchState getMatchState() {
        return matchState;
    }

    public void setMatchState(MatchState matchState) {
        this.matchState = matchState;
    }

    /*
     * =========================
     * Player State
     * =========================
     */

    public PlayerState getWhitePlayerState() {
        return whitePlayerState;
    }

    public void setWhitePlayerState(PlayerState whitePlayerState) {
        this.whitePlayerState = whitePlayerState;
    }

    public PlayerState getBlackPlayerState() {
        return blackPlayerState;
    }

    public void setBlackPlayerState(PlayerState blackPlayerState) {
        this.blackPlayerState = blackPlayerState;
    }

    /*
     * =========================
     * Check / Checkmate
     * =========================
     */

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public boolean isStalemate() {
        return stalemate;
    }

    public void setStalemate(boolean stalemate) {
        this.stalemate = stalemate;
    }

    /*
     * =========================
     * Game Over
     * =========================
     */

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /*
     * =========================
     * Winner
     * =========================
     */

    public PieceColor getWinner() {
        return winner;
    }

    public void setWinner(PieceColor winner) {
        this.winner = winner;
    }

    /*
     * =========================
     * Multiplayer Support
     * =========================
     */

    public boolean areBothPlayersConnected() {
        return whitePlayerState != PlayerState.DISCONNECTED &&
                blackPlayerState != PlayerState.DISCONNECTED;
    }

    public boolean isAnyPlayerDisconnected() {
        return whitePlayerState == PlayerState.DISCONNECTED ||
                blackPlayerState == PlayerState.DISCONNECTED;
    }

    public void pauseMatchDueToDisconnect() {
        matchState = MatchState.DISCONNECTED;
    }

    public void resumeMatch() {
        if (areBothPlayersConnected()) {
            matchState = MatchState.PLAYING;
        }
    }

    public void surrender(PieceColor loser) {
        gameOver = true;
        matchState = MatchState.ENDED;

        if (loser == PieceColor.WHITE) {
            winner = PieceColor.BLACK;
            whitePlayerState = PlayerState.SURRENDERED;
        }
        else {
            winner = PieceColor.WHITE;
            blackPlayerState = PlayerState.SURRENDERED;
        }
    }

    public void endGame(PieceColor winner) {
        this.winner = winner;
        this.gameOver = true;
        this.matchState = MatchState.ENDED;
    }
}
