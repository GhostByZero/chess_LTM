package network.server;

import network.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private static RoomManager instance;

    private final List<MatchRoom>    activeRooms  = new ArrayList<>();
    private       SessionManager     waitingClient = null;

    private RoomManager() {}

    public static synchronized RoomManager getInstance() {
        if (instance == null) instance = new RoomManager();
        return instance;
    }

    public synchronized void matchmaking(SessionManager newClient) {

        // 1. KIỂM TRA RECONNECT TRƯỚC: Nếu đang ở phòng cũ bị đứt mạng thì vào lại luôn
        for (MatchRoom room : activeRooms) {
            if (room.hasPlayer(newClient.getPlayerName())) {
                newClient.setRoom(room);
                room.handleReconnect(newClient);
                return;
            }
        }

        // 2. NẾU KHÔNG CÓ PHÒNG CŨ -> TIẾN HÀNH GHÉP CẶP MỚI
        if (activeRooms.size() >= Protocol.MAX_ROOMS) {
            System.out.println("[RoomManager] Đã đầy phòng!");
            return;
        }

        if (waitingClient == null) {
            waitingClient = newClient;
            System.out.println("[RoomManager] "
                    + newClient.getPlayerName() + " đang chờ đối thủ...");
        } else {
            MatchRoom room = new MatchRoom(waitingClient, newClient);
            activeRooms.add(room);
            room.startGame();
            waitingClient = null;
            System.out.println("[RoomManager] Phòng mới! Tổng: "
                    + activeRooms.size());
        }
    }

    public synchronized void removeRoom(MatchRoom room) {
        activeRooms.remove(room);
        System.out.println("[RoomManager] Phòng đã xóa. Còn: "
                + activeRooms.size());
    }

    public synchronized void cancelWaiting(SessionManager client) {
        if (waitingClient == client) {
            waitingClient = null;
            System.out.println("[RoomManager] Hủy chờ: "
                    + client.getPlayerName());
        }
    }

    public synchronized int getRoomCount()  { return activeRooms.size(); }
    public synchronized boolean isFull()    { return activeRooms.size() >= Protocol.MAX_ROOMS; }
}