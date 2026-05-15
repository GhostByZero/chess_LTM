package network.packet;

import network.common.Message;
import network.common.MessageFactory;

public class DisconnectPacket {

    public final String playerName;
    public final String reason;

    public DisconnectPacket(String playerName, String reason) {
        this.playerName = playerName;
        this.reason     = reason;
    }

    public static DisconnectPacket fromMessage(Message msg) {
        return new DisconnectPacket(
                msg.get("player"),
                msg.get("reason")
        );
    }

    public Message toMessage() {
        return MessageFactory.disconnect(playerName);
    }

    @Override
    public String toString() {
        return playerName + " disconnected: " + reason;
    }
}