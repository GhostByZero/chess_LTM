package util.constants;

public final class NetworkConstants {

    /*
     * =========================
     * Server
     * =========================
     */

    public static final String DEFAULT_HOST =
            "127.0.0.1";

    public static final int DEFAULT_PORT =
            5555;

    /*
     * =========================
     * Timeout
     * =========================
     */

    public static final int CONNECTION_TIMEOUT =
            10000;

    public static final int RECONNECT_TIMEOUT =
            30000;

    /*
     * =========================
     * Packet
     * =========================
     */

    public static final int MAX_PACKET_SIZE =
            4096;

    /*
     * =========================
     * Chat
     * =========================
     */

    public static final int MAX_CHAT_MESSAGE_LENGTH =
            200;

    /*
     * =========================
     * Ping
     * =========================
     */

    public static final int PING_INTERVAL =
            5000;

    /*
     * =========================
     * Constructor Protection
     * =========================
     */

    private NetworkConstants() {

    }
}