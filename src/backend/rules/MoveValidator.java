package backend.rules;

import backend.board.Board;
import backend.board.Position;
import backend.board.Tile;
import backend.move.Move;
import backend.move.MoveHistory;
import backend.move.MoveManager;
import backend.move.MoveRecord;
import backend.move.MoveResult;
import backend.move.MoveType;
import backend.pieces.*;

/**
 * Hai tầng validation cho nước đi:
 * <p>
 * 1. {@link #isValidMove(Move)} — kiểm tra hình học thuần túy:
 *    quân đi đúng kiểu không? Đường đi thông không? Không ăn quân cùng màu?
 *    Không thực thi lên board. Dùng nội bộ bởi CheckDetector khi quét
 *    tất cả quân địch (tránh vòng lặp vô hạn).
 * <p>
 * 2. {@link #isMoveLegal(Move)} — kiểm tra hoàn chỉnh theo luật cờ vua:
 *    isValidMove() CỘNG với "nước đi này có để vua của mình bị chiếu không?".
 *    Dùng simulate (executeMove → checkKing → undoMove) nên cần MoveManager.
 *    Đây là method phải dùng ở mọi nơi quyết định nước đi có hợp lệ không.
 */
public class MoveValidator {

    private final Board board;
    private MoveManager moveManager;
    private MoveHistory moveHistory;
    private CheckDetector checkDetector;
    private CastlingValidator castlingValidator;

    /*
     * =========================
     * Constructors
     * =========================
     */

    public MoveValidator(Board board, MoveManager moveManager) {
        this.board             = board;
        this.moveManager       = moveManager;
        this.moveHistory       = moveManager.getHistory();
        this.checkDetector     = new CheckDetector(board);
        this.castlingValidator = new CastlingValidator(board);
    }

    public MoveValidator(Board board) {
        this.board             = board;
        this.moveManager       = null;
        this.moveHistory       = null;
        this.checkDetector     = null;
        this.castlingValidator = null;
    }

    /*
     * =========================
     * Tầng 1 — Geometric validation
     * =========================
     */

    /**
     * Kiểm tra hình học thuần túy: quân đi đúng kiểu, đường đi thông,
     * không ăn quân cùng màu. Không kiểm tra tự chiếu.
     * <p>
     * Chỉ dùng nội bộ (CheckDetector, simulate trong isMoveLegal).
     * Code bên ngoài nên gọi {@link #isMoveLegal(Move)}.
     */
    public boolean isValidMove(Move move) {
        if (move == null) { return false; }

        Position from = move.getFrom();
        Position to   = move.getTo();

        if (!board.isPositionValid(from) || !board.isPositionValid(to)) {
            return false;
        }

        Tile fromTile = board.getTile(from);
        Tile toTile   = board.getTile(to);

        if (fromTile == null || !fromTile.isOccupied()) {
            return false;
        }

        Piece movingPiece = fromTile.getPiece();

        if (movingPiece == null) {
            return false;
        }

        /*
         * Không được ăn quân cùng màu.
         */
        if (toTile.isOccupied()) {
            if (toTile.getPiece().getColor() == movingPiece.getColor()) {
                return false;
            }
        }

        /*
         * Dispatch validation theo từng loại quân.
         */
        return switch (movingPiece) {
            case Pawn   pawn   -> validatePawnMove(pawn, from, to, move);
            case Rook   rook   -> validateRookMove(from, to);
            case Bishop bishop -> validateBishopMove(from, to);
            case Knight knight -> validateKnightMove(from, to);
            case Queen  queen  -> validateQueenMove(from, to);
            case King   king   -> validateKingMove(king, from, to, move);
            default            -> false;
        };
    }

    /*
     * =========================
     * Tầng 2 — Legal move (full chess rules)
     * =========================
     */

