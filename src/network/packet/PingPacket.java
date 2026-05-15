package network.packet;

import network.common.Message;
import network.common.MessageFactory;

public class PingPacket {

    public final long timestamp;

    public PingPacket() {
        this.timestamp = System.currentTimeMillis();
    }

    public PingPacket(long timestamp) {
        this.timestamp = timestamp;
    }

    public static PingPacket fromMessage(Message msg) {
        return new PingPacket(msg.getInt("timestamp"));
    }

    public Message toPingMessage() {
        return MessageFactory.ping();
    }

    public Message toPongMessage() {
        return MessageFactory.pong(timestamp);
    }

    // Tính độ trễ (ms)
    public long getLatency() {
        return System.currentTimeMillis() - timestamp;
    }
}