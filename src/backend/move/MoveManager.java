package backend.move;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.pieces.Pawn;
import backend.pieces.Piece;
import backend.pieces.Rook;
import backend.rules.PromotionHandler;

/**
 * Thực thi và hoàn tác nước đi trên {@link Board}.
 * <p>
 * Mỗi lần {@link #executeMove(Move)} thành công, một {@link MoveRecord}
 * được đẩy vào {@link MoveHistory}. {@link #undoMove()} pop record đó
 * và khôi phục board về đúng trạng thái trước khi nước đi xảy ra.
 * <p>
 * Cặp executeMove / undoMove là nền tảng để các bước tiếp theo
 * (isMoveLegal, generateLegalMoves) có thể simulate nước đi mà
 * không làm hỏng trạng thái board thật.
 */
public class MoveManager {

    private final Board board;
    private final MoveHistory history;

    public MoveManager(Board board) {
        this.board   = board;
        this.history = new MoveHistory();
    }

    /*
     * =========================
     * Execute Move
     * =========================
     */

    /**
     * Thực thi nước đi lên board và ghi vào history.
     * <p>
     * Thứ tự thao tác:
     *   1. Validate cơ bản (null / bounds / có quân).
     *   2. Snapshot trạng thái trước khi thay đổi (wasMovedBefore, capturedPiece).
     *   3. Cập nhật board: clearTile(from), setPiece(to), setPosition, setMoved.
     *   4. Push MoveRecord vào history.
     *
     * @param move Nước đi cần thực thi.
     * @return {@link MoveResult#SUCCESS} nếu thành công, {@link MoveResult#INVALID} nếu không hợp lệ.
     */
    public MoveResult executeMove(Move move) {
        if (move == null) {
            return MoveResult.INVALID;
        }

        Position from = move.getFrom();
        Position to   = move.getTo();

        if (!board.isPositionValid(from) || !board.isPositionValid(to)) {
            return MoveResult.INVALID;
        }

        Tile fromTile = board.getTile(from);
        Tile toTile   = board.getTile(to);

        if (fromTile == null || toTile == null) {
            return MoveResult.INVALID;
        }

        Piece movingPiece = fromTile.getPiece();

        if (movingPiece == null) {
            return MoveResult.INVALID;
        }

        /*
         * Nhánh nhập thành (CASTLING): di chuyển cả King lẫn Rook.
         */
        if (move.getMoveType() == MoveType.CASTLING) {
            return executeCastling(move, fromTile, toTile, movingPiece);
        }

        /*
         * Nhánh en passant (EN_PASSANT): tốt ăn qua — quân bị ăn không ở toTile.
         */
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            return executeEnPassant(move, fromTile, toTile, movingPiece);
        }

        /*
         * Nhánh phong cấp (PROMOTION): tốt đến hàng cuối, thay bằng quân mới.
         */
        if (move.getMoveType() == MoveType.PROMOTION) {
            return executePromotion(move, fromTile, toTile, movingPiece);
        }

        /*
         * Nước đi thông thường / capture.
         */
        boolean wasMovedBefore = movingPiece.hasMoved();
        Piece capturedPiece = null;

        if (toTile.isOccupied()) {
            capturedPiece = toTile.getPiece();
            move.setCapturedPiece(capturedPiece);
            move.setMoveType(MoveType.CAPTURE);
        }

        fromTile.clearTile();
        toTile.setPiece(movingPiece);
        movingPiece.setPosition(to);
        movingPiece.setMoved(true);

        history.push(new MoveRecord(move, wasMovedBefore, capturedPiece));