    /**
     * Kiểm tra nước đi hợp lệ theo luật cờ vua đầy đủ:
     * hình học đúng VÀ không để vua của mình bị chiếu sau nước đi.
     * <p>
     * Cơ chế: simulate nước đi lên board (executeMove) →
     * kiểm tra vua của bên vừa đi có bị chiếu không →
     * rollback (undoMove) dù kết quả thế nào.
     * <p>
     * Nếu MoveManager chưa được inject (constructor 1 argument),
     * fallback về isValidMove() — hành vi cũ, dùng bởi CheckDetector.
     */
    public boolean isMoveLegal(Move move) {
        if (!isValidMove(move)) {
            return false;
        }

        /*
         * Fallback nếu không có MoveManager (dùng bởi CheckDetector nội bộ).
         */
        if (moveManager == null || checkDetector == null) {
            return true;
        }

        Tile fromTile    = board.getTile(move.getFrom());
        PieceColor color = fromTile.getPiece().getColor();

        /*
         * Simulate: thực thi nước đi lên board thật.
         */
        MoveResult result = moveManager.executeMove(move);

        if (result != MoveResult.SUCCESS) {
            return false;
        }

        /*
         * Kiểm tra vua của bên vừa đi có bị chiếu không.
         */
        boolean leavesKingInCheck = checkDetector.isKingInCheck(color);

        /*
         * Rollback vô điều kiện — board phải về trạng thái cũ.
         */
        moveManager.undoMove();

        return !leavesKingInCheck;
    }

    /*
     * =========================
     * Pawn
     * =========================
     */

    private boolean validatePawnMove(Pawn pawn, Position from, Position to, Move move) {
        int direction = pawn.getColor() == PieceColor.WHITE ? -1 : 1;
        int rowDiff   = to.getRow() - from.getRow();
        int colDiff   = to.getCol() - from.getCol();

        Tile destinationTile = board.getTile(to);

        /*
         * Tiến thẳng 1 ô (có thể đến hàng phong cấp).
         */
        if (colDiff == 0 && rowDiff == direction && !destinationTile.isOccupied()) {
            if (isPromotionRow(pawn, to)) {
                move.setMoveType(MoveType.PROMOTION);
            }
            return true;
        }

        /*
         * Tiến thẳng 2 ô từ vị trí ban đầu.
         */
        if (colDiff == 0 && !pawn.hasMoved() && rowDiff == direction * 2
                && !destinationTile.isOccupied()) {
            Position middlePos = new Position(from.getRow() + direction, from.getCol());
            Tile middleTile    = board.getTile(middlePos);
            return middleTile != null && !middleTile.isOccupied();
        }

        /*
         * Ăn chéo thông thường (có thể đến hàng phong cấp).
         */
        if (Math.abs(colDiff) == 1 && rowDiff == direction && destinationTile.isOccupied()) {
            if (destinationTile.getPiece().getColor() != pawn.getColor()) {
                if (isPromotionRow(pawn, to)) {
                    move.setMoveType(MoveType.PROMOTION);
                }
                return true;
            }
            return false;
        }

        /*
         * En passant:
         *   - Tốt di chuyển chéo 1 ô, nhưng ô đích trống (không phải capture thông thường)
         *   - Nước trước của đối phương phải là tốt tiến 2 ô, dừng đúng cạnh tốt này
         *   - moveHistory chỉ có trong constructor đầy đủ; nếu null thì bỏ qua
         */
        if (Math.abs(colDiff) == 1 && rowDiff == direction && !destinationTile.isOccupied()
                && moveHistory != null) {

            MoveRecord lastMove = moveHistory.getLastMove();
            if (isEnPassantTarget(lastMove, pawn, from, to)) {
                move.setMoveType(MoveType.EN_PASSANT);
                return true;
            }
        }

        return false;
    }

