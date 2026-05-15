package network.packet;

import network.common.Message;
import network.common.MessageFactory;

public class SurrenderPacket {

    public final String playerName;

    public SurrenderPacket(String playerName) {
        this.playerName = playerName;
    }

    public static SurrenderPacket fromMessage(Message msg) {
        return new SurrenderPacket(msg.get("player"));
    }

    public Message toMessage() {
        return MessageFactory.resign();
    }

    @Override
    public String toString() {
        return playerName + " đã đầu hàng";
    }
}