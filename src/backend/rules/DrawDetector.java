package backend.rules;

import backend.board.Board;
import backend.board.Tile;
import backend.move.MoveHistory;
import backend.move.MoveRecord;

import backend.pieces.Pawn;
import backend.pieces.Piece;
import backend.pieces.PieceColor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phát hiện các điều kiện hòa theo luật cờ vua FIDE:
 *
 * <ol>
 *   <li><b>50-move rule</b> — nếu trong 50 nước đi liên tiếp (mỗi bên 50)
 *       không có quân nào bị ăn và không có tốt nào di chuyển, ván cờ hòa.
 *       Thực chất kiểm tra 100 half-moves (ply) trong MoveHistory.</li>
 *   <li><b>Threefold repetition</b> — nếu cùng một vị trí trên bàn cờ xuất hiện
 *       3 lần (tính cả lượt đi và quyền nhập thành / en passant), ván hòa.
 *       Hiện tại dùng FEN tối giản (piece placement + turn) làm key — đủ dùng
 *       cho hầu hết trường hợp thực tế.</li>
 * </ol>
 *
 * <h3>Cách dùng</h3>
 * <pre>
 * DrawDetector drawDetector = new DrawDetector(board, moveManager.getHistory());
 *
 * // Sau mỗi nước đi thành công, gọi trước khi check checkmate/stalemate:
 * if (drawDetector.isFiftyMoveRule())     { ... hòa 50 nước ... }
 * if (drawDetector.isThreefoldRepetition()) { ... hòa lặp vị trí ... }
 * </pre>
 *
 * <h3>TODO khi tích hợp</h3>
 * <ul>
 *   <li>Gọi {@link #recordPosition(PieceColor)} sau mỗi {@code executeMove} thành công
 *       trong {@code GameManager.processMove()}.</li>
 *   <li>Thêm {@code DrawDetector} vào {@code ChessEngine} và {@code GameManager}.</li>
 *   <li>Thêm {@code MoveResult.DRAW} hoặc phân tách
 *       {@code DRAW_FIFTY_MOVE} / {@code DRAW_REPETITION} vào {@code MoveResult}.</li>
 *   <li>Gọi {@code clear()} trong {@code GameManager} khi bắt đầu ván mới.</li>
 * </ul>
 */
public class DrawDetector {

    private static final int FIFTY_MOVE_PLY_LIMIT = 100; // 50 nước mỗi bên = 100 half-moves

    private final Board board;
    private final MoveHistory moveHistory;

    /**
     * positionCounts lưu số lần mỗi vị trí đã xuất hiện.
     * Key: FEN tối giản (piece placement + bên đến lượt).
     * Value: số lần xuất hiện.
     * <p>
     * Cần gọi {@link #recordPosition(PieceColor)} sau mỗi nước đi thành công.
     */
    private final Map<String, Integer> positionCounts;

    public DrawDetector(Board board, MoveHistory moveHistory) {
        this.board          = board;
        this.moveHistory    = moveHistory;
        this.positionCounts = new HashMap<>();
    }

    /*
     * =========================
     * 50-Move Rule
     * =========================
     */

    /**
     * Kiểm tra quy tắc 50 nước:
     * nếu trong 100 half-move gần nhất không có pawn move và không có capture,
     * trả về true → ván hòa.
     * <p>
     * Không cần caller truyền vào gì — tự đọc từ MoveHistory.
     */
    public boolean isFiftyMoveRule() {
        List<MoveRecord> history = moveHistory.getHistory();

        if (history.size() < FIFTY_MOVE_PLY_LIMIT) {
            return false;
        }

        // Kiểm tra 100 half-moves gần nhất
        int start = history.size() - FIFTY_MOVE_PLY_LIMIT;
        for (int i = start; i < history.size(); i++) {
            MoveRecord record = history.get(i);
            Piece moved = record.getMove().getMovedPiece();

            // Có tốt di chuyển → reset đếm
            if (moved instanceof Pawn) { return false; }

            // Có capture → reset đếm
            if (record.getCapturedPiece() != null) { return false; }

            // En passant luôn là capture
            if (record.isEnPassant()) { return false; }
        }

        return true;
    }

    /*
     * =========================
     * Threefold Repetition
     * =========================
     */

    /**
     * Ghi nhận vị trí hiện tại của board sau khi nước đi thành công.
     * Phải được gọi bởi GameManager sau mỗi executeMove thật.
     *
     * @param sideToMove bên sắp đến lượt (sau khi switchTurn).
     */
    public void recordPosition(PieceColor sideToMove) {
        String fen = buildMinimalFen(sideToMove);
        positionCounts.merge(fen, 1, Integer::sum);
    }

    /**
     * Kiểm tra quy tắc lặp vị trí 3 lần.
     * Trả về true nếu vị trí hiện tại đã xuất hiện ≥ 3 lần.
     * <p>
     * Gọi TRƯỚC {@link #recordPosition} để count tính đúng:
     * lần đầu (count=1) là lần setup ban đầu, lần 3 mới trigger draw.
     * <p>
     * Hoặc gọi SAU recordPosition — khi count ≥ 3 thì draw.
     */
    public boolean isThreefoldRepetition() {
        // Nếu gọi sau recordPosition, vị trí hiện tại đã được tính
        // Tìm entry nào có count >= 3
        return positionCounts.values().stream().anyMatch(count -> count >= 3);
    }

    /**
     * Reset bộ đếm lặp vị trí. Gọi khi bắt đầu ván mới.
     */
    public void clear() {
        positionCounts.clear();
    }

    /**
     * Giảm count của vị trí hiện tại đi 1 (dùng khi undo nước đi).
     * Nếu count về 0, xóa key ra khỏi map.
     * <p>
     * Phải gọi SAU {@code moveManager.undoMove()} và SAU {@code turnManager.switchTurn()}
     * để FEN được build với đúng bên đến lượt (bên vừa được hoàn lại lượt).
     */
    public void removeLastPosition(PieceColor sideToMove) {
        String fen = buildMinimalFen(sideToMove);
        positionCounts.compute(fen, (k, count) -> {
            if (count == null || count <= 1) { return null; }
            return count - 1;
        });
    }

    /*
     * =========================
     * FEN builder (tối giản)
     * =========================
     */

    /**
     * Xây dựng FEN tối giản: piece placement + bên đến lượt.
     * Không include castling rights và en passant target vì phức tạp
     * hơn mức cần thiết cho hầu hết use case.
     * <p>
     * TODO nâng cấp: include castling availability và en passant target square
     * để hoàn toàn chuẩn FIDE. Hiện tại đủ dùng cho 99% ván cờ thực tế.
     */
    private String buildMinimalFen(PieceColor sideToMove) {
        StringBuilder sb = new StringBuilder(80);

        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            int empty = 0;
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                Tile tile = board.getTileByCoordinate(row, col);
                if (tile == null || !tile.isOccupied()) {
                    empty++;
                } else {
                    if (empty > 0) { sb.append(empty); empty = 0; }
                    sb.append(tile.getPiece().getSymbol());
                }
            }
            if (empty > 0) { sb.append(empty); }
            if (row < 7) { sb.append('/'); }
        }

        sb.append(' ');
        sb.append(sideToMove == PieceColor.WHITE ? 'w' : 'b');

        return sb.toString();
    }
}