    /**
     * Kiểm tra nước trước có phải là tốt đối phương tiến 2 ô,
     * dừng đúng cạnh tốt hiện tại, và ô "qua" khớp với ô đích.
     */
    private boolean isEnPassantTarget(MoveRecord lastMove, Pawn pawn,
                                      Position from, Position to) {
        if (lastMove == null) { return false; }

        Piece lastMoved = lastMove.getMove().getMovedPiece();

        /* Nước trước phải là Pawn đối phương */
        if (!(lastMoved instanceof Pawn)) { return false; }
        if (lastMoved.getColor() == pawn.getColor()) { return false; }

        Position lastFrom = lastMove.getFromPosition();
        Position lastTo   = lastMove.getToPosition();

        /* Nước trước phải là tiến 2 ô */
        if (Math.abs(lastTo.getRow() - lastFrom.getRow()) != 2) { return false; }

        /* Tốt đối phương phải đang đứng ngay cạnh (cùng hàng, cạnh cột) */
        if (lastTo.getRow() != from.getRow()) { return false; }

        return lastTo.getCol() == to.getCol();
    }

    /**
     * Kiểm tra ô đích có phải hàng phong cấp của tốt không.
     * WHITE đến row 0, BLACK đến row 7.
     */
    private boolean isPromotionRow(Pawn pawn, Position to) {
        if (pawn.getColor() == PieceColor.WHITE) { return to.getRow() == 0; }
        return to.getRow() == 7;
    }

    /*
     * =========================
     * Rook
     * =========================
     */

    private boolean validateRookMove(Position from, Position to) {
        if (from.getRow() != to.getRow() && from.getCol() != to.getCol()) {
            return false;
        }
        return isPathClear(from, to);
    }

    /*
     * =========================
     * Bishop
     * =========================
     */

    private boolean validateBishopMove(Position from, Position to) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        if (rowDiff != colDiff) { return false; }
        return isPathClear(from, to);
    }

    /*
     * =========================
     * Knight
     * =========================
     */

    private boolean validateKnightMove(Position from, Position to) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    /*
     * =========================
     * Queen
     * =========================
     */

    private boolean validateQueenMove(Position from, Position to) {
        return validateRookMove(from, to) || validateBishopMove(from, to);
    }

    /*
     * =========================
     * King
     * =========================
     */

    /**
     * Kiểm tra nước đi của King:
     *   - Di chuyển 1 ô thông thường: hình học ±1
     *   - Nhập thành: King đi 2 ô sang trái/phải, gọi CastlingValidator
     * <p>
     * Kiểm tra ô đích có bị tấn công không do isMoveLegal() xử lý (simulate + checkDetector).
     */
    private boolean validateKingMove(King king, Position from, Position to, Move move) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = to.getCol() - from.getCol();

        /* Nước đi thông thường: ±1 theo mọi hướng */
        if (rowDiff <= 1 && Math.abs(colDiff) <= 1 && (rowDiff + Math.abs(colDiff) > 0)) {
            return true;
        }

        /* Nhập thành: cùng hàng, vua đi đúng 2 cột sang phải (kingside) hoặc trái (queenside) */
        if (rowDiff == 0 && castlingValidator != null) {
            PieceColor color = king.getColor();
            if (colDiff == 2) {
                if (castlingValidator.canCastleKingSide(color)) {
                    move.setMoveType(MoveType.CASTLING);
                    return true;
                }
            } else if (colDiff == -2) {
                if (castlingValidator.canCastleQueenSide(color)) {
                    move.setMoveType(MoveType.CASTLING);
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * =========================
     * Path checking
     * =========================
     */

    private boolean isPathClear(Position from, Position to) {
        int rowDir = Integer.compare(to.getRow(), from.getRow());
        int colDir = Integer.compare(to.getCol(), from.getCol());

        int r = from.getRow() + rowDir;
        int c = from.getCol() + colDir;

        while (r != to.getRow() || c != to.getCol()) {
            Tile tile = board.getTile(new Position(r, c));
            if (tile != null && tile.isOccupied()) { return false; }
            r += rowDir;
            c += colDir;
        }
        return true;
    }
}
