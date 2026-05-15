package network.packet;

import network.common.Message;
import network.common.MessageFactory;

// Helper tạo MOVE message từ tọa độ thô
public class MovePacket {

    public final int    fromRow;
    public final int    fromCol;
    public final int    toRow;
    public final int    toCol;
    public final String piece;
    public final String moveType;
    public final String promotion;

    public MovePacket(int fromRow, int fromCol,
                      int toRow,   int toCol,
                      String piece, String moveType, String promotion) {
        this.fromRow   = fromRow;
        this.fromCol   = fromCol;
        this.toRow     = toRow;
        this.toCol     = toCol;
        this.piece     = piece;
        this.moveType  = moveType;
        this.promotion = (promotion != null) ? promotion : "QUEEN";
    }

    // Tạo từ Message nhận được
    public static MovePacket fromMessage(Message msg) {
        return new MovePacket(
                msg.getInt("fromRow"), msg.getInt("fromCol"),
                msg.getInt("toRow"),   msg.getInt("toCol"),
                msg.get("piece"),      msg.get("moveType"),
                msg.get("promotion")
        );
    }

    // Chuyển thành Message để gửi
    public Message toMessage() {
        return MessageFactory.move(
                fromRow, fromCol, toRow, toCol,
                piece, moveType, promotion);
    }

    @Override
    public String toString() {
        return piece + ": (" + fromRow + "," + fromCol + ")"
                + " → (" + toRow + "," + toCol + ")"
                + (promotion.equals("QUEEN") ? "" : " promote=" + promotion);
    }
}