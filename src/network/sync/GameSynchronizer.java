package network.sync;

import backend.board.Board;
import backend.board.Tile;
import backend.controller.GameController;
import backend.move.Move;
import backend.move.MoveRecord;
import backend.pieces.Piece;
import backend.state.GameState;
import backend.state.TurnState;
import network.common.Message;
import network.common.MessageFactory;
import network.server.SessionManager;
import util.formatter.MoveFormatter;

public class GameSynchronizer {

    private final GameController gameController;

    public GameSynchronizer(GameController gameController) {
        this.gameController = gameController;
    }

    public void syncToClient(SessionManager client) {
        Board board = gameController.getGameManager().getBoard();
        GameState gameState = gameController.getGameState();

        // 1. Lấy dữ liệu cơ bản
        String boardData   = encodeBoardData(board);
        String currentTurn = gameState.getCurrentTurn() == TurnState.WHITE_TURN ? "WHITE" : "BLACK";
        boolean isCheck    = gameState.isCheck();
        boolean isGameOver = gameState.isGameOver();
        String matchState  = gameState.getMatchState().name();

        // 2. Lấy dữ liệu Lịch sử & Vệt vàng highlight
        String moveHistoryData   = encodeMoveHistory();
        String lastMoveHighlight = encodeLastMoveHighlight();

        // 3. Đóng gói 7 tham số vào Message và gửi đi
        Message syncMsg = MessageFactory.syncState(
                boardData, currentTurn, isCheck, isGameOver, matchState,
                moveHistoryData, lastMoveHighlight
        );

        client.send(syncMsg);
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    private String encodeBoardData(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < Board.BOARD_SIZE; r++) {
            for (int c = 0; c < Board.BOARD_SIZE; c++) {
                Tile tile = board.getTileByCoordinate(r, c);
                if (tile != null && tile.getPiece() != null) {
                    Piece piece = tile.getPiece();
                    sb.append(r).append(",").append(c).append(",")
                            .append(piece.getSymbol()).append(",")
                            .append(piece.getColor().name()).append(";");
                }
            }
        }
        return sb.toString();
    }

    private String encodeMoveHistory() {
        StringBuilder sb = new StringBuilder();

        // Lấy đối tượng MoveHistory
        var historyObj = gameController.getGameManager().getMoveManager().getHistory();

        // GỌI HÀM .getHistory() ĐỂ LẤY RA DANH SÁCH (List<MoveRecord>)
        for (MoveRecord record : historyObj.getHistory()) {
            String formatted = MoveFormatter.formatDetailedMove(record.getMove());
            sb.append(formatted).append("|");
        }
        return sb.toString();
    }

    private String encodeLastMoveHighlight() {
        MoveRecord lastRecord = gameController.getGameManager().getLastMove();
        if (lastRecord != null) {
            Move move = lastRecord.getMove();
            // Trả về chuỗi: fromRow,fromCol,toRow,toCol
            return move.getFrom().getRow() + "," + move.getFrom().getCol() + "," +
                    move.getTo().getRow() + "," + move.getTo().getCol();
        }
        return "";
    }
}