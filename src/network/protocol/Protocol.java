package network.protocol;

public class Protocol {

    // Server
    public static final int    PORT            = 5000;
    public static final int    MAX_ROOMS       = 100;

    // Timeout (milliseconds)
    public static final int    PING_INTERVAL   = 5_000;   // gửi ping mỗi 5 giây
    public static final int    PING_TIMEOUT    = 15_000;  // mất kết nối sau 15 giây không pong
    public static final int    RECONNECT_TIMEOUT = 60_000; // chờ reconnect tối đa 60 giây

    // Version
    public static final String VERSION         = "1.0";

    private Protocol() {} // không cho tạo instance
}