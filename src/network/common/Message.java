package network.common;

import org.json.JSONObject;

public class Message {
    private MessageType type;
    private JSONObject data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new JSONObject();
    }

    public Message put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String get(String key) {
        return data.optString(key, "");
    }

    public int getInt(String key) {
        return data.optInt(key, -1);
    }

    public boolean getBoolean(String key) {
        return data.optBoolean(key, false);
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("type", type.name());
        obj.put("data", data);
        return obj.toString();
    }

    public static Message fromJson(String jsonStr) {
        JSONObject obj   = new JSONObject(jsonStr);
        MessageType type = MessageType.valueOf(obj.getString("type"));
        Message msg      = new Message(type);
        msg.data         = obj.optJSONObject("data");
        if (msg.data == null) msg.data = new JSONObject();
        return msg;
    }

    public MessageType getType() { return type; }

    @Override
    public String toString() { return toJson(); }
}