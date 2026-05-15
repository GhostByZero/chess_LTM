package network.packet;

import network.common.Message;
import network.common.MessageFactory;

public class ChatPacket {

    public final String sender;
    public final String content;

    public ChatPacket(String sender, String content) {
        this.sender  = sender;
        this.content = content;
    }

    public static ChatPacket fromMessage(Message msg) {
        return new ChatPacket(msg.get("sender"), msg.get("content"));
    }

    public Message toMessage() {
        return MessageFactory.chat(sender, content);
    }

    @Override
    public String toString() {
        return "[" + sender + "]: " + content;
    }
}