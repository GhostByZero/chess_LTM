package backend.move;

import backend.board.Position;
import backend.pieces.Piece;

/**
 * Snapshot bất biến của một nước đi đã được thực thi trên bàn cờ.
 * <p>
 * Lưu đủ thông tin để MoveHistory có thể hoàn tác (undo) nước đi đó, bao gồm:
 *   - Quân vừa di chuyển và vị trí xuất phát
 *   - Trạng thái hasMoved trước khi di chuyển (để restore chính xác)
 *   - Quân bị ăn (null nếu không có)
 *   - Loại nước đi (NORMAL, CAPTURE, CASTLING, EN_PASSANT, PROMOTION)
 *   - Thông tin Rook khi nhập thành (rookFrom, rookTo, rookWasMovedBefore)
 *   - Con tốt gốc khi phong cấp (promotedPawn) để undo đặt lại Pawn đúng loại
 */
public class MoveRecord {

    private final Move move;

    private final boolean wasMovedBefore;
    private final Position fromPosition;
    private final Position toPosition;
    private final Piece capturedPiece;

    /*
     * Thông tin Rook khi nhập thành (CASTLING).
     * Null nếu không phải nước nhập thành.
     */
    private final Piece rook;
    private final Position rookFromPosition;
    private final Position rookToPosition;
    private final boolean rookWasMovedBefore;

    /*
     * Vị trí của pawn bị ăn khi en passant (EN_PASSANT).
     * Khác toPosition: tốt bị ăn nằm cùng hàng với tốt tấn công,
     * không phải ô mà tốt tấn công đến.
     * Null nếu không phải en passant.
     */
    private final Position enPassantCapturedPosition;

    /*
     * Con tốt gốc trước khi phong cấp (PROMOTION).
     * Cần để undoMove() đặt lại đúng Pawn về fromPosition thay vì quân mới.
     * Null nếu không phải promotion.
     */
    private final Piece promotedPawn;

    /*
     * =========================
     * Constructor thông thường (NORMAL / CAPTURE)
     * =========================
     */

    public MoveRecord(Move move, boolean wasMovedBefore, Piece capturedPiece) {
        this.move                      = move;
        this.wasMovedBefore            = wasMovedBefore;
        this.fromPosition              = move.getFrom();
        this.toPosition                = move.getTo();
        this.capturedPiece             = capturedPiece;
        this.rook                      = null;
        this.rookFromPosition          = null;
        this.rookToPosition            = null;
        this.rookWasMovedBefore        = false;
        this.enPassantCapturedPosition = null;
        this.promotedPawn              = null;
    }

    /*
     * =========================
     * Constructor nhập thành (CASTLING)
     * =========================
     */

    public MoveRecord(Move move, boolean wasMovedBefore,
                      Piece rook, Position rookFrom, Position rookTo, boolean rookWasMovedBefore) {
        this.move                      = move;
        this.wasMovedBefore            = wasMovedBefore;
        this.fromPosition              = move.getFrom();
        this.toPosition                = move.getTo();
        this.capturedPiece             = null;
        this.rook                      = rook;
        this.rookFromPosition          = rookFrom;
        this.rookToPosition            = rookTo;
        this.rookWasMovedBefore        = rookWasMovedBefore;
        this.enPassantCapturedPosition = null;
        this.promotedPawn              = null;
    }

    /*
     * =========================
     * Constructor en passant (EN_PASSANT)
     * =========================
     */

    public MoveRecord(Move move, boolean wasMovedBefore,
                      Piece capturedPawn, Position enPassantCapturedPosition) {
        this.move                      = move;
        this.wasMovedBefore            = wasMovedBefore;
        this.fromPosition              = move.getFrom();
        this.toPosition                = move.getTo();
        this.capturedPiece             = capturedPawn;
        this.rook                      = null;
        this.rookFromPosition          = null;
        this.rookToPosition            = null;
        this.rookWasMovedBefore        = false;
        this.enPassantCapturedPosition = enPassantCapturedPosition;
        this.promotedPawn              = null;
    }

    /*
     * =========================
     * Constructor phong cấp (PROMOTION)
     *
     * promotedPawn  — con tốt gốc (trước khi đổi thành quân mới)
     * capturedPiece — quân địch bị ăn ở toPosition (null nếu không có)
     * =========================
     */

    public MoveRecord(Move move, boolean wasMovedBefore,
                      Piece promotedPawn, Piece capturedPiece, boolean isPromotion) {
        this.move                      = move;
        this.wasMovedBefore            = wasMovedBefore;
        this.fromPosition              = move.getFrom();
        this.toPosition                = move.getTo();
        this.capturedPiece             = capturedPiece;
        this.rook                      = null;
        this.rookFromPosition          = null;
        this.rookToPosition            = null;
        this.rookWasMovedBefore        = false;
        this.enPassantCapturedPosition = null;
        this.promotedPawn              = promotedPawn;
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public Move getMove()                  { return move;           }
    public boolean wasMovedBefore()        { return wasMovedBefore; }
    public Position getFromPosition()      { return fromPosition;   }
    public Position getToPosition()        { return toPosition;     }
    public Piece getCapturedPiece()        { return capturedPiece;  }
    public MoveType getMoveType()          { return move.getMoveType(); }

    public boolean isCastling()            { return move.getMoveType() == MoveType.CASTLING;   }
    public boolean isEnPassant()           { return move.getMoveType() == MoveType.EN_PASSANT; }
    public boolean isPromotion()           { return move.getMoveType() == MoveType.PROMOTION;  }
    public Piece getRook()                 { return rook;              }
    public Position getRookFromPosition()  { return rookFromPosition;  }
    public Position getRookToPosition()    { return rookToPosition;    }
    public boolean rookWasMovedBefore()    { return rookWasMovedBefore; }
    public Position getEnPassantCapturedPosition() { return enPassantCapturedPosition; }
    public Piece getPromotedPawn()         { return promotedPawn; }

    @Override
    public String toString() {
        return "MoveRecord{" + move + ", wasMovedBefore=" + wasMovedBefore
                + ", captured=" + (capturedPiece != null ? capturedPiece.getSymbol() : "none")
                + (promotedPawn != null ? ", promotion" : "") + "}";
    }
}
