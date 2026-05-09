package backend.move;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

/**
 * Lịch sử toàn bộ nước đi trong một ván cờ.
 * <p>
 * Sử dụng {@link ArrayDeque} làm stack: nước mới nhất ở đầu (peek/pop),
 * nước đầu tiên ở đáy. Mỗi nước đi được đại diện bởi một {@link MoveRecord}
 * bất biến chứa đủ thông tin để {@link MoveManager#undoMove()} rollback board.
 * <p>
 * Thiết kế:
 *   - push(record)  → thêm nước đi vừa thực thi
 *   - pop()         → lấy và xóa nước đi gần nhất (dùng khi undo)
 *   - peek()        → xem nước đi gần nhất mà không xóa
 *   - getHistory()  → toàn bộ lịch sử theo thứ tự từ đầu đến cuối (read-only)
 *   - getLastMove() → shortcut cho peek(), trả về null nếu trống
 *   - size()        → số nước đã đi
 *   - isEmpty()     → chưa có nước nào
 *   - clear()       → reset cho ván mới
 */
public class MoveHistory {

    private final Deque<MoveRecord> stack;

    public MoveHistory() {
        this.stack = new ArrayDeque<>();
    }

    /*
     * =========================
     * Push / Pop / Peek
     * =========================
     */

    /**
     * Ghi lại một nước đi vừa được thực thi lên board.
     * Gọi bởi {@link MoveManager#executeMove(Move)} ngay sau khi board đã cập nhật.
     */
    public void push(MoveRecord record) {
        if (record == null) { return; }
        stack.push(record);
    }

    /**
     * Lấy và xóa nước đi gần nhất.
     * Gọi bởi {@link MoveManager#undoMove()} để lấy thông tin rollback.
     *
     * @return MoveRecord của nước vừa bị undo, hoặc null nếu history trống.
     */
    public MoveRecord pop() {
        if (stack.isEmpty()) { return null; }
        return stack.pop();
    }

    /**
     * Xem nước đi gần nhất mà không xóa.
     *
     * @return MoveRecord gần nhất, hoặc null nếu history trống.
     */
    public MoveRecord peek() {
        if (stack.isEmpty()) { return null; }
        return stack.peek();
    }

    /*
     * =========================
     * Convenience
     * =========================
     */

    /**
     * Shortcut cho peek(). Trả về null nếu chưa có nước nào.
     */
    public MoveRecord getLastMove() {
        return peek();
    }

    /**
     * Toàn bộ lịch sử theo thứ tự từ nước đầu đến nước cuối (oldest → newest).
     * Trả về list read-only — không thể modify từ ngoài.
     */
    public List<MoveRecord> getHistory() {
        List<MoveRecord> ordered = new ArrayList<>(stack);
        Collections.reverse(ordered);
        return Collections.unmodifiableList(ordered);
    }

    /**
     * Số nước đã đi trong ván hiện tại.
     */
    public int size() {
        return stack.size();
    }

    /**
     * Chưa có nước nào được đi.
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Xóa toàn bộ lịch sử. Gọi khi bắt đầu ván mới.
     */
    public void clear() {
        stack.clear();
    }

    @Override
    public String toString() {
        return "MoveHistory{size=" + stack.size() + ", last=" + peek() + "}";
    }
}