        return MoveResult.SUCCESS;
    }

    /**
     * Thực thi nước nhập thành: di chuyển King đến ô đích, Rook đến ô kế bên.
     * <p>
     * Kingside  (King: col4→6, Rook: col7→5)
     * Queenside (King: col4→2, Rook: col0→3)
     */
    private MoveResult executeCastling(Move move, Tile kingFromTile, Tile kingToTile, Piece king) {
        boolean isKingSide = move.getTo().getCol() == 6;
        int row = move.getFrom().getRow();

        Position rookFrom = new Position(row, isKingSide ? 7 : 0);
        Position rookTo   = new Position(row, isKingSide ? 5 : 3);

        Tile rookFromTile = board.getTile(rookFrom);
        Tile rookToTile   = board.getTile(rookTo);

        if (rookFromTile == null || !rookFromTile.isOccupied()) {
            return MoveResult.INVALID;
        }

        Piece rook = rookFromTile.getPiece();
        if (!(rook instanceof Rook)) {
            return MoveResult.INVALID;
        }

        boolean kingWasMoved = king.hasMoved();
        boolean rookWasMoved = rook.hasMoved();

        /* Di chuyển King */
        kingFromTile.clearTile();
        kingToTile.setPiece(king);
        king.setPosition(move.getTo());
        king.setMoved(true);

        /* Di chuyển Rook */
        rookFromTile.clearTile();
        rookToTile.setPiece(rook);
        rook.setPosition(rookTo);
        rook.setMoved(true);

        history.push(new MoveRecord(move, kingWasMoved, rook, rookFrom, rookTo, rookWasMoved));

        return MoveResult.SUCCESS;
    }

    /**
     * Thực thi en passant:
     *   - Di chuyển tốt tấn công đến ô đích (trống).
     *   - Xóa tốt bị ăn khỏi ô cạnh (cùng hàng với tốt tấn công, cùng cột với ô đích).
     * <p>
     * Vị trí tốt bị ăn: (from.row, to.col) — luôn cùng hàng với tốt đang đứng,
     * không phải ô đích (to) mà tốt tấn công sẽ đến.
     */
    private MoveResult executeEnPassant(Move move, Tile fromTile, Tile toTile, Piece pawn) {
        Position capturedPawnPos = new Position(move.getFrom().getRow(), move.getTo().getCol());
        Tile capturedPawnTile    = board.getTile(capturedPawnPos);

        if (capturedPawnTile == null || !capturedPawnTile.isOccupied()) {
            return MoveResult.INVALID;
        }

        Piece capturedPawn = capturedPawnTile.getPiece();
        if (!(capturedPawn instanceof Pawn)) {
            return MoveResult.INVALID;
        }

        boolean wasMovedBefore = pawn.hasMoved();

        /* Di chuyển tốt tấn công */
        fromTile.clearTile();
        toTile.setPiece(pawn);
        pawn.setPosition(move.getTo());
        pawn.setMoved(true);

        /* Xóa tốt bị ăn */
        capturedPawnTile.clearTile();

        move.setCapturedPiece(capturedPawn);
        history.push(new MoveRecord(move, wasMovedBefore, capturedPawn, capturedPawnPos));

        return MoveResult.SUCCESS;
    }

    /**
     * Thực thi phong cấp (PROMOTION):
     *   1. Lưu lại con tốt gốc (để undo khôi phục đúng loại quân).
     *   2. Xóa tốt khỏi fromTile, đặt quân mới vào toTile.
     *   3. Ghi MoveRecord dạng PROMOTION vào history.
     * <p>
     * Nếu toTile đang có quân địch (tốt ăn chéo lên hàng cuối),
     * quân đó được lưu vào capturedPiece trong record để undo hoàn trả đúng.
     */
    private MoveResult executePromotion(Move move, Tile fromTile, Tile toTile, Piece movingPiece) {
        if (!(movingPiece instanceof Pawn pawn)) {
            return MoveResult.INVALID;
        }

        boolean wasMovedBefore = pawn.hasMoved();

        /* Quân bị ăn tại ô đích (có thể null nếu tiến thẳng) */
        Piece capturedPiece = null;
        if (toTile.isOccupied()) {
            capturedPiece = toTile.getPiece();
            move.setCapturedPiece(capturedPiece);
        }

        /* Tạo quân mới theo lựa chọn của người chơi */
        PromotionHandler promotionHandler = new PromotionHandler();
        Piece promotedPiece = promotionHandler.promote(pawn, move.getPromotionChoice());

        /* Cập nhật board */
        fromTile.clearTile();
        toTile.setPiece(promotedPiece);
        promotedPiece.setPosition(move.getTo());
        promotedPiece.setMoved(true);

        /* Ghi lại cả Pawn gốc và quân bị ăn để undo đúng */
        history.push(new MoveRecord(move, wasMovedBefore, pawn, capturedPiece, true));

        return MoveResult.SUCCESS;
    }

    /*
     * =========================
     * Undo Move
     * =========================
     */

    /**
     * Hoàn tác nước đi gần nhất, khôi phục board về trạng thái trước đó.
     * <p>
     * Thứ tự rollback:
     *   1. Pop MoveRecord từ history (null → không có gì để undo).
     *   2. Đưa quân di chuyển trở về fromTile.
     *   3. Khôi phục piece.position và piece.moved về giá trị cũ.
     *   4. Nếu có quân bị ăn, đặt lại vào toTile.
     *
     * @return MoveRecord của nước vừa undo, hoặc null nếu history trống.
     */
    public MoveRecord undoMove() {
        MoveRecord record = history.pop();

        if (record == null) {
            return null;
        }

        /*
         * Nhánh nhập thành: rollback cả King lẫn Rook.
         */
        if (record.isCastling()) {
            return undoCastling(record);
        }

        /*
         * Nhánh en passant: rollback tốt tấn công và phục hồi tốt bị ăn về đúng ô.
         */
        if (record.isEnPassant()) {
            return undoEnPassant(record);
        }

        /*
         * Nhánh phong cấp: xóa quân mới, đặt lại Pawn gốc về fromPosition.
         */
        if (record.isPromotion()) {
            return undoPromotion(record);
        }

        /*
         * Nước đi thông thường / capture.
         */
        Position from = record.getFromPosition();
        Position to   = record.getToPosition();

        Tile fromTile = board.getTile(from);
        Tile toTile   = board.getTile(to);

        if (fromTile == null || toTile == null) {
            return null;
        }

        Piece movingPiece = toTile.getPiece();

        if (movingPiece == null) {
            return null;
        }

        toTile.clearTile();
        fromTile.setPiece(movingPiece);
        movingPiece.setPosition(from);
        movingPiece.setMoved(record.wasMovedBefore());

        Piece capturedPiece = record.getCapturedPiece();
        if (capturedPiece != null) {
            toTile.setPiece(capturedPiece);
        }

        return record;
    }

    /**
     * Rollback en passant:
     *   - Đưa tốt tấn công về fromPosition.
     *   - Đặt lại tốt bị ăn vào enPassantCapturedPosition (không phải toPosition).
     */
    private MoveRecord undoEnPassant(MoveRecord record) {
        Position from = record.getFromPosition();
        Position to   = record.getToPosition();

        Tile fromTile = board.getTile(from);
        Tile toTile   = board.getTile(to);

        if (fromTile == null || toTile == null) { return null; }

        Piece pawn = toTile.getPiece();
        if (pawn == null) { return null; }

        /* Đưa tốt tấn công về vị trí ban đầu */
        toTile.clearTile();
        fromTile.setPiece(pawn);
        pawn.setPosition(from);
        pawn.setMoved(record.wasMovedBefore());

        /* Phục hồi tốt bị ăn về đúng ô — không phải toTile */
        Position capturedPos  = record.getEnPassantCapturedPosition();
        Piece capturedPawn    = record.getCapturedPiece();

        if (capturedPos != null && capturedPawn != null) {
            Tile capturedTile = board.getTile(capturedPos);
            if (capturedTile != null) {
                capturedTile.setPiece(capturedPawn);
                capturedPawn.setPosition(capturedPos);
            }
        }

        return record;
    }

    /**
     * Rollback nhập thành: đưa King và Rook về vị trí xuất phát,
     * khôi phục cờ hasMoved của cả hai.
     */
    private MoveRecord undoCastling(MoveRecord record) {
        /* Rollback King */
        Position kingFrom = record.getFromPosition();
        Position kingTo   = record.getToPosition();

        Tile kingFromTile = board.getTile(kingFrom);
        Tile kingToTile   = board.getTile(kingTo);

        if (kingFromTile == null || kingToTile == null) { return null; }

        Piece king = kingToTile.getPiece();
        if (king == null) { return null; }

        kingToTile.clearTile();
        kingFromTile.setPiece(king);
        king.setPosition(kingFrom);
        king.setMoved(record.wasMovedBefore());

        /* Rollback Rook */
        Position rookFrom = record.getRookFromPosition();
        Position rookTo   = record.getRookToPosition();
        Piece rook        = record.getRook();

        if (rookFrom == null || rookTo == null || rook == null) { return null; }

        Tile rookToTile   = board.getTile(rookTo);
        Tile rookFromTile = board.getTile(rookFrom);

        if (rookToTile == null || rookFromTile == null) { return null; }

        rookToTile.clearTile();
        rookFromTile.setPiece(rook);
        rook.setPosition(rookFrom);
        rook.setMoved(record.rookWasMovedBefore());

        return record;
    }

    /**
     * Rollback phong cấp:
     *   - Xóa quân mới (promoted piece) khỏi toTile.
     *   - Đặt lại Pawn gốc (record.getPromotedPawn()) về fromTile.
     *   - Nếu có quân bị ăn, hoàn trả về toTile.
     */
    private MoveRecord undoPromotion(MoveRecord record) {
        Position from = record.getFromPosition();
        Position to   = record.getToPosition();

        Tile fromTile = board.getTile(from);
        Tile toTile   = board.getTile(to);

        if (fromTile == null || toTile == null) { return null; }

        Piece originalPawn = record.getPromotedPawn();
        if (originalPawn == null) { return null; }

        /* Xóa quân mới vừa phong cấp */
        toTile.clearTile();

        /* Đặt lại Pawn gốc về ô xuất phát */
        fromTile.setPiece(originalPawn);
        originalPawn.setPosition(from);
        originalPawn.setMoved(record.wasMovedBefore());

        /* Hoàn trả quân địch bị ăn tại ô đích (nếu có) */
        Piece capturedPiece = record.getCapturedPiece();
        if (capturedPiece != null) {
            toTile.setPiece(capturedPiece);
        }

        return record;
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public MoveHistory getHistory() { return history; }

    /**
     * Shortcut: nước đi gần nhất trong history (null nếu chưa có nước nào).
     */
    public MoveRecord getLastMove()  { return history.getLastMove(); }
}
