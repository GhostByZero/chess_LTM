package backend.move;

/**
 * Kết quả trả về từ {@link backend.engine.GameManager#processMove(Move)}.
 *
 * <ul>
 *   <li>{@code SUCCESS}   — nước đi hợp lệ, ván tiếp tục bình thường.</li>
 *   <li>{@code CHECK}     — nước đi hợp lệ, bên đối phương đang bị chiếu.</li>
 *   <li>{@code CHECKMATE} — nước đi hợp lệ, bên đối phương bị chiếu hết → thắng.</li>
 *   <li>{@code STALEMATE} — nước đi hợp lệ, bên đối phương hết nước → hòa.</li>
 *   <li>{@code DRAW}      — nước đi hợp lệ, kích hoạt hòa theo luật
 *                           (50-move rule hoặc threefold repetition).</li>
 *   <li>{@code INVALID}   — nước đi không hợp lệ, board không thay đổi.</li>
 * </ul>
 *
 * Thứ tự ưu tiên khi kết hợp: CHECKMATE > STALEMATE > DRAW > CHECK > SUCCESS > INVALID.
 */
public enum MoveResult {
    SUCCESS,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW,
    INVALID
}